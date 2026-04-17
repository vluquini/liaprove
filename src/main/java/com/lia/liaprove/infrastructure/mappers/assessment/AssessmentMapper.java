package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.SystemAssessment;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.assessment.SystemAssessmentEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import org.hibernate.Hibernate;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {QuestionMapper.class, UserMapper.class}
)
public interface AssessmentMapper {

    default AssessmentEntity toEntity(Assessment domain) {
        return switch (domain) {
            case null -> null;
            case SystemAssessment systemAssessment -> toEntity(systemAssessment);
            case PersonalizedAssessment personalizedAssessment -> toEntity(personalizedAssessment);
            default -> throw new IllegalArgumentException("Unknown Assessment subtype: " + domain.getClass());
        };
    }

    @Mapping(source = "evaluationTimer", target = "evaluationTimerSeconds")
    SystemAssessmentEntity toEntity(SystemAssessment domain);

    @Mapping(source = "evaluationTimer", target = "evaluationTimerSeconds")
    PersonalizedAssessmentEntity toEntity(PersonalizedAssessment domain);

    default Assessment toDomain(AssessmentEntity entity) {
        if (entity == null) {
            return null;
        }

        Object unproxied = Hibernate.unproxy(entity);

        return switch (unproxied) {
            case null -> null;
            case SystemAssessmentEntity systemAssessmentEntity -> toDomain(systemAssessmentEntity);
            case PersonalizedAssessmentEntity personalizedAssessmentEntity -> toDomain(personalizedAssessmentEntity);
            default -> throw new IllegalArgumentException("Unknown AssessmentEntity subtype: " + unproxied.getClass());
        };
    }

    @Mapping(source = "evaluationTimerSeconds", target = "evaluationTimer")
    SystemAssessment toDomain(SystemAssessmentEntity entity);

    @Mapping(source = "evaluationTimerSeconds", target = "evaluationTimer")
    PersonalizedAssessment toDomain(PersonalizedAssessmentEntity entity);

    @ObjectFactory
    default PersonalizedAssessment createPersonalizedAssessment(PersonalizedAssessmentEntity entity) {
        if (entity == null) {
            return null;
        }

        return new PersonalizedAssessment(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCreationDate(),
                null,
                map(entity.getEvaluationTimerSeconds()),
                null,
                entity.getExpirationDate(),
                entity.getTotalAttempts(),
                entity.getMaxAttempts(),
                entity.getShareableToken(),
                entity.getStatus(),
                mapCriteriaWeights(entity),
                mapJobDescriptionAnalysis(entity)
        );
    }

    default Long map(Duration duration) {
        return duration == null ? null : duration.getSeconds();
    }

    default Duration map(Long seconds) {
        return seconds == null ? null : Duration.ofSeconds(seconds);
    }

    default AssessmentCriteriaWeights mapCriteriaWeights(PersonalizedAssessmentEntity entity) {
        if (entity == null) {
            return null;
        }

        return new AssessmentCriteriaWeights(
                entity.getHardSkillsWeight(),
                entity.getSoftSkillsWeight(),
                entity.getExperienceWeight()
        );
    }

    default void mapCriteriaWeights(AssessmentCriteriaWeights weights, PersonalizedAssessmentEntity entity) {
        AssessmentCriteriaWeights source = weights == null ? AssessmentCriteriaWeights.defaultWeights() : weights;
        entity.setHardSkillsWeight(source.getHardSkillsWeight());
        entity.setSoftSkillsWeight(source.getSoftSkillsWeight());
        entity.setExperienceWeight(source.getExperienceWeight());
    }

    default JobDescriptionAnalysis mapJobDescriptionAnalysis(PersonalizedAssessmentEntity entity) {
        if (entity == null || entity.getOriginalJobDescription() == null) {
            return null;
        }

        AssessmentCriteriaWeights suggestedCriteriaWeights = null;
        if (entity.getSuggestedHardSkillsWeight() != null
                && entity.getSuggestedSoftSkillsWeight() != null
                && entity.getSuggestedExperienceWeight() != null) {
            suggestedCriteriaWeights = new AssessmentCriteriaWeights(
                    entity.getSuggestedHardSkillsWeight(),
                    entity.getSuggestedSoftSkillsWeight(),
                    entity.getSuggestedExperienceWeight()
            );
        }

        return new JobDescriptionAnalysis(
                entity.getOriginalJobDescription(),
                entity.getSuggestedKnowledgeAreas(),
                entity.getSuggestedHardSkills(),
                entity.getSuggestedSoftSkills(),
                suggestedCriteriaWeights
        );
    }

    default void mapJobDescriptionAnalysis(JobDescriptionAnalysis analysis, PersonalizedAssessmentEntity entity) {
        if (analysis == null) {
            entity.setOriginalJobDescription(null);
            entity.setSuggestedKnowledgeAreas(null);
            entity.setSuggestedHardSkills(null);
            entity.setSuggestedSoftSkills(null);
            entity.setSuggestedHardSkillsWeight(null);
            entity.setSuggestedSoftSkillsWeight(null);
            entity.setSuggestedExperienceWeight(null);
            return;
        }

        entity.setOriginalJobDescription(analysis.getOriginalJobDescription());
        entity.setSuggestedKnowledgeAreas(analysis.getSuggestedKnowledgeAreas() == null
                ? null
                : new LinkedHashSet<>(analysis.getSuggestedKnowledgeAreas()));
        entity.setSuggestedHardSkills(analysis.getSuggestedHardSkills() == null
                ? null
                : new ArrayList<>(analysis.getSuggestedHardSkills()));
        entity.setSuggestedSoftSkills(analysis.getSuggestedSoftSkills() == null
                ? null
                : new ArrayList<>(analysis.getSuggestedSoftSkills()));

        AssessmentCriteriaWeights suggestedCriteriaWeights = analysis.getSuggestedCriteriaWeights();
        entity.setSuggestedHardSkillsWeight(suggestedCriteriaWeights.getHardSkillsWeight());
        entity.setSuggestedSoftSkillsWeight(suggestedCriteriaWeights.getSoftSkillsWeight());
        entity.setSuggestedExperienceWeight(suggestedCriteriaWeights.getExperienceWeight());
    }

    @AfterMapping
    default void afterToEntity(PersonalizedAssessment domain, @MappingTarget PersonalizedAssessmentEntity entity) {
        mapCriteriaWeights(domain.getCriteriaWeights(), entity);
        mapJobDescriptionAnalysis(domain.getJobDescriptionAnalysis(), entity);
    }

    @AfterMapping
    default void afterToDomain(PersonalizedAssessmentEntity entity, @MappingTarget PersonalizedAssessment domain) {
        domain.setCriteriaWeights(mapCriteriaWeights(entity));
        domain.setJobDescriptionAnalysis(mapJobDescriptionAnalysis(entity));
    }
}

