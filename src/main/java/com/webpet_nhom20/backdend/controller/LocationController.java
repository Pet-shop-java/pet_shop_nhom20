package com.webpet_nhom20.backdend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/provinces")
    public Object provinces() {
        return restTemplate.getForObject(
                "https://provinces.open-api.vn/api/p/", Object.class);
    }

    @GetMapping("/districts/{provinceCode}")
    public Object districts(@PathVariable int provinceCode) {
        return restTemplate.getForObject(
                "https://provinces.open-api.vn/api/p/" + provinceCode + "?depth=2",
                Object.class);
    }

    @GetMapping("/wards/{districtCode}")
    public Object wards(@PathVariable int districtCode) {
        return restTemplate.getForObject(
                "https://provinces.open-api.vn/api/d/" + districtCode + "?depth=2",
                Object.class);
    }
}

