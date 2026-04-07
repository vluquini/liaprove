package com.lia.liaprove.infrastructure.services.metrics;

import com.lia.liaprove.application.gateways.metrics.AssessmentAttemptVoteGateway;
import com.lia.liaprove.core.domain.metrics.AssessmentAttemptVote;
import com.lia.liaprove.infrastructure.entities.metrics.AssessmentAttemptVoteEntity;
import com.lia.liaprove.infrastructure.mappers.metrics.AssessmentAttemptVoteMapper;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptVoteJpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AssessmentAttemptVoteGatewayImpl implements AssessmentAttemptVoteGateway {

    private final AssessmentAttemptVoteJpaRepository assessmentAttemptVoteJpaRepository;
    private final AssessmentAttemptVoteMapper assessmentAttemptVoteMapper;

    public AssessmentAttemptVoteGatewayImpl(AssessmentAttemptVoteJpaRepository assessmentAttemptVoteJpaRepository,
                                            AssessmentAttemptVoteMapper assessmentAttemptVoteMapper) {
        this.assessmentAttemptVoteJpaRepository = assessmentAttemptVoteJpaRepository;
        this.assessmentAttemptVoteMapper = assessmentAttemptVoteMapper;
    }

    @Override
    public void save(AssessmentAttemptVote vote) {
        AssessmentAttemptVoteEntity entity = assessmentAttemptVoteMapper.toEntity(vote);
        assessmentAttemptVoteJpaRepository.save(entity);
        vote.setId(entity.getId());
    }

    @Override
    public boolean existsByUserIdAndAttemptId(UUID userId, UUID attemptId) {
        return assessmentAttemptVoteJpaRepository.existsByUserIdAndAssessmentAttemptId(userId, attemptId);
    }
}
