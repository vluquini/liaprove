package com.lia.liaprove.infrastructure.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.services.assessment.dto.ListAttemptsFilterDto;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentEntity;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentAttemptMapper;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssessmentAttemptGatewayImpl implements AssessmentAttemptGateway {

    private final AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;
    private final AssessmentJpaRepository assessmentJpaRepository;
    private final AssessmentAttemptMapper assessmentAttemptMapper;

    public AssessmentAttemptGatewayImpl(
            AssessmentAttemptJpaRepository assessmentAttemptJpaRepository,
            AssessmentJpaRepository assessmentJpaRepository,
            AssessmentAttemptMapper assessmentAttemptMapper
    ) {
        this.assessmentAttemptJpaRepository = assessmentAttemptJpaRepository;
        this.assessmentJpaRepository = assessmentJpaRepository;
        this.assessmentAttemptMapper = assessmentAttemptMapper;
    }

    @Override
    @Transactional
    public AssessmentAttempt save(AssessmentAttempt attempt) {
        AssessmentAttemptEntity entity = assessmentAttemptMapper.toEntity(attempt);

        // Se a avaliação associada for nova (sem ID), salva-a antes de salvar a tentativa.
        // Isso é comum para SystemAssessments gerados on-the-fly.
        if (entity.getAssessment() != null && entity.getAssessment().getId() == null) {
            AssessmentEntity savedAssessment = assessmentJpaRepository.save(entity.getAssessment());
            entity.setAssessment(savedAssessment);
        }

        AssessmentAttemptEntity savedEntity = assessmentAttemptJpaRepository.save(entity);
        return assessmentAttemptMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssessmentAttempt> findById(UUID id) {
        return assessmentAttemptJpaRepository.findByIdWithAssessmentAndCreator(id)
                .map(assessmentAttemptMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentAttempt> findByAssessmentId(UUID assessmentId) {
        return assessmentAttemptJpaRepository.findByAssessmentId(assessmentId).stream()
                .map(assessmentAttemptMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssessmentAttempt> findAllByCriteria(ListAttemptsFilterDto filter) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public long countByAssessmentId(UUID assessmentId) {
        return assessmentAttemptJpaRepository.countByAssessmentId(assessmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAssessmentIdAndUserId(UUID assessmentId, UUID userId) {
        return assessmentAttemptJpaRepository.existsByAssessmentIdAndUserId(assessmentId, userId);
    }
}

