package com.freshmart.model;

import java.math.BigDecimal;

public record BillItem(
        long id,
        long billId,
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal) {
        public long getId() { return id; }
        public long getBillId() { return billId; }
        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getLineTotal() { return lineTotal; }
}