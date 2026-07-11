package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.algorithms.bayesian.BayesianVotingDecision;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.exceptions.question.QuestionNotFoundException;
import com.lia.liaprove.core.usecases.algorithms.bayesian.BayesianNetworkUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BayesianEvaluateVotingResultUseCaseImplTest {

    @Mock
    private QuestionGateway questionGateway;

    @Mock
    private BayesianNetworkUseCase bayesianNetworkUseCase;

    @Test
    void shouldPersistApprovedStatusFromBayesianDecision() {
        UUID questionId = UUID.randomUUID();
        Question question = votingQuestion(questionId);
        BayesianEvaluateVotingResultUseCaseImpl useCase = new BayesianEvaluateVotingResultUseCaseImpl(
                questionGateway,
                bayesianNetworkUseCase
        );

        when(questionGateway.findById(questionId)).thenReturn(Optional.of(question));
        when(bayesianNetworkUseCase.evaluateVotingResult(questionId)).thenReturn(new BayesianVotingDecision(
                questionId,
                0.75,
                QuestionStatus.APPROVED,
                6.0,
                0.60
        ));
        when(questionGateway.save(question)).thenReturn(question);

        Question result = useCase.evaluate(questionId);

        assertThat(result.getStatus()).isEqualTo(QuestionStatus.APPROVED);
        verify(questionGateway).save(question);
    }

    @Test
    void shouldPersistRejectedStatusFromBayesianDecision() {
        UUID questionId = UUID.randomUUID();
        Question question = votingQuestion(questionId);
        BayesianEvaluateVotingResultUseCaseImpl useCase = new BayesianEvaluateVotingResultUseCaseImpl(
                questionGateway,
                bayesianNetworkUseCase
        );

        when(questionGateway.findById(questionId)).thenReturn(Optional.of(question));
        when(bayesianNetworkUseCase.evaluateVotingResult(questionId)).thenReturn(new BayesianVotingDecision(
                questionId,
                0.40,
                QuestionStatus.REJECTED,
                5.0,
                0.60
        ));
        when(questionGateway.save(question)).thenReturn(question);

        Question result = useCase.evaluate(questionId);

        assertThat(result.getStatus()).isEqualTo(QuestionStatus.REJECTED);
        verify(questionGateway).save(question);
    }

    @Test
    void shouldThrowWhenQuestionDoesNotExist() {
        UUID questionId = UUID.randomUUID();
        BayesianEvaluateVotingResultUseCaseImpl useCase = new BayesianEvaluateVotingResultUseCaseImpl(
                questionGateway,
                bayesianNetworkUseCase
        );

        when(questionGateway.findById(questionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.evaluate(questionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessage("Question with id " + questionId + " not found.");

        verify(bayesianNetworkUseCase, never()).evaluateVotingResult(questionId);
        verify(questionGateway, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private Question votingQuestion(UUID id) {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion();
        question.setId(id);
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Question title");
        question.setDescription("Question description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setRelevanceByLLM(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.of(2026, 7, 11, 10, 0));
        question.setVotingEndDate(LocalDateTime.of(2026, 7, 12, 10, 0));
        question.setStatus(QuestionStatus.VOTING);
        return question;
    }
}
