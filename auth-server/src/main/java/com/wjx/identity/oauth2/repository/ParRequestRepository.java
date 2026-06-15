package com.wjx.identity.oauth2.repository;

import com.wjx.identity.user.entity.ParRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParRequestRepository
        extends JpaRepository<ParRequestEntity, Long> {

    Optional<ParRequestEntity> findByRequestId(String requestId);
}
