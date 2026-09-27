package com.freshmart.controller;

import com.freshmart.service.FreshMartService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BillingController {
    private final FreshMartService store;

    public BillingController(FreshMartService store) {
        this.store = store;
    }

    @GetMapping("/billing")
    public String checkout(Model model) {
        model.addAttribute("products", store.products(true));
        return "billing";
    }

    @PostMapping("/billing")
    public String createBill(
            @RequestParam(name = "product_id", required = false) List<String> productIds,
            @RequestParam(name = "quantity", required = false) List<String> quantities,
            @RequestParam(defaultValue = "") String customer,
            Model model) {
        try {
            long billId = store.createBill(productIds, quantities, customer);
            return "redirect:/billing/" + billId;
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("products", store.products(true));
            model.addAttribute("customer", customer);
            return "billing";
        }
    }

    @GetMapping("/billing/{id}")
    public String receipt(@PathVariable long id, Model model,
                          RedirectAttributes redirectAttributes) {
        return store.bill(id)
                .map(details -> {
                    model.addAttribute("details", details);
                    return "bill";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Receipt not found.");
                    return "redirect:/sales";
                });
    }
}