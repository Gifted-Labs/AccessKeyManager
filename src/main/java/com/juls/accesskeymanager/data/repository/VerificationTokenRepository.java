package com.juls.accesskeymanager.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juls.accesskeymanager.data.token.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    
    VerificationToken findByToken(String token);
}
