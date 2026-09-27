package com.freshmart.model;

import java.math.BigDecimal;

public record Product(
        long id,
        String name,
        String category,
        BigDecimal price,
        int stock,
        String createdAt) {
        public long getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public BigDecimal getPrice() { return price; }
        public int getStock() { return stock; }
        public String getCreatedAt() { return createdAt; }
}