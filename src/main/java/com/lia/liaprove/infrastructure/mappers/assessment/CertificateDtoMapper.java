package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.infrastructure.dtos.assessment.CertificateVerificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CertificateDtoMapper {

    default CertificateVerificationResponse toVerificationResponse(Certificate certificate, User owner) {
        if (certificate == null) {
            return null;
        }

        CertificateVerificationResponse.OwnerSummary ownerSummary = null;
        if (owner != null) {
            ownerSummary = new CertificateVerificationResponse.OwnerSummary(
                    owner.getId(),
                    owner.getName(),
                    owner.getOccupation(),
                    owner.getExperienceLevel()
            );
        }

        return new CertificateVerificationResponse(
                certificate.getCertificateNumber(),
                certificate.getTitle(),
                certificate.getDescription(),
                certificate.getCertificateUrl(),
                certificate.getIssueDate(),
                certificate.getScore(),
                ownerSummary
        );
    }
}
