package com.lia.liaprove.infrastructure.services;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuestionGatewayImpl implements QuestionGateway {

    private final QuestionJpaRepository questionJpaRepository;
    private final QuestionMapper questionMapper;

    public QuestionGatewayImpl(QuestionJpaRepository questionJpaRepository, QuestionMapper questionMapper) {
        this.questionJpaRepository = questionJpaRepository;
        this.questionMapper = questionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDescription(String description) {
        return questionJpaRepository.existsByDescription(description);
    }

    @Override
    @Transactional
    public Question save(Question question) {
        return questionMapper.toDomain(questionJpaRepository.save(questionMapper.toEntity(question)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Question> findById(UUID id) {
        return questionJpaRepository.findById(id).map(questionMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findAll(Set<KnowledgeArea> knowledgeAreas, DifficultyLevel difficultyLevel,
                                  QuestionStatus status, UUID authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questionJpaRepository.findAllWithFilters(knowledgeAreas, difficultyLevel, status, authorId, pageable)
                .stream()
                .map(questionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Question update(Question question) {
        QuestionEntity existingEntity = questionJpaRepository.findById(question.getId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found for update with ID: " + question.getId()));

        // Use the mapper to update the existing entity from the domain object
        // This leverages @MappingTarget and ignores specified fields, handles nulls, and collections
        questionMapper.updateEntityFromDomain(question, existingEntity);

        // The existingEntity is a managed entity with updated fields.
        // Spring Data JPA's save method will persist changes to the managed entity.
        // No need to explicitly call save if the transaction commits and the entity is managed,
        // but calling save() explicitly here ensures the entity is returned and its state is synchronized
        // (and it works even if existingEntity wasn't strictly managed, although findById makes it managed).
        return questionMapper.toDomain(questionJpaRepository.save(existingEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByStatusAndVotingEndDateBefore(QuestionStatus status, java.time.LocalDateTime dateTime) {
        return questionJpaRepository.findByStatusAndVotingEndDateBefore(status, dateTime).stream()
                .map(questionMapper::toDomain)
                .collect(Collectors.toList());
    }

}
