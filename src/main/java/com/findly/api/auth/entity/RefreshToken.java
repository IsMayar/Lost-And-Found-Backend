package com.findly.api.auth.entity;

import com.findly.api.common.entity.BaseEntity;
import com.findly.api.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_token_hash", columnList = "tokenHash", unique = true),
                @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_refresh_tokens_revoked", columnList = "revoked")
        }
)
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 128)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    private Instant revokedAt;

    @Column(length = 255)
    private String replacedByTokenHash;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !revoked && !isExpired() && !isDeleted();
    }

    public void revoke() {
        this.revoked = true;
        this.revokedAt = Instant.now();
    }

    public void revoke(String replacedByTokenHash) {
        revoke();
        this.replacedByTokenHash = replacedByTokenHash;
    }
}
