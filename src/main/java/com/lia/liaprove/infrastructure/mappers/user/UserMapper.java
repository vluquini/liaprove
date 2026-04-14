package com.lia.liaprove.infrastructure.mappers.user;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserProfessionalEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
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

    UserRecruiter toDomain(UserRecruiterEntity entity);

    UserProfessional toDomain(UserProfessionalEntity entity);
}
