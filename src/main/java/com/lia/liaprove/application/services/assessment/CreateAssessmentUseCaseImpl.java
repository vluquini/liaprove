package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.CreateAssessmentUseCase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementação do caso de uso para criação de avaliações personalizadas por recrutadores.
 */
public class CreateAssessmentUseCaseImpl implements CreateAssessmentUseCase {

    private final AssessmentGateway assessmentGateway;
    private final QuestionGateway questionGateway;
    private final UserGateway userGateway;

    public CreateAssessmentUseCaseImpl(AssessmentGateway assessmentGateway, QuestionGateway questionGateway,
                                       UserGateway userGateway) {
        this.assessmentGateway = assessmentGateway;
        this.questionGateway = questionGateway;
        this.userGateway = userGateway;
    }

    @Override
    public PersonalizedAssessment execute(UUID creatorId, String title, String description, List<UUID> questionIds,
            LocalDateTime expirationDate, int maxAttempts, long evaluationTimerMinutes) {

        // 1. Validar o criador (deve ser RECRUITER ou ADMIN)
        User creator = userGateway.findById(creatorId)
                .orElseThrow(() -> new UserNotFoundException("Creator user not found."));

        if (!(creator instanceof UserRecruiter) && creator.getRole() != UserRole.ADMIN) {
            throw new AuthorizationException("Only Recruiters or Administrators can create custom assessments.");
        }

        UserRecruiter recruiter;
        if (!(creator instanceof UserRecruiter)) {
            throw new AuthorizationException("The creator must have a Recruiter profile.");
        } else {
            recruiter = (UserRecruiter) creator;
        }

        // 2. Buscar as questões
        List<Question> questions = questionIds.stream()
                .map(id -> questionGateway.findById(id)
                        .orElseThrow(() -> new RuntimeException("Questão não encontrada: " + id)))
                .collect(Collectors.toList());

        // 3. Gerar o token de compartilhamento único (UUID)
        String shareableToken = UUID.randomUUID().toString();

        // 4. Criar o objeto de domínio
        PersonalizedAssessment assessment = new PersonalizedAssessment(
                UUID.randomUUID(),
                title,
                description,
                LocalDateTime.now(),
                questions,
                Duration.ofMinutes(evaluationTimerMinutes),
                recruiter,
                expirationDate,
                0, // totalAttempts inicial
                maxAttempts,
                shareableToken,
                PersonalizedAssessmentStatus.ACTIVE
        );

        // 5. Persistir
        return (PersonalizedAssessment) assessmentGateway.save(assessment);
    }
}
