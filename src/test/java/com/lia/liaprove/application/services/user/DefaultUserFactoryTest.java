package com.lia.liaprove.application.services.user;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultUserFactoryTest {

    private final DefaultUserFactory factory = new DefaultUserFactory();

    @Test
    void shouldCreateAdminAsRecruiterCompatibleUser() {
        User user = factory.create(new UserCreateDto(
                "Admin",
                "admin@example.com",
                "hashed",
                "Admin",
                ExperienceLevel.SENIOR,
                UserRole.ADMIN,
                List.of(),
                List.of(),
                null,
                null
        ));

        assertThat(user).isInstanceOf(UserRecruiter.class);
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(user.getRole().isRecruiter()).isTrue();
    }
}
