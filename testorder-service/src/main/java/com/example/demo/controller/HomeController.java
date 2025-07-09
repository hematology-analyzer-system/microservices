package com.example.demo.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/register";
    }

    @GetMapping("/register-form")
    public String showForm(){
        return "register";
    }

    @GetMapping("/order-list")
    public String showOrders(){
        return "orders";
    }
}
