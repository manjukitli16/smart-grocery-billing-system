package com.freshmart.controller;

import com.freshmart.service.FreshMartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SalesController {
    private final FreshMartService store;

    public SalesController(FreshMartService store) {
        this.store = store;
    }

    @GetMapping("/sales")
    public String sales(Model model) {
        model.addAttribute("bills", store.salesHistory());
        return "sales";
    }
}