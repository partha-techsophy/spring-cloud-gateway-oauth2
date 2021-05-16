package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableDiscoveryClient
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public WebClient webClient() {

		return WebClient.builder()
				.filter(logRequest())
				.filter(logResponse())
				.build();
	}


	private ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(clinetRequest -> {
			System.out.println("Request "+ clinetRequest.method() +" : " + clinetRequest.url());
			return Mono.just(clinetRequest);
		});
	}

	private ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(clinetResponse -> {
			System.out.println("Response status code {} " + clinetResponse.statusCode());
			return Mono.just(clinetResponse);
		});
	}
}
