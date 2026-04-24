package com.lia.liaprove.infrastructure.controllers.user;

import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = "classpath:db/h2-populate-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DeactivateOwnAccountEndpointIntegrationTest {

    private static final String DEV_USER_HEADER = "X-Dev-User-Email";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should deactivate own account successfully")
    void shouldDeactivateOwnAccountSuccessfully() throws Exception {
        UserEntity user = getSeededUser("junior.dev@example.com");

        mockMvc.perform(patch("/api/v1/users/me/deactivate")
                        .header(DEV_USER_HEADER, user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().string("Account deactivated successfully. It will be permanently deleted in 60 days unless you log in again."));

        UserEntity updated = userJpaRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("Should return unauthorized when header is missing")
    void shouldReturnUnauthorizedWhenHeaderIsMissing() throws Exception {
        mockMvc.perform(patch("/api/v1/users/me/deactivate"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    private UserEntity getSeededUser(String email) {
        return userJpaRepository.findByEmail(email).orElseThrow();
    }
}
