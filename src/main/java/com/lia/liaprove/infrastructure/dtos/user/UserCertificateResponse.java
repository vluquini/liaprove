package com.lia.liaprove.infrastructure.dtos.user;

import java.time.LocalDate;

public record UserCertificateResponse(
        String certificateNumber,
        String title,
        String description,
        String certificateUrl,
        LocalDate issueDate,
        Float score
) {}
