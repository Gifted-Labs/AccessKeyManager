package com.juls.accesskeymanager.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juls.accesskeymanager.data.token.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
    VerificationToken findByToken(String token);
}
