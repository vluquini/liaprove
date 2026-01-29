package com.lia.liaprove.infrastructure.services;

import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackQuestionMapper;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
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
        List<FeedbackQuestionEntity> entities = feedbackQuestionJpaRepository.findByQuestionId(questionId);
        return entities.stream()
                .map(entity -> {
                    FeedbackQuestion domain = feedbackQuestionMapper.toDomain(entity);
                    // Manually set the question domain object
                    domain.setQuestion(questionMapper.toDomain(entity.getQuestion()));
                    return domain;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Optional<FeedbackQuestion> findFeedbackQuestionById(UUID feedbackId) {
        return feedbackQuestionJpaRepository.findById(feedbackId)
                .map(entity -> {
                    FeedbackQuestion domain = feedbackQuestionMapper.toDomain(entity);
                    // Manually set the question domain object
                    domain.setQuestion(questionMapper.toDomain(entity.getQuestion()));
                    return domain;
                });
    }

    @Override
    public void saveFeedbackQuestion(FeedbackQuestion feedback) {
        FeedbackQuestionEntity entity = feedbackQuestionMapper.toEntity(feedback);
        // Manually set the question entity
        entity.setQuestion(questionMapper.toEntity(feedback.getQuestion()));

        feedback.getReactions().stream()
                .map(feedbackQuestionMapper::reactionToEntity)
                .forEach(entity::addReaction);

        FeedbackQuestionEntity savedEntity = feedbackQuestionJpaRepository.save(entity);
        // Important: Update the domain object's ID with the generated ID from the entity
        feedback.setId(savedEntity.getId());
    }
}
