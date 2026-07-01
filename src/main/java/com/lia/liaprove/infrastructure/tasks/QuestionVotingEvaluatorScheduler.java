package com.lia.liaprove.infrastructure.tasks;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.usecases.question.EvaluateVotingResultUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    // Runs every 1 minute by default. This demo flow ignores votingEndDate and evaluates one VOTING question per run.
    @Scheduled(fixedRateString = "${question.voting.evaluator.scheduler.fixed-rate-ms:60000}")
    public void evaluateExpiredQuestionVotes() {
        List<Question> votingQuestions = questionGateway.findByStatus(QuestionStatus.VOTING);

        if (!votingQuestions.isEmpty()) {
            evaluateVotingResultUseCase.evaluate(votingQuestions.get(0).getId());
        }
    }
}
