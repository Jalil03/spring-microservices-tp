package com.example.authorizationservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    @Value("${security.productcomposite.admin.username}")
    private String adminUsername;

    @Value("${security.productcomposite.admin.password}")
    private String adminPassword;

    @Value("${security.productcomposite.user.username}")
    private String userUsername;

    @Value("${security.productcomposite.user.password}")
    private String userPassword;

    @GetMapping("/validate")
    public String validateUser(@RequestHeader("username") String username,
                               @RequestHeader("password") String password,
                               @RequestHeader("role") String role) {

        if (role.equalsIgnoreCase("ADMIN")
                && username.equals(adminUsername)
                && password.equals(adminPassword)) {
            return "AUTHORIZED_ADMIN";
        } else if (role.equalsIgnoreCase("USER")
                && username.equals(userUsername)
                && password.equals(userPassword)) {
            return "AUTHORIZED_USER";
        } else {
            return "UNAUTHORIZED";
        }
    }
}
