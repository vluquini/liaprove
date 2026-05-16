package com.lia.liaprove.core.usecases.user;

import com.lia.liaprove.core.domain.assessment.Certificate;

import java.util.List;
import java.util.UUID;

public interface ListMyCertificatesUseCase {
    List<Certificate> execute(UUID userId);
}
