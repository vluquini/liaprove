package com.lia.liaprove.infrastructure.services.algorithms.bayesian;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BayesianGatewayImplTest {

    @Mock
    private QuestionGateway questionGateway;

    @InjectMocks
    private BayesianGatewayImpl gateway;

    @Test
    void shouldLoadFinishedQuestionsAsSuggestionCandidates() {
        gateway.getAllQuestions();

        verify(questionGateway).findAll(null, null, QuestionStatus.FINISHED, null, null, 0, 200);
    }
}
