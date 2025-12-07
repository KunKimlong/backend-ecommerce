package com.dyc.backendecommerce.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/auth/test")
    public String test() {
        return "test";
    }
}
