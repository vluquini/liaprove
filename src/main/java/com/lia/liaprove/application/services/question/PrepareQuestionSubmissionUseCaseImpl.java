package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;

public class PrepareQuestionSubmissionUseCaseImpl implements PrepareQuestionSubmissionUseCase {

    private final QuestionPreAnalysisGateway questionPreAnalysisGateway;

    public PrepareQuestionSubmissionUseCaseImpl(QuestionPreAnalysisGateway questionPreAnalysisGateway) {
        this.questionPreAnalysisGateway = questionPreAnalysisGateway;
    }

    @Override
    public PreparedQuestion execute(PreparationCommand command) {
        QuestionValidator.validate(command);
        return questionPreAnalysisGateway.prepareForSubmission(command);
    }
}
