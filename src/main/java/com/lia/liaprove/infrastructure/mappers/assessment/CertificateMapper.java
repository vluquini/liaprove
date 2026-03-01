package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.infrastructure.entities.assessment.CertificateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CertificateMapper {

    default CertificateEntity toEntity(Certificate domain) {
        if (domain == null) {
            return null;
        }

        CertificateEntity entity = new CertificateEntity();
        entity.setId(domain.getId());
        entity.setCertificateNumber(domain.getCertificateNumber());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setCertificateUrl(domain.getCertificateUrl());
        entity.setIssueDate(domain.getIssueDate());
        entity.setScore(domain.getScore());
        return entity;
    }

    default Certificate toDomain(CertificateEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Certificate(
                entity.getId(),
                entity.getCertificateNumber(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCertificateUrl(),
                entity.getIssueDate(),
                entity.getScore()
        );
    }
}

