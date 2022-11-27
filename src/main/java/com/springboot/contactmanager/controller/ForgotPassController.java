package com.springboot.contactmanager.controller;

import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotPassController {
    @GetMapping("/forgot-password")
    public String forgot(Model model) {
        model.addAttribute("title", "forgot password");
        return "forgot-pass";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, Model model) {
        Random random = new Random();
        String otp = RandomString.make(3) + random.nextInt(1000);
//        System.out.println(otp);
//        System.out.println(email);
        return "verify-otp";
    }
}
