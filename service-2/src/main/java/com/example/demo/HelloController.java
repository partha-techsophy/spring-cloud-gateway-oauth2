package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-2")
public class HelloController {

    @RequestMapping("/hello")
    public ResponseEntity<String> sayHello() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        return ResponseEntity.ok("Hello from service 2 - "+ "Scopes: " + authentication.getAuthorities());
    }
}
