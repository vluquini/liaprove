package com.lia.liaprove.infrastructure.services;

import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackQuestionMapper;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
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
    private final FeedbackQuestionMapper feedbackQuestionMapper;
    private final QuestionMapper questionMapper;
    // Potentially other mappers/repositories for AssessmentFeedback if implemented later

    public FeedbackGatewayImpl(FeedbackQuestionJpaRepository feedbackQuestionJpaRepository, FeedbackQuestionMapper feedbackQuestionMapper, QuestionMapper questionMapper) {
        this.feedbackQuestionJpaRepository = feedbackQuestionJpaRepository;
        this.feedbackQuestionMapper = feedbackQuestionMapper;
        this.questionMapper = questionMapper;
    }

    @Override
    public void saveAssessmentFeedback(FeedbackAssessment feedback) {
        // TODO: Implement this when FeedbackAssessmentEntity and its mapper are created
        throw new UnsupportedOperationException("Not yet implemented");
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

            // Update reactions collection. This involves clearing the managed collection
            // and re-populating it with entities derived from the domain object's reactions.
            managedEntity.getReactions().clear();
            feedback.getReactions().stream()
                    .map(feedbackQuestionMapper::reactionToEntity) // Converts domain Reaction to ReactionEntity
                    .forEach(managedEntity::addReaction);

        } else {
            // This is a creation scenario. Create a new entity from the domain object.
            // The mapper will ignore 'question' and 'reactions', so we set them manually.
            managedEntity = feedbackQuestionMapper.toEntity(feedback);
            managedEntity.setQuestion(questionMapper.toEntity(feedback.getQuestion()));

            feedback.getReactions().stream()
                    .map(feedbackQuestionMapper::reactionToEntity)
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
