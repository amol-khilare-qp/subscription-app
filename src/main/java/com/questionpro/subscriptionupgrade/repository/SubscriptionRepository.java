package com.questionpro.subscriptionupgrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.questionpro.subscriptionupgrade.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
