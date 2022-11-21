package com.springboot.contactmanager.controller;

import com.springboot.contactmanager.entities.User;
import com.springboot.contactmanager.helper.Message;
import com.springboot.contactmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    //home
    @GetMapping("/")
    public String name(Model model) {
        model.addAttribute("title", "Home - Smart contact manager");
        return "home";
    }

    //about
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart contact manager");
        return "about";
    }

    //signup
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart contact manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    //signup handler
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
                               Model model,
                               HttpSession session) {
        try {

            if (!agreement) { //validating agreement
                System.out.println(" Agreement not checked");
                throw new Exception("Agreement not checked");
            }

            if (bindingResult.hasErrors()) { //validating input errors
                System.out.println("hasErrors?: " + bindingResult);
                model.addAttribute("user", user);
                return "signup";
            }

            /*for testing*/
//            System.out.println("agreement = " + agreement);
//            System.out.println("user" + user);
            /*end of testing*/

            user.setEnabled(true);
            user.setRole("ROLE_USER");
            user.setImage("null.png");
            user.setPassword(passwordEncoder.encode(user.getPassword())); //encrypting incoming password

            this.userRepo.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Success", "alert-success"));
            return "signup";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong. " + e.getMessage(), "alert-danger"));
            //whatever the data that has come to this user will get displayed on the screen
            model.addAttribute("user", user);
            return "signup";
        }
    }

    //login handler
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        return "login";
    }
}
