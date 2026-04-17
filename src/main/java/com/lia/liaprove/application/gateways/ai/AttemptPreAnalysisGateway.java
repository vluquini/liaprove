package com.lia.liaprove.application.gateways.ai;

import com.lia.liaprove.core.domain.assessment.AttemptPreAnalysis;
import com.lia.liaprove.core.domain.question.Question;

/**
 * Gateway pragmático para gerar a análise prévia por IA.
 */
public interface AttemptPreAnalysisGateway {

    AttemptPreAnalysis.Analysis generate(AttemptPreAnalysisContext context);
}
