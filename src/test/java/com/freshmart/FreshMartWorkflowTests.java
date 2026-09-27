package com.freshmart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.freshmart.model.BillDetails;
import com.freshmart.service.FreshMartService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:file:freshmart-test?mode=memory&cache=shared",
        "spring.datasource.hikari.maximum-pool-size=1"
})
class FreshMartWorkflowTests {
    @Autowired
    private FreshMartService store;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void clearDatabase() {
        jdbc.update("DELETE FROM bill_items");
        jdbc.update("DELETE FROM bills");
        jdbc.update("DELETE FROM products");
    }

    @Test
    void checkoutSavesReceiptAndDeductsStock() {
        long productId = store.addProduct(Map.of(
                "name", "Milk",
                "category", "Dairy",
                "price", "3.25",
                "stock", "5"));

        long billId = store.createBill(List.of(Long.toString(productId)), List.of("2"), "Asha");
        BillDetails receipt = store.bill(billId).orElseThrow();

        assertEquals("Asha", receipt.bill().customer());
        assertEquals(new BigDecimal("6.50"), receipt.bill().total());
        assertEquals(3, store.product(productId).orElseThrow().stock());
    }

    @Test
    void insufficientStockRollsBackCheckout() {
        long productId = store.addProduct(Map.of(
                "name", "Eggs",
                "category", "Dairy",
                "price", "4.00",
                "stock", "1"));

        assertThrows(IllegalArgumentException.class,
                () -> store.createBill(List.of(Long.toString(productId)), List.of("2"), ""));

        assertEquals(1, store.product(productId).orElseThrow().stock());
        assertEquals(0, store.salesHistory().size());
    }
}