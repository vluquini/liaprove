package com.lia.liaprove.infrastructure.controllers.user;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.CertificateEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = "classpath:db/h2-populate-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ListMyCertificatesEndpointIntegrationTest {

    private static final String DEV_USER_HEADER = "X-Dev-User-Email";
    private static final String CANDIDATE_EMAIL = "carlos.silva@example.com";
    private static final String RECRUITER_EMAIL = "ana.p@techrecruit.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;

    @AfterEach
    void tearDown() {
        assessmentAttemptJpaRepository.deleteAll();
        assessmentJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should list only certificates owned by the authenticated user")
    void shouldListOnlyCertificatesOwnedByAuthenticatedUser() throws Exception {
        UserEntity candidate = getSeededUser(CANDIDATE_EMAIL);
        UserEntity otherUser = getSeededUser("junior.dev@example.com");
        UserEntity recruiter = getSeededUser(RECRUITER_EMAIL);
        PersonalizedAssessmentEntity assessment = createPersonalizedAssessment(recruiter);
        createCertifiedAttempt(assessment, candidate, "CERT-ME-1", LocalDate.of(2026, 5, 16), 92F);
        createCertifiedAttempt(assessment, otherUser, "CERT-OTHER-1", LocalDate.of(2026, 5, 15), 88F);

        mockMvc.perform(get("/api/v1/users/me/certificates")
                        .header(DEV_USER_HEADER, candidate.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].certificateNumber").value("CERT-ME-1"))
                .andExpect(jsonPath("$[0].title").value("Certificado de Conclusão"))
                .andExpect(jsonPath("$[0].score").value(92.0));
    }

    @Test
    @DisplayName("Should return empty list when authenticated user has no certificates")
    void shouldReturnEmptyListWhenUserHasNoCertificates() throws Exception {
        UserEntity candidate = getSeededUser(CANDIDATE_EMAIL);

        mockMvc.perform(get("/api/v1/users/me/certificates")
                        .header(DEV_USER_HEADER, candidate.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should return unauthorized when header is missing")
    void shouldReturnUnauthorizedWhenHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/certificates"))
                .andExpect(status().isUnauthorized());
    }

    private void createCertifiedAttempt(
            PersonalizedAssessmentEntity assessment,
            UserEntity user,
            String certificateNumber,
            LocalDate issueDate,
            Float score
    ) {
        CertificateEntity certificate = new CertificateEntity();
        certificate.setCertificateNumber(certificateNumber);
        certificate.setTitle("Certificado de Conclusão");
        certificate.setDescription("Certificado emitido pelo LIA Prove.");
        certificate.setCertificateUrl("https://liaprove.com/certificates/" + certificateNumber);
        certificate.setIssueDate(issueDate);
        certificate.setScore(score);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(assessment);
        attempt.setUser(user);
        attempt.setStartedAt(LocalDateTime.now().minusHours(2));
        attempt.setFinishedAt(LocalDateTime.now().minusHours(1));
        attempt.setAccuracyRate(score.intValue());
        attempt.setStatus(AssessmentAttemptStatus.APPROVED);
        attempt.setCertificate(certificate);
        attempt.setQuestions(List.of());

        assessmentAttemptJpaRepository.save(attempt);
    }

    private UserEntity getSeededUser(String email) {
        return userJpaRepository.findByEmail(email).orElseThrow();
    }

    private PersonalizedAssessmentEntity createPersonalizedAssessment(UserEntity recruiter) {
        PersonalizedAssessmentEntity assessment = new PersonalizedAssessmentEntity();
        assessment.setTitle("Certificate Assessment " + UUID.randomUUID());
        assessment.setDescription("Assessment with certificates.");
        assessment.setCreationDate(LocalDateTime.now());
        assessment.setCreatedBy((UserRecruiterEntity) recruiter);
        assessment.setShareableToken("certificates-" + UUID.randomUUID());
        assessment.setStatus(PersonalizedAssessmentStatus.ACTIVE);
        assessment.setMaxAttempts(3);
        assessment.setEvaluationTimerSeconds(1800L);
        assessment.setQuestions(List.of());
        return assessmentJpaRepository.save(assessment);
    }
}
