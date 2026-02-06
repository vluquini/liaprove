-- =================================================================
--  QUESTIONS SCRIPT
-- =================================================================
--  Este script popula as tabelas 'questions', 'question_knowledge_areas',
--  e 'question_alternatives' com dados de exemplo.
-- =================================================================


-- =================================================================
--  QUESTIONS SCRIPT (H2 compatible)
-- =================================================================

-- =================================================================
--  Question 1: Multiple Choice (Java) by Carlos Silva
-- =================================================================
MERGE INTO questions (
    id,
    question_type,
    author_id,
    title,
    description,
    difficulty_by_community,
    relevance_by_community,
    submission_date,
    voting_end_date,
    status,
    relevance_byllm,
    recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000001-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Java Garbage Collection',
    'Qual das seguintes afirmações sobre o Garbage Collection (GC) em Java é a mais precisa?',
    'MEDIUM',
    'FIVE',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    'FOUR',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000001-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT'),
    ('00000001-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (
    id,
    question_id,
    text,
    correct,
    ord_index
    )
    KEY (id)
    VALUES
    ('00000001-a001-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001',
    'O GC garante que a memória nunca se esgotará.', FALSE, 0),
    ('00000001-a002-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001',
    'Chamar System.gc() força a execução imediata e síncrona do GC.', FALSE, 1),
    ('00000001-a003-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001',
    'O GC libera a memória de objetos que não são mais referenciados no programa.', TRUE, 2),
    ('00000001-a004-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001',
    'O GC só é executado quando o programa é finalizado.', FALSE, 3);

-- =================================================================
--  Question 2: Multiple Choice (Python/ML) by Mariana Costa
-- =================================================================
MERGE INTO questions (
    id,
    question_type,
    author_id,
    title,
    description,
    difficulty_by_community,
    relevance_by_community,
    submission_date,
    voting_end_date,
    status,
    relevance_byllm,
    recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000002-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Overfitting in Machine Learning',
    'O que é "overfitting" em um modelo de Machine Learning?',
    'EASY',
    'FIVE',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    'FIVE',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000002-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (
    id,
    question_id,
    text,
    correct,
    ord_index
    )
    KEY (id)
    VALUES
    ('00000002-a001-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001',
    'O modelo tem um desempenho ruim tanto nos dados de treino quanto nos de teste.', FALSE, 0),
    ('00000002-a002-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001',
    'O modelo tem um bom desempenho nos dados de teste, mas ruim nos dados de treino.', FALSE, 1),
    ('00000002-a003-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001',
    'O modelo aprende os dados de treino tão bem que captura ruído e não generaliza para novos dados.', TRUE, 2),
    ('00000002-a004-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001',
    'O modelo não consegue aprender os padrões nos dados de treino.', FALSE, 3);

-- =================================================================
--  Question 3: Project Question by Carlos Silva
-- =================================================================
MERGE INTO questions (
    id,
    question_type,
    author_id,
    title,
    description,
    difficulty_by_community,
    relevance_by_community,
    submission_date,
    voting_end_date,
    status,
    relevance_byllm,
    recruiter_usage_count,
    project_url
    )
    KEY (id)
    VALUES (
    '00000003-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'API REST com Spring Boot',
    'Crie uma API REST simples com Spring Boot para um CRUD de produtos. O projeto deve ser compartilhado via um repositório Git.',
    'MEDIUM',
    'FOUR',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'APPROVED',
    'FOUR',
    1,
    'http://github.com/example/project-details'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000003-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT'),
    ('00000003-0000-0000-0000-000000000001', 'DATABASE');


