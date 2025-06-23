package com.lia.liaprove.core.domain.question;
/*
Voting: a questão está sendo votada pelos usuários.
Approved: questão aprovada pela comnidade, mas irá para o banco de dados 'privado'.
Finished: questão aprovada pela comunidade e já poderá ser utilizada nas avaliações.
Rejected: questão rejeitada na votação pela comunidade.
 */
public enum QuestionStatus {
    VOTING, APPROVED, FINISHED, REJECTED
}
