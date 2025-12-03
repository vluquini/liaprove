package com.lia.liaprove.infrastructure.mappers;

import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.infrastructure.entities.question.AlternativeEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AlternativeMapper {
    Alternative toDomain(AlternativeEmbeddable embeddable);
    AlternativeEmbeddable toEntity(Alternative domain);
}
