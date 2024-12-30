package com.questionpro.subscriptionupgrade.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.questionpro.subscriptionupgrade.entity.Subscription;
import com.questionpro.subscriptionupgrade.entity.User;
import com.questionpro.subscriptionupgrade.entity.UserSubscription;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
	UserSubscription findByUserAndSubscription(User user, Subscription subscription);

	@Query("SELECT us FROM UserSubscription us WHERE us.user = :user AND us.isActive = true")
	UserSubscription findActiveSubscriptionByUser(User user);

	@Query("SELECT us FROM UserSubscription us WHERE us.user = :user AND us.subscription = :subscription AND us.isActive = true")
	UserSubscription findActiveSubscriptionByUserAndSubscription(User user, Subscription subscription);

	@Query("SELECT us FROM UserSubscription us WHERE us.isActive = true AND us.subscriptionEndDate < :currentTime")
	List<UserSubscription> findExpiredActiveSubscriptions(LocalDateTime currentTime);
}
