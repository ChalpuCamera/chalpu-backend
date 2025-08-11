package com.example.chalpu.customer.service;

import com.example.chalpu.customer.domain.Customer;
import com.example.chalpu.customer.model.CustomerRefreshToken;
import com.example.chalpu.customer.repository.CustomerRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerRefreshTokenService {

    private final CustomerRefreshTokenRepository customerRefreshTokenRepository;

    public CustomerRefreshToken createRefreshToken(Customer customer, String refreshToken) {
        Optional<CustomerRefreshToken> existingToken = customerRefreshTokenRepository.findByCustomer(customer);
        
        if (existingToken.isPresent()) {
            CustomerRefreshToken token = existingToken.get();
            token.updateRefreshToken(refreshToken);
            return customerRefreshTokenRepository.save(token);
        } else {
            CustomerRefreshToken newToken = CustomerRefreshToken.builder()
                    .customer(customer)
                    .refreshToken(refreshToken)
                    .build();
            return customerRefreshTokenRepository.save(newToken);
        }
    }

    @Transactional(readOnly = true)
    public Optional<CustomerRefreshToken> findByRefreshToken(String refreshToken) {
        return customerRefreshTokenRepository.findByRefreshToken(refreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<CustomerRefreshToken> findByCustomer(Customer customer) {
        return customerRefreshTokenRepository.findByCustomer(customer);
    }

    @Transactional(readOnly = true)
    public Optional<CustomerRefreshToken> findByCustomerId(Long customerId) {
        return customerRefreshTokenRepository.findByCustomerId(customerId);
    }

    public void deleteByCustomer(Customer customer) {
        customerRefreshTokenRepository.deleteByCustomer(customer);
    }

    public void deleteByCustomerId(Long customerId) {
        customerRefreshTokenRepository.deleteByCustomerId(customerId);
    }

    public void deleteByRefreshToken(String refreshToken) {
        Optional<CustomerRefreshToken> tokenOptional = customerRefreshTokenRepository.findByRefreshToken(refreshToken);
        tokenOptional.ifPresent(customerRefreshTokenRepository::delete);
    }
}