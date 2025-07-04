package com.example.chalpu.landingPage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chalpu.landingPage.domain.Landing;

public interface LandingRepository extends JpaRepository<Landing, Long> {
}
