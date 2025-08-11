package com.example.chalpu.customer.repository;

import com.example.chalpu.customer.domain.Customer;
import com.example.chalpu.customer.model.CustomerRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRefreshTokenRepository extends JpaRepository<CustomerRefreshToken, Long> {
    
    @Query("SELECT crt FROM CustomerRefreshToken crt WHERE crt.refreshToken = :refreshToken AND crt.customer.isActive = true")
    Optional<CustomerRefreshToken> findByRefreshToken(@Param("refreshToken") String refreshToken);
    
    @Query("SELECT crt FROM CustomerRefreshToken crt WHERE crt.customer = :customer AND crt.customer.isActive = true")
    Optional<CustomerRefreshToken> findByCustomer(@Param("customer") Customer customer);
    
    @Query("SELECT crt FROM CustomerRefreshToken crt WHERE crt.customer.id = :customerId AND crt.customer.isActive = true")
    Optional<CustomerRefreshToken> findByCustomerId(@Param("customerId") Long customerId);
    
    void deleteByCustomer(Customer customer);
    
    void deleteByCustomerId(Long customerId);
}