package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Representa uma tentativa específica de um usuário ao realizar uma avaliação (Assessment).
 * Armazena informações sobre o progresso, resultados e o status da tentativa.
 */
public class AssessmentAttempt {
    private UUID id;
    private Assessment assessment;
    private User user;
    private List<Question> questions;           // Lista de questões no momento da tentativa
    private List<Answer> answers;               // Respostas do usuário
    private List<FeedbackAssessment> feedbacks; // Feedbacks associados a esta tentativa
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer accuracyRate;
    private Certificate certificate;
    private AssessmentAttemptStatus status;

    public AssessmentAttempt(UUID id, Assessment assessment, User user, List<Question> questions, List<Answer> answers, List<FeedbackAssessment> feedbacks, LocalDateTime startedAt, LocalDateTime finishedAt, Integer accuracyRate, Certificate certificate, AssessmentAttemptStatus status) {
        this.id = id;
        this.assessment = assessment;
        this.user = user;
        this.questions = questions;
        this.answers = answers;
        this.feedbacks = feedbacks;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.accuracyRate = accuracyRate;
        this.certificate = certificate;
        this.status = status;
    }

    /**
     * Finaliza a tentativa de avaliação, calculando a nota e definindo o status.
     *
     * @param submittedAnswers Lista de respostas enviadas pelo usuário.
     */
    public void finish(List<Answer> submittedAnswers) {
        this.finishedAt = LocalDateTime.now();
        this.answers = submittedAnswers;

        if (this.assessment instanceof SystemAssessment) {
            calculateSystemAssessmentResult(submittedAnswers);
        } else if (this.assessment instanceof PersonalizedAssessment) {
            // Para avaliações personalizadas, o status é COMPLETED (aguardando revisão do Recruiter)
            // Mas ainda calculamos a nota das questões de múltipla escolha para referência
            calculatePartialScore();
            this.status = AssessmentAttemptStatus.COMPLETED;
        }
    }

    private void calculateSystemAssessmentResult(List<Answer> submittedAnswers) {
        if (hasManualSubmission(submittedAnswers)) {
            calculatePartialScore();
            this.status = AssessmentAttemptStatus.COMPLETED;
            return;
        }

        int correctAnswers = countCorrectAnswers();
        int totalQuestions = questions.size();

        if (totalQuestions > 0) {
            this.accuracyRate = (int) (((double) correctAnswers / totalQuestions) * 100);
        } else {
            this.accuracyRate = 0;
        }

        // Regra de aprovação: >= 70%
        if (this.accuracyRate >= 70) {
            this.status = AssessmentAttemptStatus.APPROVED;
        } else {
            this.status = AssessmentAttemptStatus.FAILED;
        }
    }

    private boolean hasManualSubmission(List<Answer> submittedAnswers) {
        return submittedAnswers != null && submittedAnswers.stream()
                .anyMatch(answer -> hasManualPayload(answer));
    }

    private void calculatePartialScore() {
        // Apenas calcula a taxa de acerto para questões de múltipla escolha, sem mudar status para APPROVED/FAILED
        int correctAnswers = countCorrectAnswers();
        int totalQuestions = questions.size(); // Considera todas as questões no denominador
        // Assumindo todas para manter consistência percentual global, mesmo que projetos não tenham "acerto automático"
        
        if (totalQuestions > 0) {
            this.accuracyRate = (int) (((double) correctAnswers / totalQuestions) * 100);
        } else {
            this.accuracyRate = 0;
        }
    }

    private int countCorrectAnswers() {
        if (questions == null || questions.isEmpty() || answers == null || answers.isEmpty()) {
            return 0;
        }

        // Cria um mapa das respostas para acesso rápido por ID da questão
        Map<UUID, Answer> answersMap = answers.stream()
                .collect(Collectors.toMap(Answer::getQuestionId, a -> a));

        int correctCount = 0;

        for (Question question : questions) {
            Answer answer = answersMap.get(question.getId());

            if (question instanceof MultipleChoiceQuestion) {
                if (isAnswerCorrect((MultipleChoiceQuestion) question, answer)) {
                    correctCount++;
                }
            }
            // Questões de projeto (ProjectQuestion) requerem correção manual, então não contam como "acerto automático" aqui.
        }

        return correctCount;
    }

    private boolean hasManualPayload(Answer answer) {
        if (answer == null) {
            return false;
        }

        return hasText(answer.getProjectUrl()) || hasText(answer.getTextResponse());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isAnswerCorrect(MultipleChoiceQuestion question, Answer answer) {
        if (answer == null || answer.getSelectedAlternativeId() == null) {
            return false; // Não respondida ou nula conta como errada
        }

        return question.getAlternatives().stream()
                .filter(Alternative::correct)
                .findFirst()
                .map(correctAlt -> correctAlt.id().equals(answer.getSelectedAlternativeId()))
                .orElse(false);
    }

    public boolean isTimeExpired() {
        if (startedAt == null || assessment.getEvaluationTimer() == null) {
            return false;
        }
        // Dá uma tolerância de 1 minuto para latência de rede
        return LocalDateTime.now().isAfter(startedAt.plus(assessment.getEvaluationTimer()).plusMinutes(1));
    }

    public UUID getId() {
        return id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<FeedbackAssessment> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<FeedbackAssessment> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(Integer accuracyRate) {
        this.accuracyRate = accuracyRate;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public AssessmentAttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentAttemptStatus status) {
        this.status = status;
    }
}
