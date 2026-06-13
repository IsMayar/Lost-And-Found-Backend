package com.findly.api.auth.repository;

import com.findly.api.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHashAndDeletedFalse(String tokenHash);

    void deleteByUserId(UUID userId);
}
