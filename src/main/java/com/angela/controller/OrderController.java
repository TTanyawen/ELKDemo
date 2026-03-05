package com.angela.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
//    http://127.0.0.1:8087/order/create?userId=1
    @PostMapping("/create")
    public String create(@RequestParam Long userId) {

        if (userId == 999) {
            throw new RuntimeException("mock exception");
        }

        return "ok";
    }
}
