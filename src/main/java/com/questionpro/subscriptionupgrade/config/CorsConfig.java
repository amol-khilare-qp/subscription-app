package com.questionpro.subscriptionupgrade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

	@Value("${access.control.allow.origin}")
	private String origin;

	@Bean
	public WebMvcConfigurer configure() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowedOrigins(origin).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("Authorization", "Content-Type", "Strict-Transport-Security",
								"Content-Security-Policy", "X-Frame-Options", "X-Content-Type-Options",
								"X-XSS-Protection", "Cache-Control", "Pragma");
			}
		};
	}
}
