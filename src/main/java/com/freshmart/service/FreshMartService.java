package com.freshmart.service;

import com.freshmart.model.Bill;
import com.freshmart.model.BillDetails;
import com.freshmart.model.BillItem;
import com.freshmart.model.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FreshMartService {
    private static final int LOW_STOCK_THRESHOLD = 5;

    private static final RowMapper<Product> PRODUCT_MAPPER = (row, index) -> new Product(
            row.getLong("id"),
            row.getString("name"),
            row.getString("category"),
            money(row, "price"),
            row.getInt("stock"),
            row.getString("created_at"));

    private static final RowMapper<Bill> BILL_MAPPER = (row, index) -> new Bill(
            row.getLong("id"),
            row.getString("customer"),
            money(row, "total"),
            row.getString("created_at"));

    private static final RowMapper<BillItem> BILL_ITEM_MAPPER = (row, index) -> {
        long productId = row.getLong("product_id");
        Long nullableProductId = row.wasNull() ? null : productId;
        return new BillItem(
                row.getLong("id"),
                row.getLong("bill_id"),
                nullableProductId,
                row.getString("product_name"),
                row.getInt("quantity"),
                money(row, "unit_price"),
                money(row, "line_total"));
    };

    private final JdbcTemplate jdbc;

    public FreshMartService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static BigDecimal money(ResultSet row, String column) throws SQLException {
        BigDecimal value = row.getBigDecimal(column);
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    public List<Product> products(boolean inStockOnly) {
        String sql = "SELECT * FROM products" + (inStockOnly ? " WHERE stock > 0" : "")
                + " ORDER BY name COLLATE NOCASE";
        return jdbc.query(sql, PRODUCT_MAPPER);
    }

    public Optional<Product> product(long id) {
        return jdbc.query("SELECT * FROM products WHERE id = ?", PRODUCT_MAPPER, id)
                .stream()
                .findFirst();
    }

    public long addProduct(Map<String, String> fields) {
        ProductInput product = validateProduct(fields);
        Long id = jdbc.queryForObject(
                "INSERT INTO products (name, category, price, stock) VALUES (?, ?, ?, ?) RETURNING id",
                Long.class,
                product.name(),
                product.category(),
                product.price(),
                product.stock());
        return id;
    }

    public boolean updateProduct(long id, Map<String, String> fields) {
        ProductInput product = validateProduct(fields);
        return jdbc.update(
                "UPDATE products SET name = ?, category = ?, price = ?, stock = ? WHERE id = ?",
                product.name(),
                product.category(),
                product.price(),
                product.stock(),
                id) == 1;
    }

    public boolean deleteProduct(long id) {
        return jdbc.update("DELETE FROM products WHERE id = ?", id) == 1;
    }

    private ProductInput validateProduct(Map<String, String> fields) {
        String name = fields.getOrDefault("name", "").trim();
        String category = fields.getOrDefault("category", "").trim();
        if (name.isEmpty() || name.length() > 120) {
            throw new IllegalArgumentException("Enter a product name under 120 characters.");
        }
        if (category.length() > 80) {
            throw new IllegalArgumentException("Keep the category under 80 characters.");
        }

        BigDecimal price;
        int stock;
        try {
            price = new BigDecimal(fields.getOrDefault("price", "").trim())
                    .setScale(2, RoundingMode.HALF_UP);
            stock = Integer.parseInt(fields.getOrDefault("stock", "").trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid price and whole-number stock quantity.");
        }
        if (price.signum() < 0 || stock < 0) {
            throw new IllegalArgumentException("Price and stock cannot be negative.");
        }
        return new ProductInput(name, category, price, stock);
    }

    public BigDecimal todaySales() {
        BigDecimal total = jdbc.queryForObject(
                "SELECT COALESCE(SUM(total), 0) FROM bills "
                        + "WHERE date(created_at) = date('now', 'localtime')",
                BigDecimal.class);
        return total == null ? BigDecimal.ZERO.setScale(2) : total.setScale(2, RoundingMode.HALF_UP);
    }

    public int todayBillCount() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM bills WHERE date(created_at) = date('now', 'localtime')",
                Integer.class);
        return count == null ? 0 : count;
    }

    public int productCount() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
        return count == null ? 0 : count;
    }

    public List<Product> lowStockProducts() {
        return jdbc.query(
                "SELECT * FROM products WHERE stock <= ? ORDER BY stock, name COLLATE NOCASE",
                PRODUCT_MAPPER,
                LOW_STOCK_THRESHOLD);
    }

    public List<Bill> recentBills() {
        return jdbc.query("SELECT * FROM bills ORDER BY id DESC LIMIT 6", BILL_MAPPER);
    }

    public List<Bill> salesHistory() {
        return jdbc.query("SELECT * FROM bills ORDER BY id DESC", BILL_MAPPER);
    }

    @Transactional
    public long createBill(List<String> productIds, List<String> quantities, String customer) {
        if (productIds == null || quantities == null || productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Add at least one product to the bill.");
        }

        Map<Long, Integer> quantitiesByProduct = new LinkedHashMap<>();
        for (int index = 0; index < productIds.size(); index++) {
            try {
                long productId = Long.parseLong(productIds.get(index));
                int quantity = Integer.parseInt(quantities.get(index));
                if (productId < 1 || quantity < 1) {
                    throw new IllegalArgumentException("Quantities must be at least one.");
                }
                quantitiesByProduct.merge(productId, quantity, Integer::sum);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Choose a product and enter a whole-number quantity.");
            }
        }
        if (quantitiesByProduct.isEmpty()) {
            throw new IllegalArgumentException("Add at least one product to the bill.");
        }

        List<BillLine> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (Map.Entry<Long, Integer> entry : quantitiesByProduct.entrySet()) {
            Product product = product(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("A selected product no longer exists."));
            if (product.stock() < entry.getValue()) {
                throw new IllegalArgumentException("Not enough stock for " + product.name() + ".");
            }
            BigDecimal lineTotal = product.price().multiply(BigDecimal.valueOf(entry.getValue()))
                    .setScale(2, RoundingMode.HALF_UP);
            total = total.add(lineTotal);
            lines.add(new BillLine(product, entry.getValue(), lineTotal));
        }

        String customerName = customer == null ? "" : customer.trim();
        if (customerName.isEmpty()) {
            customerName = "Walk-in customer";
        }
        if (customerName.length() > 120) {
            throw new IllegalArgumentException("Customer name must be 120 characters or fewer.");
        }

        Long billId = jdbc.queryForObject(
                "INSERT INTO bills (customer, total) VALUES (?, ?) RETURNING id",
                Long.class,
                customerName,
                total);
        for (BillLine line : lines) {
            jdbc.update(
                    "INSERT INTO bill_items "
                            + "(bill_id, product_id, product_name, quantity, unit_price, line_total) "
                            + "VALUES (?, ?, ?, ?, ?, ?)",
                    billId,
                    line.product().id(),
                    line.product().name(),
                    line.quantity(),
                    line.product().price(),
                    line.lineTotal());
            int updated = jdbc.update(
                    "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?",
                    line.quantity(),
                    line.product().id(),
                    line.quantity());
            if (updated != 1) {
                throw new IllegalArgumentException("Stock changed during checkout. Review the order and try again.");
            }
        }
        return billId;
    }

    public Optional<BillDetails> bill(long id) {
        List<Bill> bills = jdbc.query("SELECT * FROM bills WHERE id = ?", BILL_MAPPER, id);
        if (bills.isEmpty()) {
            return Optional.empty();
        }
        List<BillItem> items = jdbc.query(
                "SELECT * FROM bill_items WHERE bill_id = ? ORDER BY id",
                BILL_ITEM_MAPPER,
                id);
        return Optional.of(new BillDetails(bills.get(0), items));
    }

    private record ProductInput(String name, String category, BigDecimal price, int stock) {
    }

    private record BillLine(Product product, int quantity, BigDecimal lineTotal) {
    }
}