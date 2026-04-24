package com.lia.liaprove.infrastructure.repositories.assessment;

import com.lia.liaprove.infrastructure.entities.assessment.CertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificateJpaRepository extends JpaRepository<CertificateEntity, UUID> {
    Optional<CertificateEntity> findByCertificateNumber(String certificateNumber);
}

