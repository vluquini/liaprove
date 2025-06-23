package com.lia.liaprove.core.domain.question;

import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public class ProjectQuestion extends Question {
    // Link do projeto desenvolvido pelo Usuário
    private String projectUrl;

    public ProjectQuestion(UUID id, String title, String description, DifficultyLevel difficultyLevel, EnumMap<DifficultyLevel, Integer> difficultyLevelVotes, List<KnowledgeArea> knowledgeAreas, List<FeedbackQuestion> feedbacks, int[] relevanceVotes, LocalDateTime submissionDate, QuestionStatus status, Integer totalVotes, Boolean preEvaluatedByLLM, byte relevanceByLLM, Boolean isApproved, int upVote, int downVote, int recruiterUsageCount) {
        super(id, title, description, difficultyLevel, difficultyLevelVotes, knowledgeAreas, feedbacks, relevanceVotes, submissionDate, status, preEvaluatedByLLM, relevanceByLLM, isApproved, upVote, downVote, recruiterUsageCount);
        this.projectUrl = projectUrl;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }
}
