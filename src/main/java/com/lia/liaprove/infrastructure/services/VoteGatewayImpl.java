package com.lia.liaprove.infrastructure.services;

import com.lia.liaprove.application.gateways.metrics.VoteGateway;
import com.lia.liaprove.core.domain.metrics.Vote;
import com.lia.liaprove.infrastructure.entities.metrics.VoteEntity;
import com.lia.liaprove.infrastructure.mappers.metrics.VoteMapper;
import com.lia.liaprove.infrastructure.repositories.VoteJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VoteGatewayImpl implements VoteGateway {

    private final VoteJpaRepository voteJpaRepository;
    private final VoteMapper voteMapper;

    public VoteGatewayImpl(VoteJpaRepository voteJpaRepository, VoteMapper voteMapper) {
        this.voteJpaRepository = voteJpaRepository;
        this.voteMapper = voteMapper;
    }

    @Override
    public void save(Vote vote) {
        VoteEntity entity = voteMapper.toEntity(vote);
        voteJpaRepository.save(entity);
        // Update the domain object's ID with the generated ID from the entity
        vote.setId(entity.getId());
    }

    @Override
    public List<Vote> findVotesByQuestionId(UUID questionId) {
        List<VoteEntity> voteEntities = voteJpaRepository.findByQuestionId(questionId);
        return voteEntities.stream()
                .map(voteMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }
}
