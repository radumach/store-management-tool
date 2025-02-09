package com.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.model.User;
import com.store.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public User getUser(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public void changePassword(@RequestParam String newPassword) {
        userService.changePassword(newPassword);
    }
}