package com.lia.liaprove.infrastructure.mappers.users;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
import com.lia.liaprove.infrastructure.entities.users.UserProfessionalEntity;
import com.lia.liaprove.infrastructure.entities.users.UserRecruiterEntity;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponseDto toResponseDto(UserRecruiter user);

    UserResponseDto toResponseDto(UserProfessional user);

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
