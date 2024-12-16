package com.questionpro.subscriptionupgrade.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.questionpro.subscriptionupgrade.entity.Subscription;
import com.questionpro.subscriptionupgrade.entity.User;
import com.questionpro.subscriptionupgrade.entity.UserSubscription;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
	 UserSubscription findByUserAndSubscription(User user, Subscription subscription);
}
