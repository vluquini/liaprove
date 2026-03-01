package com.lia.liaprove.infrastructure.services;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentMapper;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssessmentGatewayImpl implements AssessmentGateway {

    private final AssessmentJpaRepository assessmentJpaRepository;
    private final AssessmentMapper assessmentMapper;

    public AssessmentGatewayImpl(AssessmentJpaRepository assessmentJpaRepository, AssessmentMapper assessmentMapper) {
        this.assessmentJpaRepository = assessmentJpaRepository;
        this.assessmentMapper = assessmentMapper;
    }

    @Override
    public Optional<Assessment> findById(UUID id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Optional<Assessment> findByShareableToken(String token) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Assessment save(Assessment assessment) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void saveAll(List<PersonalizedAssessment> assessments) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void deletePersonalizedAssessmentById(UUID assessmentId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<PersonalizedAssessment> findActiveAssessmentsWithPastExpirationDate() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

