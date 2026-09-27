package com.freshmart.model;

import java.util.List;

public record BillDetails(Bill bill, List<BillItem> items) {
	public Bill getBill() { return bill; }
	public List<BillItem> getItems() { return items; }
}