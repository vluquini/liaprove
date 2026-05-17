package com.lia.liaprove.infrastructure.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.services.assessment.dto.ListAttemptsFilterDto;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.assessment.SystemAssessmentEntity;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentAttemptMapper;
import com.lia.liaprove.infrastructure.mappers.assessment.CertificateMapper;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssessmentAttemptGatewayImpl implements AssessmentAttemptGateway {

    private final AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;
    private final AssessmentJpaRepository assessmentJpaRepository;
    private final AssessmentAttemptMapper assessmentAttemptMapper;
    private final CertificateMapper certificateMapper;

    public AssessmentAttemptGatewayImpl(
            AssessmentAttemptJpaRepository assessmentAttemptJpaRepository,
            AssessmentJpaRepository assessmentJpaRepository,
            AssessmentAttemptMapper assessmentAttemptMapper,
            CertificateMapper certificateMapper
    ) {
        this.assessmentAttemptJpaRepository = assessmentAttemptJpaRepository;
        this.assessmentJpaRepository = assessmentJpaRepository;
        this.assessmentAttemptMapper = assessmentAttemptMapper;
        this.certificateMapper = certificateMapper;
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
        return assessmentAttemptJpaRepository.findByIdFetchingAssessment(id)
                .map(assessmentAttemptMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssessmentAttempt> findByIdWithCreator(UUID id) {
        return assessmentAttemptJpaRepository.findByIdWithAssessmentAndCreator(id)
                .map(assessmentAttemptMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssessmentAttempt> findByCertificateNumber(String certificateNumber) {
        return assessmentAttemptJpaRepository.findByCertificateNumber(certificateNumber)
                .map(assessmentAttemptMapper::toDomainSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Certificate> findCertificatesByUserId(UUID userId) {
        Map<String, AssessmentAttemptEntity> bestAttemptsByCertificateSlot = new LinkedHashMap<>();

        for (AssessmentAttemptEntity attempt : assessmentAttemptJpaRepository.findCertifiedAttemptsByUserId(userId)) {
            bestAttemptsByCertificateSlot.merge(
                    certificateSlotKey(attempt),
                    attempt,
                    this::bestCertifiedAttempt
            );
        }

        return bestAttemptsByCertificateSlot.values().stream()
                .sorted(certificateListOrdering())
                .map(AssessmentAttemptEntity::getCertificate)
                .map(certificateMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssessmentAttempt> findBestCertifiedSystemAttemptByUserAndCriteria(
            UUID userId,
            KnowledgeArea knowledgeArea,
            DifficultyLevel difficultyLevel
    ) {
        return assessmentAttemptJpaRepository.findCertifiedSystemAttemptsByUserAndCriteria(
                        userId,
                        knowledgeArea,
                        difficultyLevel,
                        PageRequest.of(0, 1)
                ).stream()
                .findFirst()
                .map(assessmentAttemptMapper::toDomainSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentAttempt> findByAssessmentId(UUID assessmentId) {
        return assessmentAttemptJpaRepository.findByAssessmentId(assessmentId).stream()
                .map(assessmentAttemptMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentAttempt> findSummariesByAssessmentId(UUID assessmentId) {
        return assessmentAttemptJpaRepository.findSummariesByAssessmentId(assessmentId).stream()
                .map(assessmentAttemptMapper::toDomainSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentAttempt> findAllByCriteria(ListAttemptsFilterDto filter) {
        Specification<AssessmentAttemptEntity> spec = Specification.where(null);

        if (filter.isPersonalized().isPresent()) {
            boolean isPersonalized = filter.isPersonalized().get();
            spec = spec.and((root, query, cb) -> {
                if (isPersonalized) {
                    return cb.equal(root.get("assessment").type(), PersonalizedAssessmentEntity.class);
                }
                return cb.equal(root.get("assessment").type(), SystemAssessmentEntity.class);
            });
        }

        if (filter.startDate().isPresent()) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startedAt"), filter.startDate().get()));
        }

        if (filter.endDate().isPresent()) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("startedAt"), filter.endDate().get()));
        }

        if (filter.statuses().isPresent() && !filter.statuses().get().isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("status").in(filter.statuses().get()));
        }

        return assessmentAttemptJpaRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "startedAt")).stream()
                .map(assessmentAttemptMapper::toDomainSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentAttempt> findPublicSystemProjectAttemptsExcludingUser(UUID userId) {
        return assessmentAttemptJpaRepository.findPublicSystemProjectAttemptsExcludingUser(userId).stream()
                .map(assessmentAttemptMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentAttempt> findCompletedSystemProjectAttemptsReadyForCommunityDecision(LocalDateTime cutoff) {
        return assessmentAttemptJpaRepository.findCompletedSystemProjectAttemptsReadyForCommunityDecision(cutoff).stream()
                .map(assessmentAttemptMapper::toDomain)
                .collect(Collectors.toList());
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

    private String certificateSlotKey(AssessmentAttemptEntity attempt) {
        if (attempt.getAssessment() instanceof SystemAssessmentEntity systemAssessment
                && systemAssessment.getKnowledgeArea() != null
                && systemAssessment.getDifficultyLevel() != null) {
            return "system:%s:%s".formatted(
                    systemAssessment.getKnowledgeArea(),
                    systemAssessment.getDifficultyLevel()
            );
        }

        return "attempt:%s".formatted(attempt.getId());
    }

    private AssessmentAttemptEntity bestCertifiedAttempt(
            AssessmentAttemptEntity current,
            AssessmentAttemptEntity candidate
    ) {
        return certificatePriorityOrdering().compare(candidate, current) < 0 ? candidate : current;
    }

    private Comparator<AssessmentAttemptEntity> certificatePriorityOrdering() {
        return Comparator
                .comparing(
                        (AssessmentAttemptEntity attempt) -> Optional.ofNullable(attempt.getCertificate().getScore()).orElse(-1F),
                        Comparator.reverseOrder()
                )
                .thenComparing(
                        attempt -> attempt.getCertificate().getIssueDate(),
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
                .thenComparing(
                        attempt -> attempt.getCertificate().getCertificateNumber(),
                        Comparator.nullsLast(String::compareTo)
                );
    }

    private Comparator<AssessmentAttemptEntity> certificateListOrdering() {
        return Comparator
                .comparing(
                        (AssessmentAttemptEntity attempt) -> attempt.getCertificate().getIssueDate(),
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
                .thenComparing(
                        attempt -> attempt.getCertificate().getCertificateNumber(),
                        Comparator.nullsLast(String::compareTo)
                );
    }
}
