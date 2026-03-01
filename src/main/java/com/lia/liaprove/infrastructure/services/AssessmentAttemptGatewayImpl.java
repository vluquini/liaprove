package com.lia.liaprove.infrastructure.services;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.services.assessment.dto.ListAttemptsFilterDto;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentAttemptMapper;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssessmentAttemptGatewayImpl implements AssessmentAttemptGateway {

    private final AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;
    private final AssessmentAttemptMapper assessmentAttemptMapper;

    public AssessmentAttemptGatewayImpl(
            AssessmentAttemptJpaRepository assessmentAttemptJpaRepository,
            AssessmentAttemptMapper assessmentAttemptMapper
    ) {
        this.assessmentAttemptJpaRepository = assessmentAttemptJpaRepository;
        this.assessmentAttemptMapper = assessmentAttemptMapper;
    }

    @Override
    public AssessmentAttempt save(AssessmentAttempt attempt) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Optional<AssessmentAttempt> findById(UUID id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<AssessmentAttempt> findByAssessmentId(UUID assessmentId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<AssessmentAttempt> findAllByCriteria(ListAttemptsFilterDto filter) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public long countByAssessmentId(UUID assessmentId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean existsByAssessmentIdAndUserId(UUID assessmentId, UUID userId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

