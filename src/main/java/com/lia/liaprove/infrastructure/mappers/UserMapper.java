package com.lia.liaprove.infrastructure.mappers;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.infrastructure.dtos.UserResponseDto;
import com.lia.liaprove.infrastructure.entities.UserEntity;
import com.lia.liaprove.infrastructure.entities.UserProfessionalEntity;
import com.lia.liaprove.infrastructure.entities.UserRecruiterEntity;

public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
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
                user.getRole()
        );
    }
    
    public UserEntity toEntity(User user) {
        if (user == null) return null;

        UserEntity entity;
        if (user instanceof UserRecruiter recruiter) {
            UserRecruiterEntity recruiterEntity = new UserRecruiterEntity();
            recruiterEntity.setCompanyName(recruiter.getCompanyName());
            recruiterEntity.setCompanyEmail(recruiter.getCompanyEmail());
            recruiterEntity.setTotalAssessmentsCreated(recruiter.getTotalAssessmentsCreated());
            recruiterEntity.setRecruiterRating(recruiter.getRecruiterRating());
            recruiterEntity.setRecruiterRatingCount(recruiter.getRecruiterRatingCount());
            entity = recruiterEntity;
        } else {
            entity = new UserProfessionalEntity();
        }

        mapCommonFieldsToEntity(entity, user);
        return entity;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        User user;
        if (entity instanceof UserRecruiterEntity recruiterEntity) {
            UserRecruiter recruiter = new UserRecruiter();
            recruiter.setCompanyName(recruiterEntity.getCompanyName());
            recruiter.setCompanyEmail(recruiterEntity.getCompanyEmail());
            recruiter.setTotalAssessmentsCreated(recruiterEntity.getTotalAssessmentsCreated());
            recruiter.setRecruiterRating(recruiterEntity.getRecruiterRating());
            recruiter.setRecruiterRatingCount(recruiterEntity.getRecruiterRatingCount());
            user = recruiter;
        } else {
            user = new UserProfessional();
        }

        mapCommonFieldsToDomain(user, entity);
        return user;
    }

    private void mapCommonFieldsToEntity(UserEntity entity, User user) {
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setOccupation(user.getOccupation());
        entity.setBio(user.getBio());
        entity.setExperienceLevel(user.getExperienceLevel());
        entity.setRole(user.getRole());
        entity.setVoteWeight(user.getVoteWeight());
        entity.setTotalAssessmentsTaken(user.getTotalAssessmentsTaken());
        entity.setAverageScore(user.getAverageScore());
        entity.setRegistrationDate(user.getRegistrationDate());
        entity.setLastLogin(user.getLastLogin());
        entity.setStatus(user.getStatus());
    }

    private void mapCommonFieldsToDomain(User user, UserEntity entity) {
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setEmail(entity.getEmail());
        user.setPasswordHash(entity.getPasswordHash());
        user.setOccupation(entity.getOccupation());
        user.setBio(entity.getBio());
        user.setExperienceLevel(entity.getExperienceLevel());
        user.setRole(entity.getRole());
        user.setVoteWeight(entity.getVoteWeight());
        user.setTotalAssessmentsTaken(entity.getTotalAssessmentsTaken());
        user.setAverageScore(entity.getAverageScore());
        user.setRegistrationDate(entity.getRegistrationDate());
        user.setLastLogin(entity.getLastLogin());
        user.setStatus(entity.getStatus());
    }
}