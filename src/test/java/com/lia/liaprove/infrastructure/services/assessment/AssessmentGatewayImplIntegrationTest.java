package com.lia.liaprove.infrastructure.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.sql.init.mode=never")
@ActiveProfiles("dev")
class AssessmentGatewayImplIntegrationTest {

    @Autowired
    private AssessmentGateway assessmentGateway;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @AfterEach
    void tearDown() {
        assessmentJpaRepository.deleteAll();
    }

    @Test
    void shouldPersistAndReloadJobDescriptionAnalysisSnapshot() {
        PersonalizedAssessment savedAssessment = (PersonalizedAssessment) assessmentGateway.save(personalizedAssessment(
                "Assessment with saved analysis",
                LocalDateTime.now().plusDays(5),
                PersonalizedAssessmentStatus.ACTIVE,
                "token-persist-and-reload"
        ));

        Assessment reloadedAssessment = assessmentGateway.findById(savedAssessment.getId()).orElseThrow();

        assertThat(reloadedAssessment).isInstanceOf(PersonalizedAssessment.class);
        assertSnapshot((PersonalizedAssessment) reloadedAssessment);
    }

    @Test
    void shouldLoadJobDescriptionAnalysisWhenListingExpiredAssessments() {
        assessmentGateway.save(personalizedAssessment(
                "Expired assessment with analysis",
                LocalDateTime.now().minusHours(2),
                PersonalizedAssessmentStatus.ACTIVE,
                "token-expired-analysis"
        ));

        List<PersonalizedAssessment> expiredAssessments = assessmentGateway.findActiveAssessmentsWithPastExpirationDate();

        assertThat(expiredAssessments).hasSize(1);
        assertSnapshot(expiredAssessments.getFirst());
    }

    private PersonalizedAssessment personalizedAssessment(
            String title,
            LocalDateTime expirationDate,
            PersonalizedAssessmentStatus status,
            String shareableToken
    ) {
        return new PersonalizedAssessment(
                null,
                title,
                "Assessment used to validate persisted job description analysis",
                LocalDateTime.now(),
                List.of(),
                Duration.ofMinutes(45),
                null,
                expirationDate,
                0,
                3,
                shareableToken,
                status,
                new AssessmentCriteriaWeights(40, 35, 25),
                new JobDescriptionAnalysis(
                        "Senior backend engineer focused on Java and distributed systems.",
                        Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT, KnowledgeArea.DATABASE),
                        List.of("Java", "Spring Boot", "Distributed systems"),
                        List.of("Communication", "Ownership"),
                        new AssessmentCriteriaWeights(50, 30, 20)
                )
        );
    }

    private void assertSnapshot(PersonalizedAssessment assessment) {
        assertThat(assessment.getCriteriaWeights()).isEqualTo(new AssessmentCriteriaWeights(40, 35, 25));
        assertThat(assessment.getJobDescriptionAnalysis()).isNotNull();
        assertThat(assessment.getJobDescriptionAnalysis().getOriginalJobDescription())
                .isEqualTo("Senior backend engineer focused on Java and distributed systems.");
        assertThat(assessment.getJobDescriptionAnalysis().getSuggestedKnowledgeAreas())
                .containsExactlyInAnyOrder(KnowledgeArea.SOFTWARE_DEVELOPMENT, KnowledgeArea.DATABASE);
        assertThat(assessment.getJobDescriptionAnalysis().getSuggestedHardSkills())
                .containsExactly("Java", "Spring Boot", "Distributed systems");
        assertThat(assessment.getJobDescriptionAnalysis().getSuggestedSoftSkills())
                .containsExactly("Communication", "Ownership");
        assertThat(assessment.getJobDescriptionAnalysis().getSuggestedCriteriaWeights())
                .isEqualTo(new AssessmentCriteriaWeights(50, 30, 20));
    }
}
