package com.lia.liaprove.core.domain.question;

import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public class MultipleChoiceQuestion extends Question {
    // Lista de opções de resposta
    private List<String> choices;
    private String correctAnswer;

    public MultipleChoiceQuestion(UUID id, String title, String description, DifficultyLevel difficultyLevel, EnumMap<DifficultyLevel, Integer> difficultyLevelVotes, List<KnowledgeArea> knowledgeAreas, List<FeedbackQuestion> feedbacks, int[] relevanceVotes, LocalDateTime submissionDate, QuestionStatus status, Integer totalVotes, Boolean preEvaluatedByLLM, byte relevanceByLLM, Boolean isApproved, int upVote, int downVote, int recruiterUsageCount) {
        super(id, title, description, difficultyLevel, difficultyLevelVotes, knowledgeAreas, feedbacks, relevanceVotes, submissionDate, status, preEvaluatedByLLM, relevanceByLLM, isApproved, upVote, downVote, recruiterUsageCount);
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
