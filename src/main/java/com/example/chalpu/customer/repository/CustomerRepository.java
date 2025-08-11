package com.example.chalpu.customer.repository;

import com.example.chalpu.customer.domain.Customer;
import com.example.chalpu.oauth.model.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmailAndIsActiveTrue(String email);
    
    Optional<Customer> findBySocialIdAndProviderAndIsActiveTrue(String socialId, AuthProvider provider);
    
    boolean existsByEmailAndIsActiveTrue(String email);
    
    @Query("SELECT c.feedbackCount FROM Customer c WHERE c.id = :customerId")
    Integer getFeedbackCount(@Param("customerId") Long customerId);
    
    @Query("SELECT c.rewardCount FROM Customer c WHERE c.id = :customerId")
    Integer getRewardCount(@Param("customerId") Long customerId);
}