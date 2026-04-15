package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.KnowledgeArea;

import java.util.List;
import java.util.Set;

public class JobDescriptionAnalysis {
    private String originalJobDescription;
    private Set<KnowledgeArea> suggestedKnowledgeAreas;
    private List<String> suggestedHardSkills;
    private List<String> suggestedSoftSkills;
    private AssessmentCriteriaWeights suggestedCriteriaWeights;

    public JobDescriptionAnalysis(String originalJobDescription, Set<KnowledgeArea> suggestedKnowledgeAreas,
                                  List<String> suggestedHardSkills, List<String> suggestedSoftSkills,
                                  AssessmentCriteriaWeights suggestedCriteriaWeights) {
        setOriginalJobDescription(originalJobDescription);
        setSuggestedKnowledgeAreas(suggestedKnowledgeAreas);
        setSuggestedHardSkills(suggestedHardSkills);
        setSuggestedSoftSkills(suggestedSoftSkills);
        setSuggestedCriteriaWeights(suggestedCriteriaWeights);
    }

    public String getOriginalJobDescription() {
        return originalJobDescription;
    }

    public void setOriginalJobDescription(String originalJobDescription) {
        if (originalJobDescription == null || originalJobDescription.isBlank()) {
            throw new IllegalArgumentException("Job description must not be blank.");
        }
        this.originalJobDescription = originalJobDescription;
    }

    public Set<KnowledgeArea> getSuggestedKnowledgeAreas() {
        return suggestedKnowledgeAreas;
    }

    public void setSuggestedKnowledgeAreas(Set<KnowledgeArea> suggestedKnowledgeAreas) {
        this.suggestedKnowledgeAreas = suggestedKnowledgeAreas == null ? Set.of() : Set.copyOf(suggestedKnowledgeAreas);
    }

    public List<String> getSuggestedHardSkills() {
        return suggestedHardSkills;
    }

    public void setSuggestedHardSkills(List<String> suggestedHardSkills) {
        this.suggestedHardSkills = suggestedHardSkills == null ? List.of() : List.copyOf(suggestedHardSkills);
    }

    public List<String> getSuggestedSoftSkills() {
        return suggestedSoftSkills;
    }

    public void setSuggestedSoftSkills(List<String> suggestedSoftSkills) {
        this.suggestedSoftSkills = suggestedSoftSkills == null ? List.of() : List.copyOf(suggestedSoftSkills);
    }

    public AssessmentCriteriaWeights getSuggestedCriteriaWeights() {
        return suggestedCriteriaWeights;
    }

    public void setSuggestedCriteriaWeights(AssessmentCriteriaWeights suggestedCriteriaWeights) {
        this.suggestedCriteriaWeights = suggestedCriteriaWeights == null
                ? AssessmentCriteriaWeights.defaultWeights()
                : suggestedCriteriaWeights;
    }
}
