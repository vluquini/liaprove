package com.lia.liaprove.core.domain.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Test
    void shouldIdentifyAdminRole() {
        assertThat(UserRole.ADMIN.isAdmin()).isTrue();
        assertThat(UserRole.ADMIN.isRecruiter()).isTrue();
        assertThat(UserRole.ADMIN.isProfessional()).isFalse();
    }

    @Test
    void shouldIdentifyRecruiterRole() {
        assertThat(UserRole.RECRUITER.isAdmin()).isFalse();
        assertThat(UserRole.RECRUITER.isRecruiter()).isTrue();
        assertThat(UserRole.RECRUITER.isProfessional()).isFalse();
    }

    @Test
    void shouldIdentifyProfessionalRole() {
        assertThat(UserRole.PROFESSIONAL.isAdmin()).isFalse();
        assertThat(UserRole.PROFESSIONAL.isRecruiter()).isFalse();
        assertThat(UserRole.PROFESSIONAL.isProfessional()).isTrue();
    }
}
