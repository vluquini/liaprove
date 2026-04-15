package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.infrastructure.entities.assessment.AnswerEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AnswerMapperTest {

    private final AnswerMapper mapper = Mappers.getMapper(AnswerMapper.class);

    @Test
    void shouldMapTextResponseAndProjectUrlToEntityAndBack() {
        UUID questionId = UUID.randomUUID();

        Answer answer = new Answer(questionId);
        answer.setSelectedAlternativeId(UUID.randomUUID());
        answer.setProjectUrl("https://github.com/acme/project");
        answer.setTextResponse("open question response");

        AnswerEntity entity = mapper.toEntity(answer);

        assertThat(entity.getQuestionId()).isEqualTo(questionId);
        assertThat(entity.getSelectedAlternativeId()).isEqualTo(answer.getSelectedAlternativeId());
        assertThat(entity.getProjectUrl()).isEqualTo("https://github.com/acme/project");
        assertThat(entity.getTextResponse()).isEqualTo("open question response");

        Answer mappedBack = mapper.toDomain(entity);

        assertThat(mappedBack.getQuestionId()).isEqualTo(questionId);
        assertThat(mappedBack.getSelectedAlternativeId()).isEqualTo(answer.getSelectedAlternativeId());
        assertThat(mappedBack.getProjectUrl()).isEqualTo("https://github.com/acme/project");
        assertThat(mappedBack.getTextResponse()).isEqualTo("open question response");
    }
}
