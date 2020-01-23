package com.example.service1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Service1Controller {

    @GetMapping("/")
    public String service1(){
        return "===Page1===";
    }
    @GetMapping("/view")
    public String service(){
        return "==Page1===의 service";
    }
}
