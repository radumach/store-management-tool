package com.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.model.User;
import com.store.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/add-user")
    @PreAuthorize("hasRole('ADMIN')")
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }

    @PutMapping("/change-username")
    @PreAuthorize("hasRole('ADMIN')")
    public void changeUsername(@RequestParam String oldUsername, @RequestParam String newUsername) {
        userService.changeUsername(oldUsername, newUsername);
    }
}