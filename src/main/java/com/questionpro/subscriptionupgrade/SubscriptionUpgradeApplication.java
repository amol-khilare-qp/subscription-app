package com.questionpro.subscriptionupgrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class SubscriptionUpgradeApplication {

	public static void main(String[] args) {
		log.info("Starting Subscription Upgrade Application...");
		SpringApplication.run(SubscriptionUpgradeApplication.class, args);
	}
}
