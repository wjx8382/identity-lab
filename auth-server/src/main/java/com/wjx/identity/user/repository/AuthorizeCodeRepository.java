package com.wjx.identity.user.repository;

import com.wjx.identity.user.entity.AuthorizationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorizeCodeRepository
        extends JpaRepository<AuthorizationCodeEntity, Long> {

    Optional<AuthorizationCodeEntity> findByCode(
            String code
    );

    void deleteByCode(String code);
}
