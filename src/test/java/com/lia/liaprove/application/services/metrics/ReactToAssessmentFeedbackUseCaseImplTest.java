package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.metrics.ReactionType;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.usecases.metrics.ReactToAssessmentFeedbackUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactToAssessmentFeedbackUseCaseImplTest {

    @Mock
    private FeedbackGateway feedbackGateway;

    @Mock
    private UserGateway userGateway;

    @Test
    void shouldAddReactionToAssessmentFeedback() {
        UserProfessional author = professional(UUID.randomUUID(), "Author");
        UserProfessional reactor = professional(UUID.randomUUID(), "Reactor");
        FeedbackAssessment feedback = feedback(author);
        when(userGateway.findById(reactor.getId())).thenReturn(Optional.of(reactor));
        when(feedbackGateway.findFeedbackAssessmentById(feedback.getId())).thenReturn(Optional.of(feedback));

        ReactToAssessmentFeedbackUseCase useCase = new ReactToAssessmentFeedbackUseCaseImpl(
                feedbackGateway,
                userGateway
        );

        useCase.reactToFeedback(reactor.getId(), feedback.getId(), ReactionType.LIKE);

        ArgumentCaptor<FeedbackAssessment> captor = ArgumentCaptor.forClass(FeedbackAssessment.class);
        verify(feedbackGateway).saveAssessmentFeedback(captor.capture());
        FeedbackAssessment savedFeedback = captor.getValue();
        assertThat(savedFeedback.getReactions()).hasSize(1);
        assertThat(savedFeedback.getReactions().getFirst().getUser().getId()).isEqualTo(reactor.getId());
        assertThat(savedFeedback.getReactions().getFirst().getType()).isEqualTo(ReactionType.LIKE);
    }

    @Test
    void shouldUpdateReactionWhenUserChoosesDifferentType() {
        UserProfessional author = professional(UUID.randomUUID(), "Author");
        UserProfessional reactor = professional(UUID.randomUUID(), "Reactor");
        FeedbackAssessment feedback = feedback(author);
        feedback.manageReaction(reactor, ReactionType.LIKE);
        when(userGateway.findById(reactor.getId())).thenReturn(Optional.of(reactor));
        when(feedbackGateway.findFeedbackAssessmentById(feedback.getId())).thenReturn(Optional.of(feedback));

        ReactToAssessmentFeedbackUseCase useCase = new ReactToAssessmentFeedbackUseCaseImpl(
                feedbackGateway,
                userGateway
        );

        useCase.reactToFeedback(reactor.getId(), feedback.getId(), ReactionType.DISLIKE);

        ArgumentCaptor<FeedbackAssessment> captor = ArgumentCaptor.forClass(FeedbackAssessment.class);
        verify(feedbackGateway).saveAssessmentFeedback(captor.capture());
        assertThat(captor.getValue().getReactions()).hasSize(1);
        assertThat(captor.getValue().getReactions().getFirst().getType()).isEqualTo(ReactionType.DISLIKE);
    }

    @Test
    void shouldRemoveReactionWhenUserRepeatsSameType() {
        UserProfessional author = professional(UUID.randomUUID(), "Author");
        UserProfessional reactor = professional(UUID.randomUUID(), "Reactor");
        FeedbackAssessment feedback = feedback(author);
        feedback.manageReaction(reactor, ReactionType.LIKE);
        when(userGateway.findById(reactor.getId())).thenReturn(Optional.of(reactor));
        when(feedbackGateway.findFeedbackAssessmentById(feedback.getId())).thenReturn(Optional.of(feedback));

        ReactToAssessmentFeedbackUseCase useCase = new ReactToAssessmentFeedbackUseCaseImpl(
                feedbackGateway,
                userGateway
        );

        useCase.reactToFeedback(reactor.getId(), feedback.getId(), ReactionType.LIKE);

        ArgumentCaptor<FeedbackAssessment> captor = ArgumentCaptor.forClass(FeedbackAssessment.class);
        verify(feedbackGateway).saveAssessmentFeedback(captor.capture());
        assertThat(captor.getValue().getReactions()).isEmpty();
    }

    @Test
    void shouldRejectReactionFromFeedbackAuthor() {
        UserProfessional author = professional(UUID.randomUUID(), "Author");
        FeedbackAssessment feedback = feedback(author);
        when(userGateway.findById(author.getId())).thenReturn(Optional.of(author));
        when(feedbackGateway.findFeedbackAssessmentById(feedback.getId())).thenReturn(Optional.of(feedback));

        ReactToAssessmentFeedbackUseCase useCase = new ReactToAssessmentFeedbackUseCaseImpl(
                feedbackGateway,
                userGateway
        );

        assertThatThrownBy(() -> useCase.reactToFeedback(author.getId(), feedback.getId(), ReactionType.LIKE))
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("Users cannot react to their own feedback");

        verify(feedbackGateway, never()).saveAssessmentFeedback(feedback);
    }

    private FeedbackAssessment feedback(UserProfessional author) {
        FeedbackAssessment feedback = new FeedbackAssessment(
                author,
                new AssessmentAttempt(
                        UUID.randomUUID(),
                        null,
                        author,
                        List.of(),
                        List.of(),
                        List.of(),
                        LocalDateTime.now().minusHours(2),
                        LocalDateTime.now().minusHours(1),
                        0,
                        null,
                        AssessmentAttemptStatus.COMPLETED
                ),
                "Assessment feedback",
                LocalDateTime.now().minusHours(1),
                true
        );
        feedback.setId(UUID.randomUUID());
        return feedback;
    }

    private UserProfessional professional(UUID userId, String name) {
        return new UserProfessional(
                userId,
                name,
                "%s@example.com".formatted(name.toLowerCase()),
                "hashed-password",
                "Developer",
                "Bio",
                ExperienceLevel.JUNIOR,
                UserRole.PROFESSIONAL,
                1,
                0,
                List.of(),
                0.0f,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now()
        );
    }
}
