package com.lia.liaprove.infrastructure.services.assessment;

import com.lia.liaprove.application.gateways.assessment.CertificateGateway;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.infrastructure.entities.assessment.CertificateEntity;
import com.lia.liaprove.infrastructure.mappers.assessment.CertificateMapper;
import com.lia.liaprove.infrastructure.repositories.assessment.CertificateJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CertificateGatewayImpl implements CertificateGateway {

    private final CertificateJpaRepository certificateJpaRepository;
    private final CertificateMapper certificateMapper;

    public CertificateGatewayImpl(CertificateJpaRepository certificateJpaRepository, CertificateMapper certificateMapper) {
        this.certificateJpaRepository = certificateJpaRepository;
        this.certificateMapper = certificateMapper;
    }

    @Override
    @Transactional
    public Certificate save(Certificate certificate) {
        CertificateEntity entity = certificateMapper.toEntity(certificate);
        CertificateEntity savedEntity = certificateJpaRepository.save(entity);
        return certificateMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Certificate> findByCertificateNumber(String certificateNumber) {
        return certificateJpaRepository.findByCertificateNumber(certificateNumber)
                .map(certificateMapper::toDomain);
    }
}

