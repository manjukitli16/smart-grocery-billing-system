package com.freshmart.controller;

import com.freshmart.service.FreshMartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final FreshMartService store;

    public DashboardController(FreshMartService store) {
        this.store = store;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("todayTotal", store.todaySales());
        model.addAttribute("todayCount", store.todayBillCount());
        model.addAttribute("productCount", store.productCount());
        model.addAttribute("lowStock", store.lowStockProducts());
        model.addAttribute("recentBills", store.recentBills());
        return "dashboard";
    }
}