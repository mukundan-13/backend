package com.rentwheelz.rentwheelz.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomePageController {

    @GetMapping("/")
    public String home() {
        return "Welcome to RentWheelz!!,Have a remarkable journey";
    }
}