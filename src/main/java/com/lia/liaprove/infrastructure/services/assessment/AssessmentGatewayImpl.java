package com.lia.liaprove.infrastructure.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentMapper;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssessmentGatewayImpl implements AssessmentGateway {

    private final AssessmentJpaRepository assessmentJpaRepository;
    private final AssessmentMapper assessmentMapper;

    public AssessmentGatewayImpl(AssessmentJpaRepository assessmentJpaRepository, AssessmentMapper assessmentMapper) {
        this.assessmentJpaRepository = assessmentJpaRepository;
        this.assessmentMapper = assessmentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Assessment> findById(UUID id) {
        return assessmentJpaRepository.findByIdWithCreator(id)
                .map(assessmentMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Assessment> findByShareableToken(String token) {
        return assessmentJpaRepository.findByShareableToken(token)
                .map(assessmentMapper::toDomain);
    }

    @Override
    @Transactional
    public Assessment save(Assessment assessment) {
        AssessmentEntity entity = assessmentMapper.toEntity(assessment);
        AssessmentEntity savedEntity = assessmentJpaRepository.save(entity);
        return assessmentMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void saveAll(List<PersonalizedAssessment> assessments) {
        List<PersonalizedAssessmentEntity> entities = assessments.stream()
                .map(assessmentMapper::toEntity)
                .collect(Collectors.toList());
        assessmentJpaRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void deletePersonalizedAssessmentById(UUID assessmentId) {
        assessmentJpaRepository.deleteById(assessmentId);
    }

    @Override
    public List<PersonalizedAssessment> findActiveAssessmentsWithPastExpirationDate() {
        return assessmentJpaRepository.findActiveAssessmentsWithPastExpirationDate(
                        PersonalizedAssessmentStatus.ACTIVE,
                        LocalDateTime.now()
                ).stream()
                .map(assessmentMapper::toDomain)
                .collect(Collectors.toList());
    }
}

