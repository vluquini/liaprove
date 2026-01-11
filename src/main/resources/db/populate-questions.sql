-- =================================================================
--  QUESTIONS SCRIPT
-- =================================================================
--  Este script popula as tabelas 'questions', 'question_knowledge_areas',
--  e 'question_alternatives' com dados de exemplo.
-- =================================================================


-- =================================================================
--  Question 1: Multiple Choice (Java) by Carlos Silva
-- =================================================================
INSERT INTO questions (id, question_type, author_id, title, description, difficulty_by_community, relevance_by_community, submission_date, voting_end_date, status, relevance_byllm, recruiter_usage_count)
VALUES
('00000001-0000-0000-0000-000000000001', 'MULTIPLE_CHOICE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Java Garbage Collection', 'Qual das seguintes afirmações sobre o Garbage Collection (GC) em Java é a mais precisa?', 'MEDIUM', 'FIVE', NOW(), NOW() + INTERVAL '7 DAY', 'VOTING', 'FOUR', 0)
    ON CONFLICT (id) DO NOTHING;

-- Knowledge Areas for Question 1
INSERT INTO question_knowledge_areas (question_id, knowledge_area)
VALUES
('00000001-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT'),
('00000001-0000-0000-0000-000000000001', 'DATABASE')
    ON CONFLICT DO NOTHING;

-- Alternatives for Question 1
INSERT INTO question_alternatives (id, question_id, text, correct, ord_index)
VALUES
('00000001-a001-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001', 'O GC garante que a memória nunca se esgotará.', false, 0),
('00000001-a002-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001', 'Chamar System.gc() força a execução imediata e síncrona do GC.', false, 1),
('00000001-a003-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001', 'O GC libera a memória de objetos que não são mais referenciados no programa.', true, 2),
('00000001-a004-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001', 'O GC só é executado quando o programa é finalizado.', false, 3)
    ON CONFLICT (id) DO NOTHING;

-- =================================================================
--  Question 2: Multiple Choice (Python/ML) by Mariana Costa
-- =================================================================
INSERT INTO questions (id, question_type, author_id, title, description, difficulty_by_community, relevance_by_community, submission_date, voting_end_date, status, relevance_byllm, recruiter_usage_count)
VALUES
('00000002-0000-0000-0000-000000000001', 'MULTIPLE_CHOICE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Overfitting in Machine Learning', 'O que é "overfitting" em um modelo de Machine Learning?', 'EASY', 'FIVE', NOW(), NOW() + INTERVAL '7 DAY', 'VOTING', 'FIVE', 0)
    ON CONFLICT (id) DO NOTHING;

-- Knowledge Areas for Question 2
INSERT INTO question_knowledge_areas (question_id, knowledge_area)
VALUES
('00000002-0000-0000-0000-000000000001', 'AI')
    ON CONFLICT DO NOTHING;

-- Alternatives for Question 2
INSERT INTO question_alternatives (id, question_id, text, correct, ord_index)
VALUES
('00000002-a001-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001', 'O modelo tem um desempenho ruim tanto nos dados de treino quanto nos de teste.', false, 0),
('00000002-a002-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001', 'O modelo tem um bom desempenho nos dados de teste, mas ruim nos dados de treino.', false, 1),
('00000002-a003-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001', 'O modelo aprende os dados de treino tão bem que captura ruído e não generaliza para novos dados.', true, 2),
('00000002-a004-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001', 'O modelo não consegue aprender os padrões nos dados de treino.', false, 3)
    ON CONFLICT (id) DO NOTHING;

-- =================================================================
--  Question 3: Project Question by Carlos Silva
-- =================================================================
INSERT INTO questions (id, question_type, author_id, title, description, difficulty_by_community, relevance_by_community, submission_date, voting_end_date, status, relevance_byllm, recruiter_usage_count, project_url)
VALUES
('00000003-0000-0000-0000-000000000001', 'PROJECT', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'API REST com Spring Boot', 'Crie uma API REST simples com Spring Boot para um CRUD de produtos. O projeto deve ser compartilhado via um repositório Git. Detalhes no link.', 'MEDIUM', 'FOUR', NOW(), NOW() + INTERVAL '7 DAY', 'APPROVED', 'FOUR', 1, 'http://github.com/example/project-details')
    ON CONFLICT (id) DO NOTHING;

-- Knowledge Areas for Question 3
INSERT INTO question_knowledge_areas (question_id, knowledge_area)
VALUES
('00000003-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT'),
('00000003-0000-0000-0000-000000000001', 'DATABASE')
    ON CONFLICT DO NOTHING;