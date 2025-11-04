/*
package com.example.apigateway02.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTHORIZATION-SERVICE")
public interface AuthorizationClient {

    @GetMapping("/auth/validate")
    String validateUser(@RequestHeader("username") String username,
                        @RequestHeader("password") String password,
                        @RequestHeader("role") String role);
}
*/
