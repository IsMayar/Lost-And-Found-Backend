package com.findly.api.claims.repository;

import com.findly.api.claims.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    boolean existsByReportIdAndClaimantIdAndDeletedFalse(UUID reportId, UUID claimantId);

    List<Claim> findByClaimantIdAndDeletedFalseOrderByCreatedAtDesc(UUID claimantId);

    List<Claim> findByReportIdAndDeletedFalseOrderByCreatedAtDesc(UUID reportId);

    Optional<Claim> findByIdAndDeletedFalse(UUID id);
}
