package com.lia.liaprove.infrastructure.services.algorithms.genetic;

import com.lia.liaprove.application.gateways.algorithms.genetic.GeneticGateway;
import com.lia.liaprove.core.algorithms.genetic.RecruiterMetrics;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.metrics.ReactionType;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.infrastructure.entities.users.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class GeneticGatewayImpl implements GeneticGateway {

    private final UserJpaRepository userJpaRepository;
    private final AssessmentJpaRepository assessmentJpaRepository;
    private final AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;
    private final QuestionJpaRepository questionJpaRepository;
    private final FeedbackQuestionJpaRepository feedbackQuestionJpaRepository;
    private final int recentWindowDays;

    public GeneticGatewayImpl(
            UserJpaRepository userJpaRepository,
            AssessmentJpaRepository assessmentJpaRepository,
            AssessmentAttemptJpaRepository assessmentAttemptJpaRepository,
            QuestionJpaRepository questionJpaRepository,
            FeedbackQuestionJpaRepository feedbackQuestionJpaRepository,
            @Value("${ga.metrics.recent-window-days:30}") int recentWindowDays
    ) {
        this.userJpaRepository = userJpaRepository;
        this.assessmentJpaRepository = assessmentJpaRepository;
        this.assessmentAttemptJpaRepository = assessmentAttemptJpaRepository;
        this.questionJpaRepository = questionJpaRepository;
        this.feedbackQuestionJpaRepository = feedbackQuestionJpaRepository;
        this.recentWindowDays = recentWindowDays;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruiterMetrics> fetchAllRecruiterMetrics() {
        List<UserRecruiterEntity> recruiters = userJpaRepository.findAllRecruiters();
        if (recruiters == null || recruiters.isEmpty()) {
            return List.of();
        }

        Map<UUID, Integer> totalAssessments = toCountMap(assessmentJpaRepository.countAssessmentsByRecruiter());
        Map<UUID, Integer> recentAssessments = toCountMap(
                assessmentJpaRepository.countRecentAssessmentsByRecruiter(LocalDateTime.now().minusDays(recentWindowDays))
        );
        Map<UUID, Double> avgAccuracy = toAvgMap(
                assessmentAttemptJpaRepository.avgAccuracyByRecruiter(List.of(
                        AssessmentAttemptStatus.COMPLETED,
                        AssessmentAttemptStatus.APPROVED,
                        AssessmentAttemptStatus.FAILED
                ))
        );
        Map<UUID, Integer> approvedQuestions = toCountMap(
                questionJpaRepository.countByAuthorAndStatus(QuestionStatus.APPROVED)
        );
        Map<UUID, ReactionCounts> reactions = toReactionCountsMap(
                feedbackQuestionJpaRepository.countReactionsByFeedbackAuthor(ReactionType.LIKE, ReactionType.DISLIKE)
        );

        List<RecruiterMetrics> result = new ArrayList<>(recruiters.size());

        for (UserRecruiterEntity recruiter : recruiters) {
            UUID recruiterId = recruiter.getId();
            int total = totalAssessments.getOrDefault(recruiterId, 0);
            int recent = recentAssessments.getOrDefault(recruiterId, 0);
            double avgAcc = avgAccuracy.getOrDefault(recruiterId, 0.0);

            /*
             * avgAssessmentRating: media de accuracyRate (0..100) convertida para 0..5
             * via divisão por 20.0.
             */
            double avgAssessmentRating = toAssessmentRating(avgAcc);

            int questionsApproved = approvedQuestions.getOrDefault(recruiterId, 0);
            ReactionCounts reactionCounts = reactions.getOrDefault(recruiterId, new ReactionCounts(0, 0));

            int likes = reactionCounts.likes;
            int dislikes = reactionCounts.dislikes;
            double likeRatio = (likes + dislikes == 0) ? 0.0 : (double) likes / (double) (likes + dislikes);

            double recruiterRating = recruiter.getRecruiterRating() == null ? 0.0 : recruiter.getRecruiterRating();

            RecruiterMetrics metrics = new RecruiterMetrics(
                    recruiterId,
                    recruiter.getVoteWeight(),
                    total,
                    recent,
                    avgAssessmentRating,
                    questionsApproved,
                    likes,
                    dislikes,
                    likeRatio,
                    recruiterRating
            );

            result.add(metrics);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterMetrics fetchRecruiterMetrics(UUID recruiterId) {
        if (recruiterId == null) {
            return null;
        }
        return fetchAllRecruiterMetrics().stream()
                .filter(m -> Objects.equals(m.getRecruiterId(), recruiterId))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public void updateVoteWeight(UUID recruiterId, int newWeight) {
        if (recruiterId == null) {
            return;
        }
        int bounded = Math.max(1, Math.min(10, newWeight));
        userJpaRepository.findById(recruiterId).ifPresent(entity -> {
            if (entity instanceof UserRecruiterEntity recruiter) {
                recruiter.setVoteWeight(bounded);
                userJpaRepository.save(recruiter);
            }
        });
    }

    private Map<UUID, Integer> toCountMap(List<Object[]> rows) {
        Map<UUID, Integer> result = new HashMap<>();
        if (rows == null) {
            return result;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 2) {
                continue;
            }
            UUID id = (UUID) row[0];
            Number count = (Number) row[1];
            if (id != null && count != null) {
                result.put(id, count.intValue());
            }
        }
        return result;
    }

    private Map<UUID, Double> toAvgMap(List<Object[]> rows) {
        Map<UUID, Double> result = new HashMap<>();
        if (rows == null) {
            return result;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 2) {
                continue;
            }
            UUID id = (UUID) row[0];
            Number avg = (Number) row[1];
            if (id != null && avg != null) {
                result.put(id, avg.doubleValue());
            }
        }
        return result;
    }

    private Map<UUID, ReactionCounts> toReactionCountsMap(List<Object[]> rows) {
        Map<UUID, ReactionCounts> result = new HashMap<>();
        if (rows == null) {
            return result;
        }
        for (Object[] row : rows) {
            if (row == null || row.length < 3) {
                continue;
            }
            UUID id = (UUID) row[0];
            Number likes = (Number) row[1];
            Number dislikes = (Number) row[2];
            if (id != null) {
                result.put(id, new ReactionCounts(
                        likes == null ? 0 : likes.intValue(),
                        dislikes == null ? 0 : dislikes.intValue()
                ));
            }
        }
        return result;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // Converte media de accuracyRate (0..100) para escala 0..5 dividindo por 20.0.
    private double toAssessmentRating(double avgAccuracy) {
        return clamp(avgAccuracy / 20.0, 0.0, 5.0);
    }

    private static class ReactionCounts {
        private final int likes;
        private final int dislikes;

        private ReactionCounts(int likes, int dislikes) {
            this.likes = likes;
            this.dislikes = dislikes;
        }
    }
}




