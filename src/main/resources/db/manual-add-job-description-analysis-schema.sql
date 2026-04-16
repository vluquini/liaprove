-- Manual migration for environments that use an existing schema with
-- spring.jpa.hibernate.ddl-auto=validate (for example, the future prod profile).
-- Execute this script before deploying the version that persists
-- PersonalizedAssessment.jobDescriptionAnalysis.

ALTER TABLE assessments
    ADD COLUMN IF NOT EXISTS original_job_description TEXT,
    ADD COLUMN IF NOT EXISTS suggested_hard_skills_weight INTEGER,
    ADD COLUMN IF NOT EXISTS suggested_soft_skills_weight INTEGER,
    ADD COLUMN IF NOT EXISTS suggested_experience_weight INTEGER;

CREATE TABLE IF NOT EXISTS personalized_assessment_job_description_knowledge_areas (
    personalized_assessment_id UUID NOT NULL,
    knowledge_area VARCHAR(64) NOT NULL,
    PRIMARY KEY (personalized_assessment_id, knowledge_area),
    CONSTRAINT fk_pa_job_description_knowledge_areas_assessment
        FOREIGN KEY (personalized_assessment_id) REFERENCES assessments(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS personalized_assessment_job_description_hard_skills (
    personalized_assessment_id UUID NOT NULL,
    skill_order INTEGER NOT NULL,
    skill VARCHAR(255) NOT NULL,
    PRIMARY KEY (personalized_assessment_id, skill_order),
    CONSTRAINT fk_pa_job_description_hard_skills_assessment
        FOREIGN KEY (personalized_assessment_id) REFERENCES assessments(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS personalized_assessment_job_description_soft_skills (
    personalized_assessment_id UUID NOT NULL,
    skill_order INTEGER NOT NULL,
    skill VARCHAR(255) NOT NULL,
    PRIMARY KEY (personalized_assessment_id, skill_order),
    CONSTRAINT fk_pa_job_description_soft_skills_assessment
        FOREIGN KEY (personalized_assessment_id) REFERENCES assessments(id) ON DELETE CASCADE
);
