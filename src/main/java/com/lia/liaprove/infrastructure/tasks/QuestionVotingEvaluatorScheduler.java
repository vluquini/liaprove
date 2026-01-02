package com.lia.liaprove.infrastructure.tasks;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.usecases.question.EvaluateVotingResultUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tarefa agendada (Scheduler) para avaliar o resultado da votação de questões expiradas.
 * Este componente executa periodicamente uma verificação para encontrar questões
 * cujo período de votação terminou e aciona o caso de uso de avaliação para
 * atualizar seus status.
 */
@Component
public class QuestionVotingEvaluatorScheduler {

    private final QuestionGateway questionGateway;
    private final EvaluateVotingResultUseCase evaluateVotingResultUseCase;

    public QuestionVotingEvaluatorScheduler(QuestionGateway questionGateway,
                                            EvaluateVotingResultUseCase evaluateVotingResultUseCase) {
        this.questionGateway = questionGateway;
        this.evaluateVotingResultUseCase = evaluateVotingResultUseCase;
    }

    // Runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void evaluateExpiredQuestionVotes() {
        // Find questions that are in VOTING status and their votingEndDate has passed
        List<Question> expiredVotingQuestions = questionGateway.findByStatusAndVotingEndDateBefore(
                QuestionStatus.VOTING,
                LocalDateTime.now()
        );

        for (Question question : expiredVotingQuestions) {
            evaluateVotingResultUseCase.evaluate(question.getId());
        }
    }
}
