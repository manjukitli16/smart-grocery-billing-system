package com.freshmart.model;

import java.math.BigDecimal;

public record Bill(long id, String customer, BigDecimal total, String createdAt) {
	public long getId() { return id; }
	public String getCustomer() { return customer; }
	public BigDecimal getTotal() { return total; }
	public String getCreatedAt() { return createdAt; }
}