package com.lia.liaprove.infrastructure.mappers.user;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.infrastructure.dtos.user.PublicUserResponseDto;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserProfessionalEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import org.mapstruct.AfterMapping;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    default UserResponseDto toResponseDto(UserRecruiter user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getOccupation(),
                user.getBio(),
                user.getExperienceLevel(),
                null,
                null,
                user.getRole(),
                user.getCompanyName(),
                user.getCompanyEmail()
        );
    }

    default UserResponseDto toResponseDto(UserProfessional user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getOccupation(),
                user.getBio(),
                user.getExperienceLevel(),
                user.getHardSkills(),
                user.getSoftSkills(),
                user.getRole(),
                null,
                null
        );
    }

    default UserResponseDto toResponseDto(User user) {
        return switch (user) {
            case null -> null;
            case UserRecruiter ur -> toResponseDto(ur);
            case UserProfessional up -> toResponseDto(up);
            default -> throw new IllegalArgumentException("Unknown User subtype: " + user.getClass());
        };
    }

    /**
     * Mapeia um {@link UserRecruiter} para um {@link PublicUserResponseDto},
     * omitindo campos sensíveis (e-mail e e-mail corporativo) para prevenir
     * vazamento de PII ao expor perfis de outros usuários.
     */
    default PublicUserResponseDto toPublicResponseDto(UserRecruiter user) {
        if (user == null) {
            return null;
        }

        return new PublicUserResponseDto(
                user.getId(),
                user.getName(),
                user.getOccupation(),
                user.getBio(),
                user.getExperienceLevel(),
                null,
                null,
                user.getRole(),
                user.getCompanyName()
        );
    }

    /**
     * Mapeia um {@link UserProfessional} para um {@link PublicUserResponseDto},
     * omitindo campos sensíveis (e-mail) para prevenir vazamento de PII.
     */
    default PublicUserResponseDto toPublicResponseDto(UserProfessional user) {
        if (user == null) {
            return null;
        }

        return new PublicUserResponseDto(
                user.getId(),
                user.getName(),
                user.getOccupation(),
                user.getBio(),
                user.getExperienceLevel(),
                user.getHardSkills(),
                user.getSoftSkills(),
                user.getRole(),
                null
        );
    }

    /**
     * Mapeia qualquer {@link User} para um {@link PublicUserResponseDto} via dispatch de subtipo.
     */
    default PublicUserResponseDto toPublicResponseDto(User user) {
        return switch (user) {
            case null -> null;
            case UserRecruiter ur -> toPublicResponseDto(ur);
            case UserProfessional up -> toPublicResponseDto(up);
            default -> throw new IllegalArgumentException("Unknown User subtype: " + user.getClass());
        };
    }

    default UserEntity toEntity(User domain) {
        return switch (domain) {
            case null -> null;
            case UserRecruiter ur -> toEntity(ur);
            case UserProfessional up -> toEntity(up);
            default -> throw new IllegalArgumentException("Unknown User subtype: " + domain.getClass());
        };
    }

    UserRecruiterEntity toEntity(UserRecruiter domain);

    UserProfessionalEntity toEntity(UserProfessional domain);

    default User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        // Unproxy the entity to get the real underlying object
        Object unproxiedEntity = Hibernate.unproxy(entity);

        // Now perform the check on the real object
        if (unproxiedEntity instanceof UserRecruiterEntity ure) {
            return toDomain(ure);
        } else if (unproxiedEntity instanceof UserProfessionalEntity upe) {
            return toDomain(upe);
        } else {
            throw new IllegalArgumentException("Unknown UserEntity subtype: " + entity.getClass());
        }
    }

    @Mapping(target = "experienceLevel", ignore = true)
    UserRecruiter toDomain(UserRecruiterEntity entity);

    @Mapping(target = "experienceLevel", ignore = true)
    UserProfessional toDomain(UserProfessionalEntity entity);

    @AfterMapping
    default void setExperienceLevelIfPresent(UserRecruiterEntity entity, @MappingTarget UserRecruiter domain) {
        if (entity.getExperienceLevel() != null) {
            domain.setExperienceLevel(entity.getExperienceLevel());
        }
    }

    @AfterMapping
    default void setExperienceLevelIfPresent(UserProfessionalEntity entity, @MappingTarget UserProfessional domain) {
        if (entity.getExperienceLevel() != null) {
            domain.setExperienceLevel(entity.getExperienceLevel());
        }
    }
}
