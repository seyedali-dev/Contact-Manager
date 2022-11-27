package com.springboot.contactmanager.controller;

import com.springboot.contactmanager.entities.User;
import com.springboot.contactmanager.helper.Message;
import com.springboot.contactmanager.repository.UserRepository;
import com.springboot.contactmanager.service.EmailService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgotPassController {
    @Autowired private EmailService emailService;
    @Autowired private UserRepository userRepo;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/forgot-password")
    public String forgot(Model model) {
        model.addAttribute("title", "forgot password");
        return "forgotPassword/forgot-pass";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session, Model model) {
        Random random = new Random();
        String otp = RandomString.make(3) + random.nextInt(1000);
//        System.out.println(otp);
//        System.out.println(email);
        String subject = "OTP : Verify Your Account";
        String message = "<div style='padding: 25px; border:3px solid black; width:18%; text-align: center'>" +
                         "      <h3>Your OTP Request!</h3>" +
                         "      <hr>" +
                         "      <h2>=>     "+ otp +"     <=</h2>" +
                         "      Please copy this otp text!" +
                         "</div>";
        boolean sendEmail = emailService.sendEmail(message, subject, email);
        if (sendEmail) {
            session.setAttribute("sentOtp", otp);
            session.setAttribute("email", email);
            model.addAttribute("title", "Verify OTP");
            session.setAttribute("message", new Message("We have send you an email containing the OTP, Please proceed to your email account.", "alert-secondary"));
            return "forgotPassword/verify-otp";
        } else {
            session.setAttribute("message", "Please Check Your Email Id!");
            return "forgotPassword/forgot-pass";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp, Model model, HttpSession session) {
        String sentOtp = (String) session.getAttribute("sentOtp");
        String email = (String) session.getAttribute("email");

        if (sentOtp.equals(otp)) {
            //change password form
            User user = userRepo.findByEmail(email);
            if (user != null) {
                model.addAttribute("title", "Reset Password");
                return "forgotPassword/change-password-form";
            } else {
                session.setAttribute("message", "Unfortunately, the user does not exist in our database.");
                return "forgotPassword/forgot-pass";
            }
        } else {
            session.setAttribute("message", new Message("OTP does not match :(", "alert-danger"));
            return "forgotPassword/verify-otp";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword") String newPassword, Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");
        User user = userRepo.findByEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepo.save(user);
        return "redirect:/login?change=Password changed successfully!";
    }
}
