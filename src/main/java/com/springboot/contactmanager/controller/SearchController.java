package com.springboot.contactmanager.controller;

import com.springboot.contactmanager.entities.Contact;
import com.springboot.contactmanager.repository.ContactRepository;
import com.springboot.contactmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {
    private UserRepository userRepo;
    private ContactRepository contactRepo;

    @Autowired
    public SearchController(UserRepository userRepo, ContactRepository contactRepo) {
        this.userRepo = userRepo;
        this.contactRepo = contactRepo;
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
        List<Contact> searchContactsResult = contactRepo.findByNameContainingAndUser(query, userRepo.findByEmail(principal.getName()));
        return ResponseEntity.ok(searchContactsResult);
    }
}
