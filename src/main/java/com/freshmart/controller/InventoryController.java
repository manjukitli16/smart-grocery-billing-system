package com.freshmart.controller;

import com.freshmart.service.FreshMartService;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InventoryController {
    private final FreshMartService store;

    public InventoryController(FreshMartService store) {
        this.store = store;
    }

    @GetMapping("/inventory")
    public String inventory(Model model) {
        model.addAttribute("products", store.products(false));
        return "inventory";
    }

    @GetMapping("/inventory/add")
    public String addForm() {
        return "add_product";
    }

    @PostMapping("/inventory/add")
    public String add(@RequestParam Map<String, String> fields, Model model,
                      RedirectAttributes redirectAttributes) {
        try {
            store.addProduct(fields);
            redirectAttributes.addFlashAttribute("success", "Product added to inventory.");
            return "redirect:/inventory";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("error", "A product with that name already exists.");
        }
        model.addAttribute("form", fields);
        return "add_product";
    }

    @GetMapping("/inventory/{id}/edit")
    public String editForm(@PathVariable long id, Model model,
                           RedirectAttributes redirectAttributes) {
        return store.product(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "edit_product";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found.");
                    return "redirect:/inventory";
                });
    }

    @PostMapping("/inventory/{id}/edit")
    public String edit(@PathVariable long id, @RequestParam Map<String, String> fields,
                       Model model, RedirectAttributes redirectAttributes) {
        var product = store.product(id);
        if (product.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Product not found.");
            return "redirect:/inventory";
        }
        try {
            store.updateProduct(id, fields);
            redirectAttributes.addFlashAttribute("success", "Inventory updated.");
            return "redirect:/inventory";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("error", "A product with that name already exists.");
        }
        model.addAttribute("product", product.get());
        model.addAttribute("form", fields);
        return "edit_product";
    }

    @PostMapping("/inventory/{id}/delete")
    public String delete(@PathVariable long id, RedirectAttributes redirectAttributes) {
        if (store.deleteProduct(id)) {
            redirectAttributes.addFlashAttribute("success", "Product removed.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found.");
        }
        return "redirect:/inventory";
    }
}