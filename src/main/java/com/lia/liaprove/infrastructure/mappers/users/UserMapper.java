package com.lia.liaprove.infrastructure.mappers.users;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.infrastructure.dtos.UserResponseDto;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
import com.lia.liaprove.infrastructure.entities.users.UserProfessionalEntity;
import com.lia.liaprove.infrastructure.entities.users.UserRecruiterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponseDto toResponseDto(User user);

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
        return switch (entity) {
            case null -> null;
            case UserRecruiterEntity ure -> toDomain(ure);
            case UserProfessionalEntity upe -> toDomain(upe);
            default -> throw new IllegalArgumentException("Unknown UserEntity subtype: " + entity.getClass());
        };
    }

    UserRecruiter toDomain(UserRecruiterEntity entity);

    UserProfessional toDomain(UserProfessionalEntity entity);
}
