package com.lia.liaprove.application.gateways.algorithms.bayesian;

import com.lia.liaprove.core.algorithms.bayesian.QuestionVoteSummary;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.UserRecruiter;

import java.util.List;
import java.util.UUID;

/**
 * Porta que a camada infrastructure deve implementar.
 * Preferencialmente as agregações como getVoteSummaryForQuestion devem ser calculadas
 * no banco (consulta agregada) para performance.
 *
 * Campos / dados que um provider deve expor:
 *  - List<UserRecruiter> getAllRecruiters() -> recruiter.voteWeight, recruiterUsageCount, recruiterRating
 *  - List<Question> getAllQuestions()       -> question.recruiterUsageCount, relevanceByLLM, upVote/downVote, knowledgeAreas
 *
 * Observação: as entidades retornadas são do domínio core (Question, UserRecruiter).
 */
public interface BayesianGateway {

    /**
     * Retorna resumo agregado dos votos da questão solicitada.
     * Implementação ideal: consulta agregada no BD.
     */
    QuestionVoteSummary getVoteSummaryForQuestion(UUID questionId);

    /**
     * Retorna todos os recruiters (ou um subconjunto relevante).
     * A implementação pode paginar ou fornecer apenas os recruiters ativos.
     */
    List<UserRecruiter> getAllRecruiters();

    /**
     * Retorna todas as questões candidatas para sugerir (ou um subconjunto).
     * A implementação pode filtrar por status (por exemplo, apenas questões aprovadas/para votação).
     */
    List<Question> getAllQuestions();

}
