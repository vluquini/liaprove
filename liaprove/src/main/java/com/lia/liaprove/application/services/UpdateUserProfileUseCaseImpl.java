package com.lia.liaprove.application.services;

import com.lia.liaprove.application.gateways.UserGateway;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.user.UpdateUserProfileUseCase;

import java.util.Objects;
import java.util.UUID;

public class UpdateUserProfileUseCaseImpl implements UpdateUserProfileUseCase {

    private final UserGateway userGateway;

    public UpdateUserProfileUseCaseImpl(UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway, "userGateway must not be null");
    }

    @Override
    public void updateProfile(UUID userId,
                              String occupation,
                              String bio,
                              ExperienceLevel experienceLevel)
            throws UserNotFoundException, InvalidUserDataException {

        Objects.requireNonNull(userId, "userId must not be null");

        // Pelo menos um campo deve ser informado para atualizar
        boolean hasOccupation = occupation != null && !occupation.isBlank();
        boolean hasBio = bio != null; // Bio pode ser uma string vazia para limpar
        boolean hasExperience = experienceLevel != null;

        if (!hasOccupation && !hasBio && !hasExperience) {
            throw new InvalidUserDataException("At least one field must be provided to update the profile");
        }

        User user = userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Atualiza occupation se fornecido
        if (hasOccupation) {
            user.setOccupation(occupation.trim());
        }

        // Atualiza bio (aceita vazio para limpar)
        if (hasBio) {
            user.setBio(bio == null ? null : bio.trim());
        }

        // Se experienceLevel foi informado, só faz sentido para profissionais
        if (hasExperience) {
            if (user instanceof UserProfessional) {
                ((UserProfessional) user).updateExperienceLevel(experienceLevel);
            } else {
                throw new InvalidUserDataException("ExperienceLevel can only be set for professional users");
            }
        }

        // Persistir alterações
        userGateway.save(user);
    }
}
