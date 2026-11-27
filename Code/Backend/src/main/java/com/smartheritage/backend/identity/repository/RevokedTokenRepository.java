package com.smartheritage.backend.identity.repository;

import com.smartheritage.backend.identity.entity.RevokedToken;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {

    boolean existsByTokenIdAndExpiresAtAfter(String tokenId, Instant now);
}
