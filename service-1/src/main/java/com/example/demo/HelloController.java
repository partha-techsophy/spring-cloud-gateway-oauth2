package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/service-1")
public class HelloController {

    @Autowired
    WebClient webClient;


    @RequestMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from service 1");
    }

    @GetMapping("/internal-call")
    public Mono<String> callService2(@CookieValue("SESSION") String session) {

        return webClient
                .get()
                .uri("http://cloud-gateway:8080/service-2/hello")
                .header("Cookie", "SESSION="+session)
                .retrieve()
                .bodyToMono(String.class);
    }
}
