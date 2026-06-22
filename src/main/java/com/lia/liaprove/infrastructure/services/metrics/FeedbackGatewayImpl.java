package com.lia.liaprove.infrastructure.services.metrics;

import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackAssessmentMapper;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackQuestionMapper;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackReactionMapper;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.metrics.FeedbackAssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.FeedbackQuestionJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedbackGatewayImpl implements FeedbackGateway {

    private final FeedbackQuestionJpaRepository feedbackQuestionJpaRepository;
    private final FeedbackAssessmentJpaRepository feedbackAssessmentJpaRepository;
    private final FeedbackQuestionMapper feedbackQuestionMapper;
    private final FeedbackAssessmentMapper feedbackAssessmentMapper;
    private final FeedbackReactionMapper feedbackReactionMapper;
    private final QuestionMapper questionMapper;

    public FeedbackGatewayImpl(FeedbackQuestionJpaRepository feedbackQuestionJpaRepository,
                               FeedbackAssessmentJpaRepository feedbackAssessmentJpaRepository,
                               FeedbackQuestionMapper feedbackQuestionMapper,
                               FeedbackAssessmentMapper feedbackAssessmentMapper,
                               FeedbackReactionMapper feedbackReactionMapper,
                               QuestionMapper questionMapper) {
        this.feedbackQuestionJpaRepository = feedbackQuestionJpaRepository;
        this.feedbackAssessmentJpaRepository = feedbackAssessmentJpaRepository;
        this.feedbackQuestionMapper = feedbackQuestionMapper;
        this.feedbackAssessmentMapper = feedbackAssessmentMapper;
        this.feedbackReactionMapper = feedbackReactionMapper;
        this.questionMapper = questionMapper;
    }

    @Override
    public void saveAssessmentFeedback(FeedbackAssessment feedback) {
        FeedbackAssessmentEntity managedEntity;

        if (feedback.getId() != null) {
            managedEntity = feedbackAssessmentJpaRepository.findByIdWithDetails(feedback.getId())
                    .orElseThrow(() -> new EntityNotFoundException("FeedbackAssessment not found for ID: " + feedback.getId()));

            managedEntity.setComment(feedback.getComment());
            managedEntity.setVisible(feedback.isVisible());
            managedEntity.setUpdatedAt(feedback.getUpdatedAt());
            managedEntity.getReactions().clear();
            feedback.getReactions().stream()
                    .map(feedbackReactionMapper::toEntity)
                    .forEach(managedEntity::addReaction);
        } else {
            managedEntity = feedbackAssessmentMapper.toEntity(feedback);
            feedback.getReactions().stream()
                    .map(feedbackReactionMapper::toEntity)
                    .forEach(managedEntity::addReaction);
        }

        FeedbackAssessmentEntity savedEntity = feedbackAssessmentJpaRepository.save(managedEntity);
        if (feedback.getId() == null) {
            feedback.setId(savedEntity.getId());
        }
    }

    @Override
    public boolean existsAssessmentFeedbackByUserIdAndAttemptId(UUID userId, UUID attemptId) {
        return feedbackAssessmentJpaRepository.existsByUserIdAndAssessmentAttemptId(userId, attemptId);
    }

    @Override
    public List<FeedbackAssessment> findAssessmentFeedbacksByAttemptId(UUID attemptId) {
        return feedbackAssessmentJpaRepository.findVisibleByAssessmentAttemptIdWithDetails(attemptId).stream()
                .map(feedbackAssessmentMapper::toDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Optional<FeedbackAssessment> findFeedbackAssessmentById(UUID feedbackId) {
        return feedbackAssessmentJpaRepository.findByIdWithDetails(feedbackId)
                .map(feedbackAssessmentMapper::toDomain);
    }

    @Override
    public List<FeedbackQuestion> findFeedbacksByQuestionId(UUID questionId) {
        List<FeedbackQuestionEntity> entities = feedbackQuestionJpaRepository.findWithDetailsByQuestionId(questionId);
        return entities.stream()
                .map(feedbackQuestionMapper::toDomain)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Optional<FeedbackQuestion> findFeedbackQuestionById(UUID feedbackId) {
        return feedbackQuestionJpaRepository.findFeedbackByIdWithDetails(feedbackId)
                .map(feedbackQuestionMapper::toDomain);
    }

    @Override
    public void saveFeedbackQuestion(FeedbackQuestion feedback) {
        FeedbackQuestionEntity managedEntity;

        if (feedback.getId() != null) {
            // This is an update scenario. Fetch the existing managed entity.
            managedEntity = feedbackQuestionJpaRepository.findFeedbackByIdWithDetails(feedback.getId())
                    .orElseThrow(() -> new EntityNotFoundException("FeedbackQuestion not found for ID: " + feedback.getId()));

            // Now, update only the mutable properties of the MANAGED entity.
            // We assume 'comment' can be updated.
            // The 'question' and 'user' associations are immutable for an existing feedback and should not be touched here.
            managedEntity.setComment(feedback.getComment());
            managedEntity.setUpdatedAt(feedback.getUpdatedAt());

            // Update reactions collection.
            // Clear existing reactions and add new ones derived from the domain object.
            managedEntity.getReactions().clear();
            feedback.getReactions().stream()
                    .map(feedbackReactionMapper::toEntity)
                    .forEach(managedEntity::addReaction);

        } else {
            // This is a creation scenario. Create a new entity from the domain object.
            managedEntity = feedbackQuestionMapper.toEntity(feedback);
            managedEntity.setQuestion(questionMapper.toEntity(feedback.getQuestion()));

            // Map and add reactions for the new entity.
            feedback.getReactions().stream()
                    .map(feedbackReactionMapper::toEntity)
                    .forEach(managedEntity::addReaction);
        }

        // Save the managed (or newly created) entity.
        // For updates, this will merge the changes into the database.
        FeedbackQuestionEntity savedEntity = feedbackQuestionJpaRepository.save(managedEntity);

        // Only set the ID on the domain object if it was a new creation.
        if (feedback.getId() == null) {
            feedback.setId(savedEntity.getId());
        }
    }
}

