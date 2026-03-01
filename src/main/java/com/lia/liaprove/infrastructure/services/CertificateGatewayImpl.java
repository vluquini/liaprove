package com.lia.liaprove.infrastructure.services;

import com.lia.liaprove.application.gateways.assessment.CertificateGateway;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.infrastructure.mappers.assessment.CertificateMapper;
import com.lia.liaprove.infrastructure.repositories.CertificateJpaRepository;
import org.springframework.stereotype.Service;

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
    public Certificate save(Certificate certificate) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Optional<Certificate> findByCertificateNumber(String certificateNumber) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

