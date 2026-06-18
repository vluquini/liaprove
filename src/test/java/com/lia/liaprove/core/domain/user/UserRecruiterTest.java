package com.lia.liaprove.core.domain.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRecruiterTest {

    @Test
    void shouldIncrementCreatedAssessmentsFromNullAndExistingValue() {
        UserRecruiter recruiter = new UserRecruiter();

        recruiter.incrementAssessmentsCreated();
        recruiter.incrementAssessmentsCreated();

        assertThat(recruiter.getTotalAssessmentsCreated()).isEqualTo(2);
    }

    @Test
    void shouldUpdateRecruiterRatingUsingIncrementalAverage() {
        UserRecruiter recruiter = new UserRecruiter();
        recruiter.setRecruiterRating(4f);
        recruiter.setRecruiterRatingCount(2);

        recruiter.updateRecruiterRating(1f);

        assertThat(recruiter.getRecruiterRating()).isEqualTo(3f);
        assertThat(recruiter.getRecruiterRatingCount()).isEqualTo(3);
    }

    @Test
    void shouldUpdateRecruiterRatingFromNullMetrics() {
        UserRecruiter recruiter = new UserRecruiter();
        recruiter.setRecruiterRating(null);
        recruiter.setRecruiterRatingCount(null);

        recruiter.updateRecruiterRating(5f);

        assertThat(recruiter.getRecruiterRating()).isEqualTo(5f);
        assertThat(recruiter.getRecruiterRatingCount()).isEqualTo(1);
    }

    @Test
    void shouldRejectRecruiterRatingOutsideAcceptedRange() {
        UserRecruiter recruiter = new UserRecruiter();

        assertThatThrownBy(() -> recruiter.updateRecruiterRating(-0.1f))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("newRating must must be between 0.0 and 5.0.");
        assertThatThrownBy(() -> recruiter.updateRecruiterRating(5.1f))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("newRating must must be between 0.0 and 5.0.");
    }

    @Test
    void shouldCalculateRecruiterEngagementScoreWithCappedInputs() {
        UserRecruiter recruiter = new UserRecruiter();
        recruiter.setTotalAssessmentsCreated(50);
        recruiter.setRecruiterRating(2.5f);

        assertThat(recruiter.getRecruiterEngagementScore()).isEqualTo(0.5d);

        recruiter.setTotalAssessmentsCreated(200);
        recruiter.setRecruiterRating(10f);

        assertThat(recruiter.getRecruiterEngagementScore()).isEqualTo(1.0d);
    }

    @Test
    void shouldAllowAssessmentCreationWhenActiveAndCompanyEmailIsNotBlank() {
        UserRecruiter recruiter = new UserRecruiter();
        recruiter.setStatus(UserStatus.ACTIVE);

        recruiter.setCompanyEmail(" recruiter@company.com ");

        assertThat(recruiter.canCreateAssessment()).isTrue();
    }

    @Test
    void shouldNotAllowAssessmentCreationWhenRecruiterIsInactive() {
        UserRecruiter recruiter = new UserRecruiter();
        recruiter.setStatus(UserStatus.INACTIVE);
        recruiter.setCompanyEmail("recruiter@company.com");

        assertThat(recruiter.canCreateAssessment()).isFalse();
    }

    @Test
    void shouldNotAllowAssessmentCreationWithoutCompanyEmail() {
        UserRecruiter recruiter = new UserRecruiter();

        assertThat(recruiter.canCreateAssessment()).isFalse();

        recruiter.setCompanyEmail(" ");

        assertThat(recruiter.canCreateAssessment()).isFalse();
    }

    @Test
    void shouldUpdateCompanyInfoTrimmingNameAndLowercasingEmail() {
        UserRecruiter recruiter = new UserRecruiter();

        recruiter.updateCompanyInfo(" Acme Inc ", " HR@ACME.COM ");

        assertThat(recruiter.getCompanyName()).isEqualTo("Acme Inc");
        assertThat(recruiter.getCompanyEmail()).isEqualTo("hr@acme.com");
    }

    @Test
    void shouldRejectInvalidCompanyInfo() {
        UserRecruiter recruiter = new UserRecruiter();

        assertThatThrownBy(() -> recruiter.updateCompanyInfo(null, "hr@acme.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("companyName must not be null/blank");
        assertThatThrownBy(() -> recruiter.updateCompanyInfo(" ", "hr@acme.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("companyName must not be null/blank");
        assertThatThrownBy(() -> recruiter.updateCompanyInfo("Acme", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("companyEmail must not be null/blank");
        assertThatThrownBy(() -> recruiter.updateCompanyInfo("Acme", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("companyEmail must not be null/blank");
        assertThatThrownBy(() -> recruiter.updateCompanyInfo("Acme", "invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("companyEmail seems invalid");
    }
}
