package com.example.HotelManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
public class TestController {

    @GetMapping("/prajyot")
    public String getPrajyot() {
        return "Prajyot";
    }

    @GetMapping("/sayhi")
    public String sayHi() {
        return "Hi guyss";
    }

    @GetMapping("/hotelManagement")
    public String hotelManagement() {
        return "Hotel Management Project";
    }

    @GetMapping("/harshal")
    public String getHarshal() {
        return "Hello there !! ";
    }

    @GetMapping("mohit")
    public String getBy(){
        return "Mohit";
    }

    @GetMapping("zaid")
    public String getZaid(){
        return "Zaid Here";
    }
}
