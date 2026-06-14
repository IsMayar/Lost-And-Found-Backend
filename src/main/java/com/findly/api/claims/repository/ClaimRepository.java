package com.findly.api.claims.repository;

import com.findly.api.claims.entity.Claim;
import com.findly.api.common.enums.ClaimStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID>, JpaSpecificationExecutor<Claim> {

    boolean existsByReportIdAndClaimantIdAndDeletedFalse(UUID reportId, UUID claimantId);

    List<Claim> findByClaimantIdAndDeletedFalseOrderByCreatedAtDesc(UUID claimantId);

    List<Claim> findByReportIdAndDeletedFalseOrderByCreatedAtDesc(UUID reportId);

    Optional<Claim> findByIdAndDeletedFalse(UUID id);

    List<Claim> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    List<Claim> findByStatusAndDeletedFalseOrderByCreatedAtDesc(ClaimStatus status, Pageable pageable);

    long countByDeletedFalse();

    long countByStatusAndDeletedFalse(ClaimStatus status);
}