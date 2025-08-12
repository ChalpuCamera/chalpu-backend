package com.example.chalpu.customer.service;

import com.example.chalpu.customer.domain.Customer;
import com.example.chalpu.customer.domain.CustomerTaste;
import com.example.chalpu.customer.dto.CustomerResponse;
import com.example.chalpu.customer.dto.CustomerTasteResponse;
import com.example.chalpu.customer.repository.CustomerRepository;
import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.CustomerException;
import com.example.chalpu.oauth.model.AuthProvider;
import com.example.chalpu.oauth.security.oauth2.user.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(ErrorMessage.CUSTOMER_NOT_FOUND));
        return CustomerResponse.from(customer);
    }

    public Customer processOAuth2Customer(OAuth2UserInfo oAuth2UserInfo, String providerType) {
        AuthProvider provider = AuthProvider.valueOf(providerType.toUpperCase());
        
        Optional<Customer> existingCustomer = customerRepository
                .findBySocialIdAndProviderAndIsActiveTrue(oAuth2UserInfo.getId(), provider);

        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            customer.updateOAuth2Info(oAuth2UserInfo.getName(), oAuth2UserInfo.getImageUrl());
            log.info("기존 고객 OAuth2 정보 업데이트: customerId={}, provider={}", customer.getId(), provider);
            return customer;
        }

        Optional<Customer> customerByEmail = customerRepository
                .findByEmailAndIsActiveTrue(oAuth2UserInfo.getEmail());

        if (customerByEmail.isPresent()) {
            throw new CustomerException(ErrorMessage.OAUTH_DUPLICATE_EMAIL);
        }

        Customer newCustomer = Customer.createCustomer(
                oAuth2UserInfo.getEmail(),
                oAuth2UserInfo.getName(),
                oAuth2UserInfo.getId(),
                provider,
                oAuth2UserInfo.getImageUrl()
        );

        Customer savedCustomer = customerRepository.save(newCustomer);
        log.info("새로운 고객 생성: customerId={}, email={}, provider={}", 
                savedCustomer.getId(), savedCustomer.getEmail(), provider);
        
        return savedCustomer;
    }

    @Transactional(readOnly = true)
    public Integer getFeedbackCount(Long customerId) {
        return customerRepository.getFeedbackCount(customerId);
    }

    @Transactional(readOnly = true)
    public Integer getRewardCount(Long customerId) {
        return customerRepository.getRewardCount(customerId);
    }

    public void updateProfile(Long customerId, String nickname, Customer.Gender gender, Integer age) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(ErrorMessage.CUSTOMER_NOT_FOUND));
        
        customer.updateProfile(nickname, gender, age);
        log.info("고객 프로필 업데이트: customerId={}, nickname={}", customerId, nickname);
    }

    @Transactional(readOnly = true)
    public CustomerTasteResponse getCustomerTaste(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(ErrorMessage.CUSTOMER_NOT_FOUND));
        
        return CustomerTasteResponse.from(customer.getCustomerTaste());
    }

    public void updateCustomerTaste(Long customerId, CustomerTaste customerTaste) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(ErrorMessage.CUSTOMER_NOT_FOUND));
        
        customer.updateTastePreferences(customerTaste);
        log.info("고객 취향 정보 업데이트: customerId={}", customerId);
    }
}