package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapperImpl;
import com.lia.liaprove.infrastructure.mappers.user.UserMapperImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentMapperTest {

    private final AssessmentMapperImpl mapper = configuredMapper();

    @Test
    void shouldRoundTripJobDescriptionAnalysisThroughPersonalizedAssessmentEntity() throws Exception {
        JobDescriptionAnalysis analysis = new JobDescriptionAnalysis(
                "Senior Java developer role",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT, KnowledgeArea.AI),
                List.of("Java", "Spring Boot"),
                List.of("Communication", "Teamwork"),
                new AssessmentCriteriaWeights(50, 30, 20)
        );

        PersonalizedAssessment assessment = new PersonalizedAssessment(
                UUID.randomUUID(),
                "Backend assessment",
                "Assessment with analysis snapshot",
                LocalDateTime.of(2026, 4, 15, 10, 30),
                null,
                Duration.ofMinutes(45),
                null,
                LocalDateTime.of(2026, 4, 20, 10, 30),
                1,
                3,
                "shareable-token",
                PersonalizedAssessmentStatus.ACTIVE,
                new AssessmentCriteriaWeights(40, 35, 25),
                analysis
        );

        PersonalizedAssessmentEntity entity = (PersonalizedAssessmentEntity) mapper.toEntity(assessment);

        assertThat(readField(entity, "originalJobDescription")).isEqualTo("Senior Java developer role");
        assertThat(readSetField(entity, "suggestedKnowledgeAreas")).containsExactlyInAnyOrder(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                KnowledgeArea.AI
        );
        assertThat(readListField(entity, "suggestedHardSkills")).containsExactly("Java", "Spring Boot");
        assertThat(readListField(entity, "suggestedSoftSkills")).containsExactly("Communication", "Teamwork");
        assertThat(readField(entity, "suggestedHardSkillsWeight")).isEqualTo(50);
        assertThat(readField(entity, "suggestedSoftSkillsWeight")).isEqualTo(30);
        assertThat(readField(entity, "suggestedExperienceWeight")).isEqualTo(20);

        PersonalizedAssessment mappedBack = (PersonalizedAssessment) mapper.toDomain(entity);
        JobDescriptionAnalysis mappedAnalysis = mappedBack.getJobDescriptionAnalysis();

        assertThat(mappedBack.getCriteriaWeights()).isEqualTo(new AssessmentCriteriaWeights(40, 35, 25));
        assertThat(mappedAnalysis.getOriginalJobDescription()).isEqualTo("Senior Java developer role");
        assertThat(mappedAnalysis.getSuggestedKnowledgeAreas()).containsExactlyInAnyOrder(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                KnowledgeArea.AI
        );
        assertThat(mappedAnalysis.getSuggestedHardSkills()).containsExactly("Java", "Spring Boot");
        assertThat(mappedAnalysis.getSuggestedSoftSkills()).containsExactly("Communication", "Teamwork");
        assertThat(mappedAnalysis.getSuggestedCriteriaWeights()).isEqualTo(new AssessmentCriteriaWeights(50, 30, 20));
    }

    private static Object readField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private static AssessmentMapperImpl configuredMapper() {
        AssessmentMapperImpl mapper = new AssessmentMapperImpl();
        setField(mapper, "userMapper", new UserMapperImpl());
        setField(mapper, "questionMapper", new QuestionMapperImpl());
        return mapper;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to configure mapper test fixture", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Set<KnowledgeArea> readSetField(Object target, String fieldName) throws Exception {
        return (Set<KnowledgeArea>) readField(target, fieldName);
    }

    @SuppressWarnings("unchecked")
    private static List<String> readListField(Object target, String fieldName) throws Exception {
        return (List<String>) readField(target, fieldName);
    }
}
