package com.lia.liaprove.core.domain.relationship;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.question.Question;
/*
Esta classe representa o relacionamento entre uma questão e uma avaliação.
 */
public class AssessmentQuestion {
    private Assessment assessment;
    private Question question;
    // Posição da questão na avaliação
    private int order;
}
