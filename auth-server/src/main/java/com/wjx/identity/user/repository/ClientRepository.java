package com.wjx.identity.user.repository;

import com.wjx.identity.user.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository
        extends JpaRepository<ClientEntity, Long> {

    Optional<ClientEntity> findByClientId(
            String clientId
    );

    Optional<ClientEntity> findByCertificateSubject(
            String certificateSubject
    );
}
