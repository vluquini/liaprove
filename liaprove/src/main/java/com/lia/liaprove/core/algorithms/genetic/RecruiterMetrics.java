package com.lia.liaprove.core.algorithms.genetic;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO imutável com métricas agregadas de um recruiter.
 * Produzido pela layer infra e consumido pelo GA na layer application.
 */
public final class RecruiterMetrics {
    private final UUID recruiterId;
    private final Integer currentVoteWeight;   // atual (nullable)
    private final int totalAssessmentsCreated; // total histórico
    private final int recentAssessmentsCount;  // janela (ex.: últimas 4 semanas)
    private final double avgAssessmentRating;  // 0..5 (nullable -> 0)
    private final int questionsApprovedCount;  // total de questões do recruiter aprovadas
    private final int feedbackLikes;           // total likes em comentários (FeedbackQuestion)
    private final int feedbackDislikes;        // total dislikes em comentários
    private final double commentLikeRatio;     // 0..1 (likes/(likes+dislikes))
    private final double recruiterRating;      // rating agregado (0..5) se existir

    public RecruiterMetrics(UUID recruiterId, Integer currentVoteWeight, int totalAssessmentsCreated, int recentAssessmentsCount,
                            double avgAssessmentRating, int questionsApprovedCount, int feedbackLikes, int feedbackDislikes,
                            double commentLikeRatio, double recruiterRating, Map<String, Object> extras) {
        this.recruiterId = Objects.requireNonNull(recruiterId);
        this.currentVoteWeight = currentVoteWeight;
        this.totalAssessmentsCreated = Math.max(0, totalAssessmentsCreated);
        this.recentAssessmentsCount = Math.max(0, recentAssessmentsCount);
        this.avgAssessmentRating = Double.isFinite(avgAssessmentRating) ? avgAssessmentRating : 0.0;
        this.questionsApprovedCount = Math.max(0, questionsApprovedCount);
        this.feedbackLikes = Math.max(0, feedbackLikes);
        this.feedbackDislikes = Math.max(0, feedbackDislikes);
        this.commentLikeRatio = (Double.isFinite(commentLikeRatio) && commentLikeRatio >= 0.0 && commentLikeRatio <= 1.0)
                                ? commentLikeRatio : computeLikeRatioSafe(feedbackLikes, feedbackDislikes);
        this.recruiterRating = Double.isFinite(recruiterRating) ? recruiterRating : 0.0;
    }

    public UUID getRecruiterId() { return recruiterId; }
    public Integer getCurrentVoteWeight() { return currentVoteWeight; }
    public int getTotalAssessmentsCreated() { return totalAssessmentsCreated; }
    public int getRecentAssessmentsCount() { return recentAssessmentsCount; }
    public double getAvgAssessmentRating() { return avgAssessmentRating; }
    public int getQuestionsApprovedCount() { return questionsApprovedCount; }
    public int getFeedbackLikes() { return feedbackLikes; }
    public int getFeedbackDislikes() { return feedbackDislikes; }
    public double getCommentLikeRatio() { return commentLikeRatio; }
    public double getRecruiterRating() { return recruiterRating; }

    private static double computeLikeRatioSafe(int likes, int dislikes) {
        int total = likes + dislikes;
        if (total == 0) return 0.0;
        return (double) likes / (double) total;
    }
}
