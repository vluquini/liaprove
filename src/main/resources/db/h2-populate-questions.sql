-- =================================================================
--  QUESTIONS SCRIPT (H2 Compatible)
-- =================================================================
--  Este script popula as tabelas 'questions', 'question_knowledge_areas',
--  e 'question_alternatives' com dados de exemplo.
-- =================================================================

-- =================================================================
--  MULTIPLE CHOICE QUESTIONS
-- =================================================================

-- Question 1: Software Development and Database by Carlos Silva
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
    'FINISHED',
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
    ('00000001-a001-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001', 'O GC garante que a memória nunca se esgotará.', FALSE, 0),
    ('00000001-a002-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001', 'Chamar System.gc() força a execução imediata e síncrona do GC.', FALSE, 1),
    ('00000001-a003-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001', 'O GC libera a memória de objetos que não são mais referenciados no programa.', TRUE, 2),
    ('00000001-a004-0000-0000-000000000001', '00000001-0000-0000-0000-000000000001', 'O GC só é executado quando o programa é finalizado.', FALSE, 3);

-- Question 2: Artificial Intelligence by Mariana Costa
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
    'FINISHED',
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
    ('00000002-a001-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001', 'O modelo tem um desempenho ruim tanto nos dados de treino quanto nos de teste.', FALSE, 0),
    ('00000002-a002-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001', 'O modelo tem um bom desempenho nos dados de teste, mas ruim nos dados de treino.', FALSE, 1),
    ('00000002-a003-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001', 'O modelo aprende os dados de treino tão bem que captura ruído e não generaliza para novos dados.', TRUE, 2),
    ('00000002-a004-0000-0000-000000000001', '00000002-0000-0000-0000-000000000001', 'O modelo não consegue aprender os padrões nos dados de treino.', FALSE, 3);

-- Question 3: Software Development by Carlos Silva
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
    '00000003-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Idempotência em APIs REST',
    'Qual das alternativas descreve corretamente idempotência em uma API REST?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000003-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (
    id,
    question_id,
    text,
    correct,
    ord_index
    )
    KEY (id)
    VALUES
    ('00000003-a001-0000-0000-000000000001', '00000003-0000-0000-0000-000000000001', 'Significa que requisições repetidas causam efeitos idênticos no servidor.', TRUE, 0),
    ('00000003-a002-0000-0000-000000000001', '00000003-0000-0000-0000-000000000001', 'Significa que a resposta sempre será a mesma para qualquer chamada.', FALSE, 1),
    ('00000003-a003-0000-0000-000000000001', '00000003-0000-0000-0000-000000000001', 'É o mesmo que autenticação via token.', FALSE, 2),
    ('00000003-a004-0000-0000-000000000001', '00000003-0000-0000-0000-000000000001', 'Refere-se apenas a operações de leitura (GET).', FALSE, 3);

-- Question 4: Artificial Intelligence by Mariana Costa
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
    '00000004-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Divisão de dados: treino e teste',
    'Qual a finalidade de separar dados em conjuntos de treino e teste em ML?',
    'EASY',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000004-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000004-a001-0000-0000-000000000001', '00000004-0000-0000-0000-000000000001', 'Avaliar o desempenho do modelo em dados não vistos durante o treino.', TRUE, 0),
    ('00000004-a002-0000-0000-000000000001', '00000004-0000-0000-0000-000000000001', 'Aumentar a quantidade de features do modelo.', FALSE, 1),
    ('00000004-a003-0000-0000-000000000001', '00000004-0000-0000-0000-000000000001', 'Reduzir o tempo de treinamento pela metade.', FALSE, 2),
    ('00000004-a004-0000-0000-000000000001', '00000004-0000-0000-0000-000000000001', 'Garantir que o modelo memorize o dataset.', FALSE, 3);

-- Question 5: Software Development by Junior Dev
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
    '00000005-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Especificidade em CSS',
    'Qual seletor tem maior especificidade e, portanto, prioridade sobre os demais?',
    'MEDIUM',
    3,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    3,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000005-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000005-a001-0000-0000-000000000001', '00000005-0000-0000-0000-000000000001', 'Um seletor de ID (#meuId).', TRUE, 0),
    ('00000005-a002-0000-0000-000000000001', '00000005-0000-0000-0000-000000000001', 'Um seletor de classe (.minhaClasse).', FALSE, 1),
    ('00000005-a003-0000-0000-000000000001', '00000005-0000-0000-0000-000000000001', 'Um seletor de elemento (div).', FALSE, 2),
    ('00000005-a004-0000-0000-000000000001', '00000005-0000-0000-0000-000000000001', 'Um seletor universal (*).', FALSE, 3);

-- Question 6: Database by Carlos Silva
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
    '00000006-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Tipos de JOIN em SQL',
    'Qual JOIN retorna apenas as linhas que têm correspondência em ambas as tabelas?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000006-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000006-a001-0000-0000-000000000001', '00000006-0000-0000-0000-000000000001', 'INNER JOIN', TRUE, 0),
    ('00000006-a002-0000-0000-000000000001', '00000006-0000-0000-0000-000000000001', 'LEFT JOIN', FALSE, 1),
    ('00000006-a003-0000-0000-000000000001', '00000006-0000-0000-0000-000000000001', 'RIGHT JOIN', FALSE, 2),
    ('00000006-a004-0000-0000-000000000001', '00000006-0000-0000-0000-000000000001', 'FULL OUTER JOIN', FALSE, 3);

-- Question 7: Artificial Intelligence by Mariana Costa
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
    '00000007-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Métricas: Matriz de Confusão',
    'Na matriz de confusão, o que representa "precision"?',
    'MEDIUM',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000007-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000007-a001-0000-0000-000000000001', '00000007-0000-0000-0000-000000000001', 'A proporção de verdadeiros positivos entre todos os previstos como positivos.', TRUE, 0),
    ('00000007-a002-0000-0000-000000000001', '00000007-0000-0000-0000-000000000001', 'A proporção de verdadeiros positivos entre todos os casos reais positivos.', FALSE, 1),
    ('00000007-a003-0000-0000-000000000001', '00000007-0000-0000-0000-000000000001', 'A taxa de falsos negativos.', FALSE, 2),
    ('00000007-a004-0000-0000-000000000001', '00000007-0000-0000-0000-000000000001', 'A acurácia geral do modelo.', FALSE, 3);

-- Question 8: Cybersecurity and Database by Carlos Silva
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
    '00000008-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Mitigação de SQL Injection',
    'Qual é a prática mais eficaz para prevenir SQL Injection em aplicações?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000008-0000-0000-0000-000000000001', 'CYBERSECURITY'),
    ('00000008-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000008-a001-0000-0000-000000000001', '00000008-0000-0000-0000-000000000001', 'Usar queries parametrizadas/prepared statements.', TRUE, 0),
    ('00000008-a002-0000-0000-000000000001', '00000008-0000-0000-0000-000000000001', 'Escapar manualmente todas as strings no cliente.', FALSE, 1),
    ('00000008-a003-0000-0000-000000000001', '00000008-0000-0000-0000-000000000001', 'Conceder ao banco permissões de superusuário.', FALSE, 2),
    ('00000008-a004-0000-0000-000000000001', '00000008-0000-0000-0000-000000000001', 'Remover índices das tabelas sensíveis.', FALSE, 3);

-- Question 9: Networks by Junior Dev
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
    '00000009-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Modelo OSI: Camadas básicas',
    'Qual camada do modelo OSI é responsável pelo roteamento entre redes diferentes?',
    'EASY',
    3,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    3,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000009-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000009-a001-0000-0000-000000000001', '00000009-0000-0000-0000-000000000001', 'Camada de Rede (Network).', TRUE, 0),
    ('00000009-a002-0000-0000-000000000001', '00000009-0000-0000-0000-000000000001', 'Camada de Enlace (Data Link).', FALSE, 1),
    ('00000009-a003-0000-0000-000000000001', '00000009-0000-0000-0000-000000000001', 'Camada de Aplicação.', FALSE, 2),
    ('00000009-a004-0000-0000-000000000001', '00000009-0000-0000-0000-000000000001', 'Camada Física.', FALSE, 3);

-- Question 10: Database by Ana Pereira
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
    '00000010-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Formas Normais em Banco de Dados',
    'Qual condição caracteriza a Segunda Forma Normal (2NF)?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000010-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000010-a001-0000-0000-000000000001', '00000010-0000-0000-0000-000000000001', 'Todos os atributos não-chave dependem totalmente da chave primária.', TRUE, 0),
    ('00000010-a002-0000-0000-000000000001', '00000010-0000-0000-0000-000000000001', 'Não há dependências funcionais.', FALSE, 1),
    ('00000010-a003-0000-0000-000000000001', '00000010-0000-0000-0000-000000000001', 'A tabela contém apenas uma coluna.', FALSE, 2),
    ('00000010-a004-0000-0000-000000000001', '00000010-0000-0000-0000-000000000001', 'Todas as colunas são chaves primárias.', FALSE, 3);

-- Question 11: Software Development by Roberto Lima
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
    '00000011-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'Concorrência em Java: sincronização',
    'Qual mecanismo em Java evita condições de corrida ao acessar recursos compartilhados?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000011-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000011-a001-0000-0000-000000000001', '00000011-0000-0000-0000-000000000001', 'Uso de blocos synchronized ou classes do pacote java.util.concurrent.', TRUE, 0),
    ('00000011-a002-0000-0000-000000000001', '00000011-0000-0000-0000-000000000001', 'Executar tudo em single-thread para performance máxima.', FALSE, 1),
    ('00000011-a003-0000-0000-000000000001', '00000011-0000-0000-0000-000000000001', 'Usar System.gc() antes de acessar a variável compartilhada.', FALSE, 2),
    ('00000011-a004-0000-0000-000000000001', '00000011-0000-0000-0000-000000000001', 'Remover todos os locks do sistema.', FALSE, 3);

-- Question 12: Database by Mariana Costa
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
    '00000012-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Índices em banco de dados',
    'Qual é o principal benefício de criar um índice em uma coluna frequentemente consultada?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000012-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000012-a001-0000-0000-000000000001', '00000012-0000-0000-0000-000000000001', 'Melhorar a velocidade de consultas que filtram por essa coluna.', TRUE, 0),
    ('00000012-a002-0000-0000-000000000001', '00000012-0000-0000-0000-000000000001', 'Reduzir o espaço em disco usado pelo banco.', FALSE, 1),
    ('00000012-a003-0000-0000-000000000001', '00000012-0000-0000-0000-000000000001', 'Impedir que a coluna aceite valores nulos.', FALSE, 2),
    ('00000012-a004-0000-0000-000000000001', '00000012-0000-0000-0000-000000000001', 'Aumentar a latência de escrita sem benefícios de leitura.', FALSE, 3);

-- Question 13: Networks by Junior Dev
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
    '00000013-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Tipos de registro DNS',
    'Qual tipo de registro DNS é usado para mapear um nome de domínio para um endereço IPv4?',
    'EASY',
    3,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    3,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000013-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000013-a001-0000-0000-000000000001', '00000013-0000-0000-0000-000000000001', 'Registro A', TRUE, 0),
    ('00000013-a002-0000-0000-000000000001', '00000013-0000-0000-0000-000000000001', 'Registro MX', FALSE, 1),
    ('00000013-a003-0000-0000-000000000001', '00000013-0000-0000-0000-000000000001', 'Registro TXT', FALSE, 2),
    ('00000013-a004-0000-0000-000000000001', '00000013-0000-0000-0000-000000000001', 'Registro CNAME', FALSE, 3);

-- Question 14: Cybersecurity by Carlos Silva
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
    '00000014-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Cross-Site Scripting (XSS)',
    'Qual técnica é a mais efetiva para prevenir XSS baseado em contexto ao renderizar conteúdo enviado por usuários?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000014-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000014-a001-0000-0000-000000000001', '00000014-0000-0000-0000-000000000001', 'Contextual escaping/encoding no momento da saída (output encoding).', TRUE, 0),
    ('00000014-a002-0000-0000-000000000001', '00000014-0000-0000-0000-000000000001', 'Confiar em validação apenas no cliente (JavaScript).', FALSE, 1),
    ('00000014-a003-0000-0000-000000000001', '00000014-0000-0000-0000-000000000001', 'Remover cabeçalhos HTTP de segurança.', FALSE, 2),
    ('00000014-a004-0000-0000-000000000001', '00000014-0000-0000-0000-000000000001', 'Armazenar scripts inline no banco de dados sem tratamento.', FALSE, 3);

-- Question 15: Software Development by Carlos Silva
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
    '00000015-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Padrão Singleton: armadilhas',
    'Qual é um problema comum ao implementar Singleton sem cuidado em aplicações multithreaded em Java?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000015-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000015-a001-0000-0000-000000000001', '00000015-0000-0000-0000-000000000001', 'Risco de criar múltiplas instâncias se a inicialização não for thread-safe.', TRUE, 0),
    ('00000015-a002-0000-0000-000000000001', '00000015-0000-0000-0000-000000000001', 'Singleton sempre melhora testabilidade.', FALSE, 1),
    ('00000015-a003-0000-0000-000000000001', '00000015-0000-0000-0000-000000000001', 'Singleton elimina necessidade de sincronização em todos os casos.', FALSE, 2),
    ('00000015-a004-0000-0000-000000000001', '00000015-0000-0000-0000-000000000001', 'Singleton é recomendado apenas para classes de utilitários sem estado.', FALSE, 3);

-- Question 16: Database by Ana Pereira
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
    '00000016-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Propriedade Isolation em transações',
    'O que a propriedade "Isolation" garante em um sistema de transações ACID?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000016-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000016-a001-0000-0000-000000000001', '00000016-0000-0000-0000-000000000001', 'Evitar que transações concorrentes interfiram nos resultados umas das outras.', TRUE, 0),
    ('00000016-a002-0000-0000-000000000001', '00000016-0000-0000-0000-000000000001', 'Garantir que todas as alterações sejam persistidas no disco imediatamente.', FALSE, 1),
    ('00000016-a003-0000-0000-000000000001', '00000016-0000-0000-0000-000000000001', 'Permitir que qualquer transação seja revertida sem efeito.', FALSE, 2),
    ('00000016-a004-0000-0000-000000000001', '00000016-0000-0000-0000-000000000001', 'Assegurar que dados sejam replicados entre nós.', FALSE, 3);

-- Question 17: Software Development by Junior Dev
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
    '00000017-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Níveis de cache da CPU',
    'Qual nível de cache tipicamente é o mais rápido e menor em capacidade?',
    'EASY',
    3,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    3,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000017-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000017-a001-0000-0000-000000000001', '00000017-0000-0000-0000-000000000001', 'Cache L1', TRUE, 0),
    ('00000017-a002-0000-0000-000000000001', '00000017-0000-0000-0000-000000000001', 'Cache L3', FALSE, 1),
    ('00000017-a003-0000-0000-000000000001', '00000017-0000-0000-0000-000000000001', 'Memória RAM', FALSE, 2),
    ('00000017-a004-0000-0000-000000000001', '00000017-0000-0000-0000-000000000001', 'Disco SSD', FALSE, 3);

-- Question 18: Artificial Intelligence by Mariana Costa
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
    '00000018-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Regularização L1 vs L2',
    'Qual diferença prática entre regularização L1 e L2 em modelos lineares?',
    'MEDIUM',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000018-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000018-a001-0000-0000-000000000001', '00000018-0000-0000-0000-000000000001', 'L1 tende a gerar coeficientes esparsos (zero), L2 distribui penalidade mais uniformemente.', TRUE, 0),
    ('00000018-a002-0000-0000-000000000001', '00000018-0000-0000-0000-000000000001', 'L1 sempre é melhor que L2 em todos os casos.', FALSE, 1),
    ('00000018-a003-0000-0000-000000000001', '00000018-0000-0000-0000-000000000001', 'L2 zera coeficientes automaticamente.', FALSE, 2),
    ('00000018-a004-0000-0000-000000000001', '00000018-0000-0000-0000-000000000001', 'L1 aumenta o overfitting por padrão.', FALSE, 3);

-- Question 19: Networks by Roberto Lima
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
    '00000019-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'TCP vs UDP',
    'Qual protocolo oferece garantia de entrega e controle de fluxo, mas com maior overhead?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000019-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000019-a001-0000-0000-000000000001', '00000019-0000-0000-0000-000000000001', 'TCP', TRUE, 0),
    ('00000019-a002-0000-0000-000000000001', '00000019-0000-0000-0000-000000000001', 'UDP', FALSE, 1),
    ('00000019-a003-0000-0000-000000000001', '00000019-0000-0000-0000-000000000001', 'ICMP', FALSE, 2),
    ('00000019-a004-0000-0000-000000000001', '00000019-0000-0000-0000-000000000001', 'ARP', FALSE, 3);

-- Question 20: Cybersecurity by Carlos Silva
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
    '00000020-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Fluxos OAuth2: Authorization Code',
    'Qual característica distingue o fluxo "Authorization Code" de OAuth2 em relação ao "Implicit"?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000020-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000020-a001-0000-0000-000000000001', '00000020-0000-0000-0000-000000000001', 'Authorization Code troca um código por token no servidor backend, evitando exposição do token ao browser.', TRUE, 0),
    ('00000020-a002-0000-0000-000000000001', '00000020-0000-0000-0000-000000000001', 'Implicit envia refresh tokens para o cliente pela URL.', FALSE, 1),
    ('00000020-a003-0000-0000-000000000001', '00000020-0000-0000-0000-000000000001', 'Authorization Code não usa redirect URIs.', FALSE, 2),
    ('00000020-a004-0000-0000-000000000001', '00000020-0000-0000-0000-000000000001', 'Implicit é o mais seguro para aplicações server-side.', FALSE, 3);

-- Question 21: Database by Roberto Lima
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
    '00000021-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'Teorema CAP em sistemas distribuídos',
    'Segundo o teorema CAP, ao ocorrer uma partição de rede, um sistema distribuído deve escolher entre quais propriedades?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000021-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000021-a001-0000-0000-000000000001', '00000021-0000-0000-0000-000000000001', 'Consistência (Consistency) ou Disponibilidade (Availability).', TRUE, 0),
    ('00000021-a002-0000-0000-000000000001', '00000021-0000-0000-0000-000000000001', 'Partição (Partition) ou Escalabilidade (Scalability).', FALSE, 1),
    ('00000021-a003-0000-0000-000000000001', '00000021-0000-0000-0000-000000000001', 'Durabilidade (Durability) ou Confidencialidade (Confidentiality).', FALSE, 2),
    ('00000021-a004-0000-0000-000000000001', '00000021-0000-0000-0000-000000000001', 'Integridade (Integrity) ou Autenticidade (Authenticity).', FALSE, 3);

-- Question 22: Software Development by Mariana Costa
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
    '00000022-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'SOLID: Single Responsibility Principle',
    'O que afirma o Princípio da Responsabilidade Única (SRP)?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000022-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000022-a001-0000-0000-000000000001', '00000022-0000-0000-0000-000000000001', 'Uma classe deve ter apenas uma razão para mudar (uma única responsabilidade).', TRUE, 0),
    ('00000022-a002-0000-0000-000000000001', '00000022-0000-0000-0000-000000000001', 'Classes devem expor todos os seus detalhes de implementação.', FALSE, 1),
    ('00000022-a003-0000-0000-000000000001', '00000022-0000-0000-0000-000000000001', 'Evitar qualquer comentário no código fonte.', FALSE, 2),
    ('00000022-a004-0000-0000-000000000001', '00000022-0000-0000-0000-000000000001', 'Sempre usar herança múltipla para reutilização.', FALSE, 3);

-- Question 23: Software Development by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000023-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Conceito de Interface em Java',
    'Qual é o principal objetivo de uma interface em Java?',
    'EASY',
    'FOUR',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    'FOUR',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000023-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000023-a001-0000-0000-000000000001', '00000023-0000-0000-0000-000000000001', 'Definir um contrato que classes devem implementar.', TRUE, 0),
    ('00000023-a002-0000-0000-000000000001', '00000023-0000-0000-0000-000000000001', 'Permitir herança múltipla de implementação.', FALSE, 1),
    ('00000023-a003-0000-0000-000000000001', '00000023-0000-0000-0000-000000000001', 'Armazenar estado interno obrigatoriamente.', FALSE, 2),
    ('00000023-a004-0000-0000-000000000001', '00000023-0000-0000-0000-000000000001', 'Substituir completamente classes abstratas.', FALSE, 3);

-- Question 24: Database by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000024-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Chave Primária em Banco de Dados',
    'Qual é a principal característica de uma chave primária?',
    'EASY',
    'THREE',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    'THREE',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000024-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000024-a001-0000-0000-000000000001', '00000024-0000-0000-0000-000000000001', 'Identificar unicamente cada registro da tabela.', TRUE, 0),
    ('00000024-a002-0000-0000-000000000001', '00000024-0000-0000-0000-000000000001', 'Permitir valores duplicados.', FALSE, 1),
    ('00000024-a003-0000-0000-000000000001', '00000024-0000-0000-0000-000000000001', 'Ser obrigatoriamente do tipo VARCHAR.', FALSE, 2),
    ('00000024-a004-0000-0000-000000000001', '00000024-0000-0000-0000-000000000001', 'Ser opcional em uma tabela relacional.', FALSE, 3);

-- Question 25: Networks by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000025-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Modelo OSI',
    'Quantas camadas possui o modelo OSI?',
    'EASY',
    'FOUR',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    'FOUR',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000025-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000025-a001-0000-0000-000000000001', '00000025-0000-0000-0000-000000000001', '7 camadas.', TRUE, 0),
    ('00000025-a002-0000-0000-000000000001', '00000025-0000-0000-0000-000000000001', '4 camadas.', FALSE, 1),
    ('00000025-a003-0000-0000-000000000001', '00000025-0000-0000-0000-000000000001', '5 camadas.', FALSE, 2),
    ('00000025-a004-0000-0000-000000000001', '00000025-0000-0000-0000-000000000001', '10 camadas.', FALSE, 3);

-- Question 26: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000026-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Validação Cruzada',
    'Qual é o objetivo principal da validação cruzada (cross-validation)?',
    'MEDIUM',
    'FIVE',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    'FIVE',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000026-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000026-a001-0000-0000-000000000001', '00000026-0000-0000-0000-000000000001', 'Avaliar a capacidade de generalização do modelo.', TRUE, 0),
    ('00000026-a002-0000-0000-000000000001', '00000026-0000-0000-0000-000000000001', 'Aumentar o tamanho do dataset.', FALSE, 1),
    ('00000026-a003-0000-0000-000000000001', '00000026-0000-0000-0000-000000000001', 'Eliminar a necessidade de teste.', FALSE, 2),
    ('00000026-a004-0000-0000-000000000001', '00000026-0000-0000-0000-000000000001', 'Garantir 100% de acurácia.', FALSE, 3);

-- Question 27: Cybersecurity by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000027-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Ataque de Phishing',
    'O que caracteriza um ataque de phishing?',
    'MEDIUM',
    'FOUR',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    'FOUR',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000027-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000027-a001-0000-0000-000000000001', '00000027-0000-0000-0000-000000000001', 'Enganar usuários para obter informações sensíveis.', TRUE, 0),
    ('00000027-a002-0000-0000-000000000001', '00000027-0000-0000-0000-000000000001', 'Interceptar tráfego via força bruta.', FALSE, 1),
    ('00000027-a003-0000-0000-000000000001', '00000027-0000-0000-0000-000000000001', 'Explorar falhas físicas de hardware.', FALSE, 2),
    ('00000027-a004-0000-0000-000000000001', '00000027-0000-0000-0000-000000000001', 'Aumentar largura de banda da rede.', FALSE, 3);

-- Question 28: Database by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000028-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Normalização 3FN',
    'Qual é o objetivo da Terceira Forma Normal (3FN)?',
    'MEDIUM',
    'FOUR',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    'FOUR',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000028-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000028-a001-0000-0000-000000000001', '00000028-0000-0000-0000-000000000001', 'Eliminar dependências transitivas.', TRUE, 0),
    ('00000028-a002-0000-0000-000000000001', '00000028-0000-0000-0000-000000000001', 'Permitir redundância controlada.', FALSE, 1),
    ('00000028-a003-0000-0000-000000000001', '00000028-0000-0000-0000-000000000001', 'Remover todas as chaves estrangeiras.', FALSE, 2),
    ('00000028-a004-0000-0000-000000000001', '00000028-0000-0000-0000-000000000001', 'Eliminar chaves primárias.', FALSE, 3);

-- Question 29: Software Development by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000029-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Concorrência e Deadlock',
    'Qual condição é necessária para ocorrer deadlock?',
    'HARD',
    'FIVE',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    'FIVE',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000029-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000029-a001-0000-0000-000000000001', '00000029-0000-0000-0000-000000000001', 'Espera circular entre processos.', TRUE, 0),
    ('00000029-a002-0000-0000-000000000001', '00000029-0000-0000-0000-000000000001', 'Execução sequencial.', FALSE, 1),
    ('00000029-a003-0000-0000-000000000001', '00000029-0000-0000-0000-000000000001', 'Uso de memória heap.', FALSE, 2),
    ('00000029-a004-0000-0000-000000000001', '00000029-0000-0000-0000-000000000001', 'Coleta de lixo automática.', FALSE, 3);

-- Question 30: Networks by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000030-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'TCP vs UDP',
    'Qual característica diferencia o TCP do UDP?',
    'HARD',
    'FOUR',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    'FOUR',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000030-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000030-a001-0000-0000-000000000001', '00000030-0000-0000-0000-000000000001', 'TCP é orientado à conexão e garante entrega confiável.', TRUE, 0),
    ('00000030-a002-0000-0000-000000000001', '00000030-0000-0000-0000-000000000001', 'UDP garante retransmissão automática.', FALSE, 1),
    ('00000030-a003-0000-0000-000000000001', '00000030-0000-0000-0000-000000000001', 'TCP não utiliza portas.', FALSE, 2),
    ('00000030-a004-0000-0000-000000000001', '00000030-0000-0000-0000-000000000001', 'UDP é sempre mais seguro.', FALSE, 3);

-- Question 31: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000031-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Regularização L2',
    'Qual é o efeito principal da regularização L2 em modelos de regressão?',
    'HARD',
    'FIVE',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    'FIVE',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000031-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000031-a001-0000-0000-000000000001', '00000031-0000-0000-0000-000000000001', 'Penalizar pesos grandes para reduzir overfitting.', TRUE, 0),
    ('00000031-a002-0000-0000-000000000001', '00000031-0000-0000-0000-000000000001', 'Eliminar variáveis irrelevantes automaticamente.', FALSE, 1),
    ('00000031-a003-0000-0000-000000000001', '00000031-0000-0000-0000-000000000001', 'Aumentar dimensionalidade.', FALSE, 2),
    ('00000031-a004-0000-0000-000000000001', '00000031-0000-0000-0000-000000000001', 'Garantir convergência exata.', FALSE, 3);

-- Question 32: Cybersecurity and Database by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000032-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'SQL Injection',
    'Qual prática ajuda a prevenir ataques de SQL Injection?',
    'HARD',
    'FIVE',
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    'FIVE',
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000032-0000-0000-0000-000000000001', 'CYBERSECURITY'),
    ('00000032-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000032-a001-0000-0000-000000000001', '00000032-0000-0000-0000-000000000001', 'Uso de prepared statements com parâmetros.', TRUE, 0),
    ('00000032-a002-0000-0000-000000000001', '00000032-0000-0000-0000-000000000001', 'Concatenar strings dinamicamente.', FALSE, 1),
    ('00000032-a003-0000-0000-000000000001', '00000032-0000-0000-0000-000000000001', 'Desabilitar logs.', FALSE, 2),
    ('00000032-a004-0000-0000-000000000001', '00000032-0000-0000-0000-000000000001', 'Usar apenas SELECT.', FALSE, 3);

-- Question 33: Software Development by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000033-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Códigos de status HTTP: criação de recurso',
    'Qual código de status HTTP indica que um recurso foi criado com sucesso?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000033-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000033-a001-0000-0000-000000000001', '00000033-0000-0000-0000-000000000001', '201 Created', TRUE, 0),
    ('00000033-a002-0000-0000-000000000001', '00000033-0000-0000-0000-000000000001', '200 OK', FALSE, 1),
    ('00000033-a003-0000-0000-000000000001', '00000033-0000-0000-0000-000000000001', '404 Not Found', FALSE, 2),
    ('00000033-a004-0000-0000-000000000001', '00000033-0000-0000-0000-000000000001', '500 Internal Server Error', FALSE, 3);

-- Question 34: Database by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000034-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Transações: rollback',
    'O que ocorre quando uma transação é submetida a rollback?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000034-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000034-a001-0000-0000-000000000001', '00000034-0000-0000-0000-000000000001', 'Todas as alterações realizadas na transação são desfeitas.', TRUE, 0),
    ('00000034-a002-0000-0000-000000000001', '00000034-0000-0000-0000-000000000001', 'As alterações são automaticamente confirmadas (committed).', FALSE, 1),
    ('00000034-a003-0000-0000-000000000001', '00000034-0000-0000-0000-000000000001', 'A transação é convertida em leitura apenas.', FALSE, 2),
    ('00000034-a004-0000-0000-000000000001', '00000034-0000-0000-0000-000000000001', 'Os registros são duplicados para redundância.', FALSE, 3);

-- Question 35: Networks by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000035-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'Máscaras de sub-rede / CIDR',
    'Qual prefixo CIDR corresponde à máscara 255.255.255.0?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000035-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000035-a001-0000-0000-000000000001', '00000035-0000-0000-0000-000000000001', '/24', TRUE, 0),
    ('00000035-a002-0000-0000-000000000001', '00000035-0000-0000-0000-000000000001', '/16', FALSE, 1),
    ('00000035-a003-0000-0000-000000000001', '00000035-0000-0000-0000-000000000001', '/8', FALSE, 2),
    ('00000035-a004-0000-0000-000000000001', '00000035-0000-0000-0000-000000000001', '/32', FALSE, 3);

-- Question 36: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000036-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Vanishing Gradient',
    'O que caracteriza o problema do "vanishing gradient" em redes neurais profundas?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000036-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000036-a001-0000-0000-000000000001', '00000036-0000-0000-0000-000000000001', 'Gradientes que se tornam muito pequenos ao propagar pelo tempo/estágios, dificultando aprendizado de camadas profundas.', TRUE, 0),
    ('00000036-a002-0000-0000-000000000001', '00000036-0000-0000-0000-000000000001', 'Gradientes que explodem indefinidamente durante o treino.', FALSE, 1),
    ('00000036-a003-0000-0000-000000000001', '00000036-0000-0000-0000-000000000001', 'Ausência de regularização no modelo.', FALSE, 2),
    ('00000036-a004-0000-0000-000000000001', '00000036-0000-0000-0000-000000000001', 'Dados de treino insuficientes apenas.', FALSE, 3);

-- Question 37: Cybersecurity by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000037-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'HTTPS: objetivo principal',
    'Qual é o principal objetivo do HTTPS em relação ao HTTP?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000037-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000037-a001-0000-0000-000000000001', '00000037-0000-0000-0000-000000000001', 'Criptografar a comunicação entre cliente e servidor para garantir confidencialidade e integridade.', TRUE, 0),
    ('00000037-a002-0000-0000-000000000001', '00000037-0000-0000-0000-000000000001', 'Reduzir o tamanho das respostas HTTP.', FALSE, 1),
    ('00000037-a003-0000-0000-000000000001', '00000037-0000-0000-0000-000000000001', 'Aumentar a velocidade do protocolo sem criptografia.', FALSE, 2),
    ('00000037-a004-0000-0000-000000000001', '00000037-0000-0000-0000-000000000001', 'Substituir DNS.', FALSE, 3);

-- Question 38: Software Development by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000038-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Async/Await vs Promises',
    'O que o async/await fornece em cima das Promises no JavaScript?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000038-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000038-a001-0000-0000-000000000001', '00000038-0000-0000-0000-000000000001', 'Sintaxe que permite escrever código assíncrono de forma imperativa e mais legível sobre Promises.', TRUE, 0),
    ('00000038-a002-0000-0000-000000000001', '00000038-0000-0000-0000-000000000001', 'Substitui totalmente o modelo de Promises com eventos.', FALSE, 1),
    ('00000038-a003-0000-0000-000000000001', '00000038-0000-0000-0000-000000000001', 'Força execução síncrona do código.', FALSE, 2),
    ('00000038-a004-0000-0000-000000000001', '00000038-0000-0000-0000-000000000001', 'Remove suporte a callbacks.', FALSE, 3);

-- Question 39: Database by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000039-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Índices: B-tree vs Hash',
    'Em que cenário um índice do tipo hash tende a ser mais eficiente que um B-tree?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000039-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000039-a001-0000-0000-000000000001', '00000039-0000-0000-0000-000000000001', 'Quando consultas são de igualdade exata ( = ) e não requerem ordenação.', TRUE, 0),
    ('00000039-a002-0000-0000-000000000001', '00000039-0000-0000-0000-000000000001', 'Para ordenação de grandes ranges.', FALSE, 1),
    ('00000039-a003-0000-0000-000000000001', '00000039-0000-0000-0000-000000000001', 'Quando índices precisam suportar junções (JOINs) exclusivamente.', FALSE, 2),
    ('00000039-a004-0000-0000-000000000001', '00000039-0000-0000-0000-000000000001', 'Quando o disco é do tipo magnético apenas.', FALSE, 3);

-- Question 40: Networks by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000040-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'BGP: visão geral',
    'O que é o BGP em redes?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000040-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000040-a001-0000-0000-000000000001', '00000040-0000-0000-0000-000000000001', 'Protocolo de roteamento entre sistemas autônomos (Internet), baseado em políticas e rotas.', TRUE, 0),
    ('00000040-a002-0000-0000-000000000001', '00000040-0000-0000-0000-000000000001', 'Protocolo de transporte confiável sobre IP.', FALSE, 1),
    ('00000040-a003-0000-0000-000000000001', '00000040-0000-0000-0000-000000000001', 'Um tipo de firewall de aplicação.', FALSE, 2),
    ('00000040-a004-0000-0000-000000000001', '00000040-0000-0000-0000-000000000001', 'Um protocolo de criptografia para e-mails.', FALSE, 3);

-- Question 41: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000041-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Aprendizado supervisionado vs não-supervisionado',
    'Qual a diferença básica entre aprendizado supervisionado e não-supervisionado?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000041-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000041-a001-0000-0000-000000000001', '00000041-0000-0000-0000-000000000001', 'Supervisionado usa rótulos (labels) no treino; não-supervisionado não usa.', TRUE, 0),
    ('00000041-a002-0000-0000-000000000001', '00000041-0000-0000-0000-000000000001', 'Não-supervisionado sempre é mais preciso.', FALSE, 1),
    ('00000041-a003-0000-0000-000000000001', '00000041-0000-0000-0000-000000000001', 'Supervisionado não usa dados de entrada.', FALSE, 2),
    ('00000041-a004-0000-0000-000000000001', '00000041-0000-0000-0000-000000000001', 'Ambos são exatamente a mesma técnica.', FALSE, 3);

-- Question 42: Cybersecurity by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000042-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Princípio do menor privilégio',
    'O que expressa o Princípio do Menor Privilégio na segurança de sistemas?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000042-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000042-a001-0000-0000-000000000001', '00000042-0000-0000-0000-000000000001', 'Usuários e processos devem receber apenas as permissões estritamente necessárias para executar suas tarefas.', TRUE, 0),
    ('00000042-a002-0000-0000-000000000001', '00000042-0000-0000-0000-000000000001', 'Conceder privilégios administrativos a todos por conveniência.', FALSE, 1),
    ('00000042-a003-0000-0000-000000000001', '00000042-0000-0000-0000-000000000001', 'Remover autenticação para facilitar acesso.', FALSE, 2),
    ('00000042-a004-0000-0000-000000000001', '00000042-0000-0000-0000-000000000001', 'Garantir que todos tenham privilégios iguais.', FALSE, 3);

-- Question 43: Software Development by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000043-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Complexidade O(n log n)',
    'Qual algoritmo clássico de ordenação possui complexidade média O(n log n)?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000043-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000043-a001-0000-0000-000000000001', '00000043-0000-0000-0000-000000000001', 'Merge Sort', TRUE, 0),
    ('00000043-a002-0000-0000-000000000001', '00000043-0000-0000-0000-000000000001', 'Bubble Sort', FALSE, 1),
    ('00000043-a003-0000-0000-000000000001', '00000043-0000-0000-0000-000000000001', 'Insertion Sort', FALSE, 2),
    ('00000043-a004-0000-0000-000000000001', '00000043-0000-0000-0000-000000000001', 'Selection Sort', FALSE, 3);

-- Question 44: Cybersecurity by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000044-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Prevenção contra SQL Injection',
    'Qual prática é mais eficaz para prevenir SQL Injection?',
    'EASY',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000044-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000044-a001-0000-0000-000000000001', '00000044-0000-0000-0000-000000000001', 'Uso de prepared statements / queries parametrizadas.', TRUE, 0),
    ('00000044-a002-0000-0000-000000000001', '00000044-0000-0000-0000-000000000001', 'Concatenação direta de strings na query.', FALSE, 1),
    ('00000044-a003-0000-0000-000000000001', '00000044-0000-0000-0000-000000000001', 'Remover validação de entrada.', FALSE, 2),
    ('00000044-a004-0000-0000-000000000001', '00000044-0000-0000-0000-000000000001', 'Expor mensagens de erro detalhadas ao usuário.', FALSE, 3);

-- Question 45: Database by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000045-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'Deadlock em bancos de dados',
    'O que caracteriza um deadlock em um sistema de banco de dados?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000045-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000045-a001-0000-0000-000000000001', '00000045-0000-0000-0000-000000000001', 'Duas ou mais transações ficam esperando indefinidamente por recursos bloqueados umas pelas outras.', TRUE, 0),
    ('00000045-a002-0000-0000-000000000001', '00000045-0000-0000-0000-000000000001', 'Uma consulta retorna muitos registros.', FALSE, 1),
    ('00000045-a003-0000-0000-000000000001', '00000045-0000-0000-0000-000000000001', 'Índices são recriados automaticamente.', FALSE, 2),
    ('00000045-a004-0000-0000-000000000001', '00000045-0000-0000-0000-000000000001', 'Backup é executado simultaneamente.', FALSE, 3);

-- Question 46: Networks by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000046-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'TCP vs UDP',
    'Qual das alternativas descreve corretamente uma diferença entre TCP e UDP?',
    'EASY',
    3,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    3,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000046-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id,question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000046-a001-0000-0000-000000000001', '00000046-0000-0000-0000-000000000001', 'TCP é orientado à conexão e garante entrega e ordem dos dados.', TRUE, 0),
    ('00000046-a002-0000-0000-000000000001', '00000046-0000-0000-0000-000000000001', 'UDP garante entrega confiável e controle de congestionamento.', FALSE, 1),
    ('00000046-a003-0000-0000-000000000001', '00000046-0000-0000-0000-000000000001', 'TCP é mais rápido que UDP porque não realiza controle de fluxo.', FALSE, 2),
    ('00000046-a004-0000-0000-000000000001', '00000046-0000-0000-0000-000000000001', 'UDP exige estabelecimento prévio de conexão antes do envio de dados.', FALSE, 3);

-- Question 47: Database by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000047-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Terceira Forma Normal (3NF)',
    'Uma tabela está na 3NF quando:',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000047-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000047-a001-0000-0000-000000000001', '00000047-0000-0000-0000-000000000001', 'Não possui dependências transitivas entre atributos não-chave.', TRUE, 0),
    ('00000047-a002-0000-0000-000000000001', '00000047-0000-0000-0000-000000000001', 'Possui colunas duplicadas.', FALSE, 1),
    ('00000047-a003-0000-0000-000000000001', '00000047-0000-0000-0000-000000000001', 'Todos os campos são obrigatórios.', FALSE, 2),
    ('00000047-a004-0000-0000-000000000001', '00000047-0000-0000-0000-000000000001', 'Não possui chave primária.', FALSE, 3);

-- Question 48: Cybersecurity by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000048-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'XSS (Cross-Site Scripting)',
    'Qual é a principal consequência de uma vulnerabilidade XSS?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000048-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000048-a001-0000-0000-000000000001', '00000048-0000-0000-0000-000000000001', 'Execução de scripts maliciosos no navegador da vítima.', TRUE, 0),
    ('00000048-a002-0000-0000-000000000001', '00000048-0000-0000-0000-000000000001', 'Queda física do servidor.', FALSE, 1),
    ('00000048-a003-0000-0000-000000000001', '00000048-0000-0000-0000-000000000001', 'Formatação automática do banco de dados.', FALSE, 2),
    ('00000048-a004-0000-0000-000000000001', '00000048-0000-0000-0000-000000000001', 'Desligamento do roteador.', FALSE, 3);

-- Question 49: Networks by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000049-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'TCP vs UDP',
    'Qual é uma característica principal do protocolo TCP em comparação ao UDP?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000049-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000049-a001-0000-0000-000000000001', '00000049-0000-0000-0000-000000000001', 'Fornece entrega confiável e controle de congestionamento.', TRUE, 0),
    ('00000049-a002-0000-0000-000000000001', '00000049-0000-0000-0000-000000000001', 'É sempre mais rápido que UDP.', FALSE, 1),
    ('00000049-a003-0000-0000-000000000001', '00000049-0000-0000-0000-000000000001', 'Não possui controle de fluxo.', FALSE, 2),
    ('00000049-a004-0000-0000-000000000001', '00000049-0000-0000-0000-000000000001', 'Não utiliza portas.', FALSE, 3);

-- Question 50: Software Development by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000050-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Teorema CAP',
    'Segundo o Teorema CAP, um sistema distribuído pode garantir simultaneamente:',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000050-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000050-a001-0000-0000-000000000001', '00000050-0000-0000-0000-000000000001', 'No máximo duas das três propriedades: Consistência, Disponibilidade e Tolerância a Partição.', TRUE, 0),
    ('00000050-a002-0000-0000-000000000001', '00000050-0000-0000-0000-000000000001', 'Todas as três propriedades sempre.', FALSE, 1),
    ('00000050-a003-0000-0000-000000000001', '00000050-0000-0000-0000-000000000001', 'Apenas Consistência.', FALSE, 2),
    ('00000050-a004-0000-0000-000000000001', '00000050-0000-0000-0000-000000000001', 'Somente Disponibilidade e Latência zero.', FALSE, 3);

-- Question 51: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000051-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Algoritmo K-Means',
    'O algoritmo K-Means é classificado como:',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000051-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000051-a001-0000-0000-000000000001', '00000051-0000-0000-0000-000000000001', 'Algoritmo de aprendizado não-supervisionado para clusterização.', TRUE, 0),
    ('00000051-a002-0000-0000-000000000001', '00000051-0000-0000-0000-000000000001', 'Algoritmo supervisionado de regressão linear.', FALSE, 1),
    ('00000051-a003-0000-0000-000000000001', '00000051-0000-0000-0000-000000000001', 'Algoritmo criptográfico simétrico.', FALSE, 2),
    ('00000051-a004-0000-0000-000000000001', '00000051-0000-0000-0000-000000000001', 'Protocolo de roteamento.', FALSE, 3);

-- Question 52: Cybersecurity by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000052-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Função de um Firewall',
    'Qual é a principal função de um firewall em uma rede?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000052-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000052-a001-0000-0000-000000000001', '00000052-0000-0000-0000-000000000001', 'Controlar e filtrar o tráfego de rede com base em regras de segurança.', TRUE, 0),
    ('00000052-a002-0000-0000-000000000001', '00000052-0000-0000-0000-000000000001', 'Aumentar a velocidade da internet.', FALSE, 1),
    ('00000052-a003-0000-0000-000000000001', '00000052-0000-0000-0000-000000000001', 'Substituir o sistema operacional do servidor.', FALSE, 2),
    ('00000052-a004-0000-0000-000000000001', '00000052-0000-0000-0000-000000000001', 'Criptografar automaticamente todos os arquivos do usuário.', FALSE, 3);

-- Question 53: Software Development by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000053-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Factory vs Abstract Factory',
    'Qual a diferença principal entre o padrão Factory Method e o Abstract Factory?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000053-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000053-a001-0000-0000-000000000001', '00000053-0000-0000-0000-000000000001', 'Factory Method cria objetos através de subclasses; Abstract Factory fornece famílias relacionadas de objetos sem especificar classes concretas.', TRUE, 0),
    ('00000053-a002-0000-0000-000000000001', '00000053-0000-0000-0000-000000000001', 'São padrões idênticos com nomes diferentes.', FALSE, 1),
    ('00000053-a003-0000-0000-000000000001', '00000053-0000-0000-0000-000000000001', 'Abstract Factory é usado apenas para singletons.', FALSE, 2),
    ('00000053-a004-0000-0000-000000000001', '00000053-0000-0000-0000-000000000001', 'Factory Method exige reflexão para funcionar.', FALSE, 3);

-- Question 54: Software Development by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000054-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Estratégias de invalidação de cache',
    'Qual estratégia de invalidação de cache é geralmente recomendada quando os dados mudam com frequência e a validade temporal é crítica?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000054-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000054-a001-0000-0000-000000000001', '00000054-0000-0000-0000-000000000001', 'Usar TTL (time-to-live) curto e invalidação proativa no momento das atualizações.', TRUE, 0),
    ('00000054-a002-0000-0000-000000000001', '00000054-0000-0000-0000-000000000001', 'Nunca expirar entradas do cache.', FALSE, 1),
    ('00000054-a003-0000-0000-000000000001', '00000054-0000-0000-0000-000000000001', 'Limpar todo o cache a cada 24 horas apenas.', FALSE, 2),
    ('00000054-a004-0000-0000-000000000001', '00000054-0000-0000-0000-000000000001', 'Confiar no cache do navegador para dados sensíveis.', FALSE, 3);

-- Question 55: Database by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000055-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Eventual Consistency vs ACID',
    'O que descreve melhor o modelo "eventual consistency" em sistemas distribuídos NoSQL?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000055-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000055-a001-0000-0000-000000000001', '00000055-0000-0000-0000-000000000001', 'Os dados ficam consistentes eventualmente após algum tempo, permitindo maior disponibilidade e particionamento.', TRUE, 0),
    ('00000055-a002-0000-0000-000000000001', '00000055-0000-0000-0000-000000000001', 'Garante consistência fortemente imediata como em ACID.', FALSE, 1),
    ('00000055-a003-0000-0000-000000000001', '00000055-0000-0000-0000-000000000001', 'Significa que backups são instantâneos.', FALSE, 2),
    ('00000055-a004-0000-0000-000000000001', '00000055-0000-0000-0000-000000000001', 'É aplicável apenas a bancos relacionais.', FALSE, 3);

-- Question 56:Cybersecurity by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000056-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'Estratégias de Rate Limiting',
    'Qual técnica de rate limiting é adequada para permitir ráfagas curtas de tráfego, mas limitar a taxa média ao longo do tempo?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000056-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000056-a001-0000-0000-000000000001', '00000056-0000-0000-0000-000000000001', 'Token bucket (bucket de tokens).', TRUE, 0),
    ('00000056-a002-0000-0000-000000000001', '00000056-0000-0000-0000-000000000001', 'Bloquear IP permanentemente no primeiro acesso.', FALSE, 1),
    ('00000056-a003-0000-0000-000000000001', '00000056-0000-0000-0000-000000000001', 'Ignorar cabeçalhos de rate limiting.', FALSE, 2),
    ('00000056-a004-0000-0000-000000000001', '00000056-0000-0000-0000-000000000001', 'Aumentar número de threads sem limites.', FALSE, 3);

-- Question 57: Software Development by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000057-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Kubernetes: Pod vs Deployment',
    'Qual a finalidade principal de um Deployment em Kubernetes em comparação a um Pod isolado?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000057-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000057-a001-0000-0000-000000000001', '00000057-0000-0000-0000-000000000001', 'Deployment gerencia réplicas, atualizações e reconciliação; Pods são unidades efêmeras que executam containers.', TRUE, 0),
    ('00000057-a002-0000-0000-000000000001', '00000057-0000-0000-0000-000000000001', 'Pod é usado para atualizar o cluster inteiro automaticamente.', FALSE, 1),
    ('00000057-a003-0000-0000-000000000001', '00000057-0000-0000-0000-000000000001', 'Deployment é um tipo de serviço de rede.', FALSE, 2),
    ('00000057-a004-0000-0000-000000000001', '00000057-0000-0000-0000-000000000001', 'Pods são permanentes e nunca reiniciam.', FALSE, 3);

-- Question 58: Database by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000058-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'NoSQL: Document Stores',
    'Em qual cenário um banco de dados tipo document store (ex: MongoDB) costuma ser a escolha adequada?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000058-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000058-a001-0000-0000-000000000001', '00000058-0000-0000-0000-000000000001', 'Quando o modelo de dados é flexível e documentos com estruturas variáveis são comuns.', TRUE, 0),
    ('00000058-a002-0000-0000-000000000001', '00000058-0000-0000-0000-000000000001', 'Quando transações multi-tabela ACID são muito frequentes.', FALSE, 1),
    ('00000058-a003-0000-0000-000000000001', '00000058-0000-0000-0000-000000000001', 'Para substituir caches em memória exclusivamente.', FALSE, 2),
    ('00000058-a004-0000-0000-000000000001', '00000058-0000-0000-0000-000000000001', 'Quando o volume de dados é sempre pequeno e imutável.', FALSE, 3);

-- Question 59: Cybersecurity by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000059-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Algoritmos de hashing de senha',
    'Qual característica é crítica em algoritmos modernos de hashing de senhas (ex: bcrypt, Argon2)?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000059-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000059-a001-0000-0000-000000000001', '00000059-0000-0000-0000-000000000001', 'Resistência a ataques por força bruta através de custo computacional configurável (work factor).', TRUE, 0),
    ('00000059-a002-0000-0000-000000000001', '00000059-0000-0000-0000-000000000001', 'Ser rápido para gerar hashes em alta velocidade.', FALSE, 1),
    ('00000059-a003-0000-0000-000000000001', '00000059-0000-0000-0000-000000000001', 'Usar funções sem sal (salt).', FALSE, 2),
    ('00000059-a004-0000-0000-000000000001', '00000059-0000-0000-0000-000000000001', 'Armazenar a senha em texto claro no banco.', FALSE, 3);

-- Question 60: Software Development by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000060-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Prometheus: modelo pull',
    'Qual é a característica do modelo "pull" usado pelo Prometheus para coleta de métricas?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000060-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000060-a001-0000-0000-000000000001', '00000060-0000-0000-0000-000000000001', 'O servidor de métricas (Prometheus) puxa (scrapes) métricas dos endpoints instrumentados em intervalos periódicos.', TRUE, 0),
    ('00000060-a002-0000-0000-000000000001', '00000060-0000-0000-0000-000000000001', 'Prometheus exige que agentes empurrem métricas diretamente ao servidor por design.', FALSE, 1),
    ('00000060-a003-0000-0000-000000000001', '00000060-0000-0000-0000-000000000001', 'Prometheus armazena métricas apenas em logs de texto.', FALSE, 2),
    ('00000060-a004-0000-0000-000000000001', '00000060-0000-0000-0000-000000000001', 'Prometheus é um sistema de autenticação.', FALSE, 3);

-- Question 61: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000061-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Otimizador Adam',
    'Qual a vantagem principal do otimizador Adam em comparação ao SGD simples durante o treino de redes neurais?',
    'MEDIUM',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000061-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000061-a001-0000-0000-000000000001', '00000061-0000-0000-0000-000000000001', 'Adam adapta a taxa de aprendizado individualmente por parâmetro usando estimativas de momentos (mean e variance).', TRUE, 0),
    ('00000061-a002-0000-0000-000000000001', '00000061-0000-0000-0000-000000000001', 'Adam elimina a necessidade de funções de ativação.', FALSE, 1),
    ('00000061-a003-0000-0000-000000000001', '00000061-0000-0000-0000-000000000001', 'Adam é uma técnica de regularização.', FALSE, 2),
    ('00000061-a004-0000-0000-000000000001', '00000061-0000-0000-0000-000000000001', 'Adam sempre converge para o mínimo global.', FALSE, 3);

-- Question 62: Networks by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000062-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'OSI vs TCP/IP',
    'Qual a diferença principal entre o modelo OSI e o modelo TCP/IP em termos de camadas?',
    'EASY',
    3,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    3,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000062-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000062-a001-0000-0000-000000000001', '00000062-0000-0000-0000-000000000001', 'OSI define sete camadas conceituais; TCP/IP usa um modelo com camadas mais agregadas (tipicamente 4).', TRUE, 0),
    ('00000062-a002-0000-0000-000000000001', '00000062-0000-0000-0000-000000000001', 'OSI e TCP/IP têm exatamente o mesmo número de camadas e nomes.', FALSE, 1),
    ('00000062-a003-0000-0000-000000000001', '00000062-0000-0000-0000-000000000001', 'TCP/IP é apenas um protocolo de aplicação.', FALSE, 2),
    ('00000062-a004-0000-0000-000000000001', '00000062-0000-0000-0000-000000000001', 'OSI é um padrão exclusivamente para redes sem fio.', FALSE, 3);

-- Question 63: Database by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000063-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Índice em Banco de Dados',
    'Qual é o principal objetivo de um índice em um banco de dados relacional?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES
    ('00000063-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000063-a001-0000-0000-000000000001', '00000063-0000-0000-0000-000000000001', 'Melhorar a performance de consultas.', TRUE, 0),
    ('00000063-a002-0000-0000-000000000001', '00000063-0000-0000-0000-000000000001', 'Garantir integridade referencial.', FALSE, 1),
    ('00000063-a003-0000-0000-000000000001', '00000063-0000-0000-0000-000000000001', 'Reduzir o tamanho físico das tabelas.', FALSE, 2),
    ('00000063-a004-0000-0000-000000000001', '00000063-0000-0000-0000-000000000001', 'Substituir a necessidade de backups.', FALSE, 3);

-- Question 64: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000064-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Overfitting vs Underfitting',
    'Qual situação caracteriza underfitting em um modelo de Machine Learning?',
    'MEDIUM',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES
    ('00000064-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000064-a001-0000-0000-000000000001', '00000064-0000-0000-0000-000000000001', 'O modelo não consegue capturar os padrões do conjunto de treino.', TRUE, 0),
    ('00000064-a002-0000-0000-000000000001', '00000064-0000-0000-0000-000000000001', 'O modelo apresenta alta variância.', FALSE, 1),
    ('00000064-a003-0000-0000-000000000001', '00000064-0000-0000-0000-000000000001', 'O modelo memoriza o conjunto de treino.', FALSE, 2),
    ('00000064-a004-0000-0000-000000000001', '00000064-0000-0000-0000-000000000001', 'O modelo possui excesso de parâmetros.', FALSE, 3);

-- Question 65: Cybersecurity by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000065-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'HTTPS',
    'Qual é a principal função do protocolo HTTPS?',
    'EASY',
    3,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    3,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES
    ('00000065-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000065-a001-0000-0000-000000000001', '00000065-0000-0000-0000-000000000001', 'Criptografar a comunicação entre cliente e servidor.', TRUE, 0),
    ('00000065-a002-0000-0000-000000000001', '00000065-0000-0000-0000-000000000001', 'Aumentar a velocidade de transmissão.', FALSE, 1),
    ('00000065-a003-0000-0000-000000000001', '00000065-0000-0000-0000-000000000001', 'Substituir o DNS.', FALSE, 2),
    ('00000065-a004-0000-0000-000000000001', '00000065-0000-0000-0000-000000000001', 'Eliminar a necessidade de certificados digitais.', FALSE, 3);

-- Question 66: Database by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000066-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Índice Clusterizado vs Não Clusterizado',
    'Qual é a principal diferença entre um índice clusterizado e um índice não clusterizado?',
    'MEDIUM',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES
    ('00000066-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000066-a001-0000-0000-000000000001', '00000066-0000-0000-0000-000000000001', 'O índice clusterizado define a ordem física dos dados na tabela.', TRUE, 0),
    ('00000066-a002-0000-0000-000000000001', '00000066-0000-0000-0000-000000000001', 'O índice não clusterizado elimina a necessidade de chave primária.', FALSE, 1),
    ('00000066-a003-0000-0000-000000000001', '00000066-0000-0000-0000-000000000001', 'Ambos organizam fisicamente os dados da mesma forma.', FALSE, 2),
    ('00000066-a004-0000-0000-000000000001', '00000066-0000-0000-0000-000000000001', 'Índices clusterizados só podem ser criados em tabelas vazias.', FALSE, 3);

-- Question 67: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000067-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Validação Cruzada',
    'Qual é a finalidade da validação cruzada (cross-validation)?',
    'MEDIUM',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES
    ('00000067-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000067-a001-0000-0000-000000000001', '00000067-0000-0000-0000-000000000001', 'Avaliar a capacidade de generalização do modelo.', TRUE, 0),
    ('00000067-a002-0000-0000-000000000001', '00000067-0000-0000-0000-000000000001', 'Reduzir o número de features.', FALSE, 1),
    ('00000067-a003-0000-0000-000000000001', '00000067-0000-0000-0000-000000000001', 'Aumentar o tamanho do dataset.', FALSE, 2),
    ('00000067-a004-0000-0000-000000000001', '00000067-0000-0000-0000-000000000001', 'Eliminar overfitting automaticamente.', FALSE, 3);

-- Question 68: Software Development by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000068-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Thread em Java',
    'Qual interface deve ser implementada para criar uma thread em Java?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES
    ('00000068-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000068-a001-0000-0000-000000000001', '00000068-0000-0000-0000-000000000001', 'Runnable', TRUE, 0),
    ('00000068-a002-0000-0000-000000000001', '00000068-0000-0000-0000-000000000001', 'Serializable', FALSE, 1),
    ('00000068-a003-0000-0000-000000000001', '00000068-0000-0000-0000-000000000001', 'Cloneable', FALSE, 2),
    ('00000068-a004-0000-0000-000000000001', '00000068-0000-0000-0000-000000000001', 'Comparable', FALSE, 3);

-- Question 69: Cybersecurity by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000069-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Firewall',
    'Qual é a função principal de um firewall em uma rede?',
    'EASY',
    3,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    3,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES
    ('00000069-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000069-a001-0000-0000-000000000001', '00000069-0000-0000-0000-000000000001', 'Controlar o tráfego de rede com base em regras.', TRUE, 0),
    ('00000069-a002-0000-0000-000000000001', '00000069-0000-0000-0000-000000000001', 'Aumentar a largura de banda.', FALSE, 1),
    ('00000069-a003-0000-0000-000000000001', '00000069-0000-0000-0000-000000000001', 'Substituir o roteador.', FALSE, 2),
    ('00000069-a004-0000-0000-000000000001', '00000069-0000-0000-0000-000000000001', 'Eliminar a necessidade de antivírus.', FALSE, 3);

-- Question 70: Networks by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000070-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'NAT (Network Address Translation)',
    'Qual é a principal função do NAT (Network Address Translation) em redes IP?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000070-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000070-a001-0000-0000-000000000001', '00000070-0000-0000-0000-000000000001', 'Traduzir endereços privados para endereços públicos para comunicação na Internet.', TRUE, 0),
    ('00000070-a002-0000-0000-000000000001', '00000070-0000-0000-0000-000000000001', 'Aumentar a velocidade do link físico.', FALSE, 1),
    ('00000070-a003-0000-0000-000000000001', '00000070-0000-0000-0000-000000000001', 'Fornecer criptografia entre roteadores.', FALSE, 2),
    ('00000070-a004-0000-0000-000000000001', '00000070-0000-0000-0000-000000000001', 'Substituir DNS para resolução de nomes.', FALSE, 3);


-- Question 71: Cybersecurity by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000071-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Proteção contra força bruta',
    'Qual medida é mais eficaz para dificultar ataques de força bruta contra um serviço de autenticação?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000071-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000071-a001-0000-0000-000000000001', '00000071-0000-0000-0000-000000000001', 'Implementar bloqueio progressivo de tentativas e rate limiting.', TRUE, 0),
    ('00000071-a002-0000-0000-000000000001', '00000071-0000-0000-0000-000000000001', 'Permitir senhas curtas para facilidade do usuário.', FALSE, 1),
    ('00000071-a003-0000-0000-000000000001', '00000071-0000-0000-0000-000000000001', 'Exibir mensagens detalhadas de erro com stack trace.', FALSE, 2),
    ('00000071-a004-0000-0000-000000000001', '00000071-0000-0000-0000-000000000001', 'Desabilitar logs de autenticação no servidor.', FALSE, 3);

-- Question 72: Database by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000072-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'Índice composto',
    'Quando é apropriado criar um índice composto em uma tabela relacional?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000072-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000072-a001-0000-0000-000000000001', '00000072-0000-0000-0000-000000000001', 'Quando consultas frequentemente filtram por várias colunas na mesma ordem do índice.', TRUE, 0),
    ('00000072-a002-0000-0000-000000000001', '00000072-0000-0000-0000-000000000001', 'Sempre que a tabela tiver qualquer coluna do tipo TEXT.', FALSE, 1),
    ('00000072-a003-0000-0000-000000000001', '00000072-0000-0000-0000-000000000001', 'Para substituir chaves estrangeiras.', FALSE, 2),
    ('00000072-a004-0000-0000-000000000001', '00000072-0000-0000-0000-000000000001', 'Quando se deseja reduzir a durabilidade do banco.', FALSE, 3);

-- Question 73: Software Development by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000073-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Imutabilidade',
    'Qual vantagem principal o uso de objetos imutáveis traz em sistemas concorrentes?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000073-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000073-a001-0000-0000-000000000001', '00000073-0000-0000-0000-000000000001', 'Evitar condições de corrida porque o estado não muda após criação.', TRUE, 0),
    ('00000073-a002-0000-0000-000000000001', '00000073-0000-0000-0000-000000000001', 'Aumentar necessidade de sincronização pesada.', FALSE, 1),
    ('00000073-a003-0000-0000-000000000001', '00000073-0000-0000-0000-000000000001', 'Impedir toda forma de paralelismo.', FALSE, 2),
    ('00000073-a004-0000-0000-000000000001', '00000073-0000-0000-0000-000000000001', 'Exigir que todos os objetos sejam serializáveis.', FALSE, 3);

-- Question 74: Cybersecurity by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000074-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Validação de Entrada',
    'Qual prática reduz o risco de vulnerabilidades causadas por entrada do usuário em aplicações web?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000074-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000074-a001-0000-0000-000000000001', '00000074-0000-0000-0000-000000000001', 'Sanitizar e validar todos os dados de entrada antes do processamento.', TRUE, 0),
    ('00000074-a002-0000-0000-000000000001', '00000074-0000-0000-0000-000000000001', 'Confiar cegamente em dados vindos do cliente.', FALSE, 1),
    ('00000074-a003-0000-0000-000000000001', '00000074-0000-0000-0000-000000000001', 'Expor erros detalhados ao usuário final.', FALSE, 2),
    ('00000074-a004-0000-0000-000000000001', '00000074-0000-0000-0000-000000000001', 'Armazenar senhas em texto claro para facilidade.', FALSE, 3);

-- Question 75: Database by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000075-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'Migrations de Banco de Dados',
    'Por que é importante versionar migrations de banco de dados em projetos colaborativos?',
    'EASY',
    3,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    3,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000075-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000075-a001-0000-0000-000000000001', '00000075-0000-0000-0000-000000000001', 'Para aplicar mudanças de schema de forma reproduzível e rastreável.', TRUE, 0),
    ('00000075-a002-0000-0000-000000000001', '00000075-0000-0000-0000-000000000001', 'Para executar mudanças de schema apenas localmente sem controle.', FALSE, 1),
    ('00000075-a003-0000-0000-000000000001', '00000075-0000-0000-0000-000000000001', 'Ignorar backups antes de migrar.', FALSE, 2),
    ('00000075-a004-0000-0000-000000000001', '00000075-0000-0000-0000-000000000001', 'Executar migrations somente em produção manualmente.', FALSE, 3);

-- Question 76: Software Development by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000076-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Principio DRY',
    'O que expressa o princípio DRY (Do not Repeat Yourself) no desenvolvimento de software?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000076-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000076-a001-0000-0000-000000000001', '00000076-0000-0000-0000-000000000001', 'Evitar duplicação de lógica e centralizar conhecimento em um único lugar.', TRUE, 0),
    ('00000076-a002-0000-0000-000000000001', '00000076-0000-0000-0000-000000000001', 'Repetir trechos de código para clareza.', FALSE, 1),
    ('00000076-a003-0000-0000-000000000001', '00000076-0000-0000-0000-000000000001', 'Escrever código sem comentários jamais.', FALSE, 2),
    ('00000076-a004-0000-0000-000000000001', '00000076-0000-0000-0000-000000000001', 'Duplicar estruturas para garantir performance.', FALSE, 3);

-- Question 77: Cybersecurity by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000077-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'CORS (Cross-Origin Resource Sharing)',
    'O cabeçalho CORS Access-Control-Allow-Origin controla qual aspecto do comportamento de navegadores?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000077-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000077-a001-0000-0000-000000000001', '00000077-0000-0000-0000-000000000001', 'Permite ao navegador aceitar respostas de origens específicas controladas pelo servidor.', TRUE, 0),
    ('00000077-a002-0000-0000-000000000001', '00000077-0000-0000-0000-000000000001', 'Criptografa automaticamente todas as requisições entre domínios.', FALSE, 1),
    ('00000077-a003-0000-0000-000000000001', '00000077-0000-0000-0000-000000000001', 'Impede que o servidor responda a qualquer requisição externa.', FALSE, 2),
    ('00000077-a004-0000-0000-000000000001', '00000077-0000-0000-0000-000000000001', 'Substitui a autenticação baseada em token.', FALSE, 3);

-- Question 78: Networks by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000078-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'TLS Handshake',
    'Qual é um dos propósitos principais do handshake no protocolo TLS?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000078-0000-0000-0000-000000000001', 'NETWORKS');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000078-a001-0000-0000-000000000001', '00000078-0000-0000-0000-000000000001', 'Negociar parâmetros de criptografia e autenticar as partes.', TRUE, 0),
    ('00000078-a002-0000-0000-000000000001', '00000078-0000-0000-0000-000000000001', 'Aumentar a largura de banda do canal.', FALSE, 1),
    ('00000078-a003-0000-0000-000000000001', '00000078-0000-0000-0000-000000000001', 'Substituir certificados por senhas.', FALSE, 2),
    ('00000078-a004-0000-0000-000000000001', '00000078-0000-0000-0000-000000000001', 'Executar compressão de payload para reduzir latência.', FALSE, 3);

-- Question 79: Software Development by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000079-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Pair Programming',
    'Qual benefício chave do pair programming em equipes de desenvolvimento ágil?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000079-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000079-a001-0000-0000-000000000001', '00000079-0000-0000-0000-000000000001', 'Melhorar qualidade do código e compartilhar conhecimento entre desenvolvedores.', TRUE, 0),
    ('00000079-a002-0000-0000-000000000001', '00000079-0000-0000-0000-000000000001', 'Diminuir colaboração e comunicação.', FALSE, 1),
    ('00000079-a003-0000-0000-000000000001', '00000079-0000-0000-0000-000000000001', 'Eliminar revisões de código permanentemente.', FALSE, 2),
    ('00000079-a004-0000-0000-000000000001', '00000079-0000-0000-0000-000000000001',  'Aumentar a burocracia no deploy.', FALSE, 3);

-- Question 80: Database by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000080-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Transação ACID',
    'Qual propriedade do ACID garante que uma transação seja totalmente executada ou totalmente revertida?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES
    ('00000080-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000080-a001-0000-0000-000000000001', '00000080-0000-0000-0000-000000000001', 'Atomicidade', TRUE, 0),
    ('00000080-a002-0000-0000-000000000001', '00000080-0000-0000-0000-000000000001', 'Consistência', FALSE, 1),
    ('00000080-a003-0000-0000-000000000001', '00000080-0000-0000-0000-000000000001', 'Isolamento', FALSE, 2),
    ('00000080-a004-0000-0000-000000000001', '00000080-0000-0000-0000-000000000001', 'Durabilidade', FALSE, 3);

-- =================================================================
--  PROJECT QUESTIONS
-- =================================================================

-- Project Question 81: Software Development and Cybersecurity by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, project_url
    ) KEY (id)
    VALUES (
    '00000081-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'API REST com Autenticação JWT',
    'Desenvolva uma API REST com autenticação e autorização usando JWT. Deve conter cadastro de usuários, login e controle de acesso por roles.',
    'MEDIUM',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 10, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0,
    'http://github.com/example/jwt-api'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000081-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT'),
    ('00000081-0000-0000-0000-000000000001', 'CYBERSECURITY');

-- Project Question 82: Artificial Intelligence by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, project_url
    ) KEY (id)
    VALUES (
    '00000082-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Pipeline de Machine Learning',
    'Implemente um pipeline completo de Machine Learning incluindo pré-processamento, treino, validação e avaliação de modelo.',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 14, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0,
    'http://github.com/example/ml-pipeline'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000082-0000-0000-0000-000000000001', 'AI');

-- Project Question 83: Artificial Intelligence by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, project_url
    ) KEY (id)
    VALUES (
    '00000083-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Sistema de Cache Distribuído',
    'Projete e implemente um sistema simples de cache distribuído utilizando Redis, incluindo estratégias de invalidação.',
    'HARD',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 10, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0,
    'http://github.com/example/distributed-cache'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000083-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

-- Project Question 84: Database by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, project_url
    ) KEY (id)
    VALUES (
    '00000084-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Modelagem de Banco de Dados Relacional',
    'Modele e implemente um banco de dados relacional normalizado (até 3NF) para um sistema de e-commerce.',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0,
    'http://github.com/example/ecommerce-db'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000084-0000-0000-0000-000000000001', 'DATABASE');

-- Project Question 85: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, project_url
    ) KEY (id)
    VALUES (
    '00000085-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Detecção de Anomalias',
    'Construa um modelo de detecção de anomalias para identificar transações suspeitas em um conjunto de dados financeiro.',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 14, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0,
    'http://github.com/example/anomaly-detection'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000085-0000-0000-0000-000000000001', 'AI');

-- Project Question 86: Cybersecurity by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, project_url
    ) KEY (id)
    VALUES (
    '00000086-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Implementação de Rate Limiting',
    'Implemente um mecanismo de rate limiting em uma API pública utilizando token bucket ou leaky bucket.',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0,
    'http://github.com/example/rate-limiting'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000086-0000-0000-0000-000000000001', 'CYBERSECURITY');

-- Project Question 87: Networks by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, project_url
    ) KEY (id)
    VALUES (
    '00000087-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'Monitoramento com Prometheus e Grafana',
    'Configure monitoramento de uma aplicação containerizada usando Prometheus e visualize métricas com Grafana.',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0,
    'http://github.com/example/monitoring-stack'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000087-0000-0000-0000-000000000001', 'NETWORKS');

-- Project Question 88: Software Development by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, project_url
    ) KEY (id)
    VALUES (
    '00000088-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Sistema de Mensageria com RabbitMQ',
    'Implemente um sistema baseado em mensageria utilizando RabbitMQ para comunicação assíncrona entre serviços.',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 10, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0,
    'http://github.com/example/rabbitmq-system'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000088-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

-- Project Question 89: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, project_url
    ) KEY (id)
    VALUES (
    '00000089-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Implementação de Sistema de Recomendação',
    'Desenvolva um sistema de recomendação simples utilizando filtragem colaborativa ou baseada em conteúdo.',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 14, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0,
    'http://github.com/example/recommendation-system'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000089-0000-0000-0000-000000000001', 'AI');

-- Project Question 90: Software Development and Networks by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, project_url
    ) KEY (id)
    VALUES (
    '00000090-0000-0000-0000-000000000001',
    'PROJECT',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Arquitetura com Microservices',
    'Projete uma arquitetura baseada em microservices incluindo gateway, service discovery e comunicação entre serviços.',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 14, CURRENT_TIMESTAMP),
    'FINISHED',
    5,
    0,
    'http://github.com/example/microservices-architecture'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000090-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT'),
    ('00000090-0000-0000-0000-000000000001', 'NETWORKS');

-- =================================================================
--  VOTING QUESTIONS
-- =================================================================

-- VOTING Question 91: Artificial Intelligence by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000091-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Rede Neural',
    'O que representa um neurônio em uma rede neural artificial?',
    'MEDIUM',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES
    ('00000091-0000-0000-0000-000000000001', 'AI');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000091-a001-0000-0000-000000000001', '00000091-0000-0000-0000-000000000001', 'Uma função que aplica pesos e uma função de ativação sobre entradas.', TRUE, 0),
    ('00000091-a002-0000-0000-000000000001', '00000091-0000-0000-0000-000000000001', 'Um banco de dados relacional.', FALSE, 1),
    ('00000091-a003-0000-0000-000000000001', '00000091-0000-0000-0000-000000000001', 'Um tipo de índice.', FALSE, 2),
    ('00000091-a004-0000-0000-000000000001', '00000091-0000-0000-0000-000000000001', 'Um protocolo de rede.', FALSE, 3);

-- VOTING Question 92: Networks by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    ) KEY (id)
    VALUES (
    '00000092-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'OWASP Top 10',
    'O que representa o OWASP Top 10 no contexto de segurança de aplicações?',
    'MEDIUM',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES
    ('00000092-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000092-a001-0000-0000-000000000001', '00000092-0000-0000-0000-000000000001', 'Uma lista das principais vulnerabilidades de segurança em aplicações web.', TRUE, 0),
    ('00000092-a002-0000-0000-000000000001', '00000092-0000-0000-0000-000000000001', 'Um framework de desenvolvimento seguro.', FALSE, 1),
    ('00000092-a003-0000-0000-000000000001', '00000092-0000-0000-0000-000000000001', 'Um protocolo de criptografia.', FALSE, 2),
    ('00000092-a004-0000-0000-000000000001', '00000092-0000-0000-0000-000000000001', 'Uma ferramenta automática de teste de carga.', FALSE, 3);

-- VOTING Question 93: Database by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000093-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Isolamento de Transações',
    'Qual nível de isolamento impede leituras sujas (dirty reads)?',
    'MEDIUM',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000093-0000-0000-0000-000000000001', 'DATABASE');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000093-a001-0000-0000-000000000001', '00000093-0000-0000-0000-000000000001', 'READ COMMITTED', TRUE,0),
    ('00000093-a002-0000-0000-000000000001', '00000093-0000-0000-0000-000000000001', 'READ UNCOMMITTED', FALSE,1),
    ('00000093-a003-0000-0000-000000000001', '00000093-0000-0000-0000-000000000001', 'REPEATABLE READ', FALSE,2),
    ('00000093-a004-0000-0000-000000000001', '00000093-0000-0000-0000-000000000001', 'SERIALIZABLE', FALSE,3);

-- VOTING Question 94: Cybersecurity by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000094-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'JWT',
    'Qual parte de um JSON Web Token contém as claims?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000094-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000094-a001-0000-0000-000000000001', '00000094-0000-0000-0000-000000000001', 'Payload', TRUE,0),
    ('00000094-a002-0000-0000-000000000001', '00000094-0000-0000-0000-000000000001', 'Header', FALSE,1),
    ('00000094-a003-0000-0000-000000000001', '00000094-0000-0000-0000-000000000001', 'Signature', FALSE,2),
    ('00000094-a004-0000-0000-000000000001', '00000094-0000-0000-0000-000000000001', 'Issuer', FALSE,3);

-- VOTING Question 95: Software Development by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000095-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Complexidade de Algoritmos',
    'Qual é a complexidade temporal média do algoritmo QuickSort?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES ('00000095-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index)
    KEY (id)
    VALUES
    ('00000095-a001-0000-0000-000000000001', '00000095-0000-0000-0000-000000000001', 'O(n log n)', TRUE,0),
    ('00000095-a002-0000-0000-000000000001', '00000095-0000-0000-0000-000000000001', 'O(n²)', FALSE,1),
    ('00000095-a003-0000-0000-000000000001', '00000095-0000-0000-0000-000000000001', 'O(log n)', FALSE,2),
    ('00000095-a004-0000-0000-000000000001', '00000095-0000-0000-0000-000000000001', 'O(n)', FALSE,3);

-- VOTING Question 96: Software Development by Carlos Silva
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000096-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'Garbage Collection',
    'Qual é o objetivo principal do Garbage Collector na JVM?',
    'EASY',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES ('00000096-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index)
    KEY (id)
    VALUES
    ('00000096-a001-0000-0000-000000000001', '00000096-0000-0000-0000-000000000001', 'Liberar memória de objetos não mais referenciados.', TRUE,0),
    ('00000096-a002-0000-0000-000000000001', '00000096-0000-0000-0000-000000000001', 'Otimizar consultas SQL.', FALSE,1),
    ('00000096-a003-0000-0000-000000000001', '00000096-0000-0000-0000-000000000001', 'Gerenciar threads automaticamente.', FALSE,2),
    ('00000096-a004-0000-0000-000000000001', '00000096-0000-0000-0000-000000000001',   'Criptografar dados sensíveis.', FALSE,3);

-- VOTING Question 97: Software Development by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000097-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'REST',
    'Qual método HTTP é semanticamente idempotente?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES ('00000097-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index)
    KEY (id)
    VALUES
    ('00000097-a001-0000-0000-000000000001',  '00000097-0000-0000-0000-000000000001',  'PUT', TRUE,0),
    ('00000097-a002-0000-0000-000000000001',  '00000097-0000-0000-0000-000000000001',  'POST', FALSE,1),
    ('00000097-a003-0000-0000-000000000001',  '00000097-0000-0000-0000-000000000001',  'PATCH', FALSE,2),
    ('00000097-a004-0000-0000-000000000001',  '00000097-0000-0000-0000-000000000001',  'CONNECT', FALSE,3);

-- VOTING Question 98: Cybersecurity by Roberto Lima
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000098-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'Hashing',
    'Qual é a principal característica de uma função hash criptográfica?',
    'HARD',
    5,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    5,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES ('00000098-0000-0000-0000-000000000001', 'CYBERSECURITY');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index)
    KEY (id)
    VALUES
    ('00000098-a001-0000-0000-000000000001',  '00000098-0000-0000-0000-000000000001',  'Ser praticamente impossível reverter o hash para o valor original.', TRUE,0),
    ('00000098-a002-0000-0000-000000000001',  '00000098-0000-0000-0000-000000000001',  'Permitir descriptografia simétrica.', FALSE,1),
    ('00000098-a003-0000-0000-000000000001',  '00000098-0000-0000-0000-000000000001',  'Gerar sempre o mesmo valor para entradas diferentes.', FALSE,2),
    ('00000098-a004-0000-0000-000000000001',  '00000098-0000-0000-0000-000000000001',  'Ser reversível com chave privada.', FALSE,3);

-- VOTING Question 99: Software Development by Mariana Costa
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000099-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'Docker',
    'Qual é a função principal de uma imagem Docker?',
    'EASY',
    3,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    3,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES ('00000099-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000099-a001-0000-0000-000000000001', '00000099-0000-0000-0000-000000000001', 'Servir como template imutável para criação de containers.', TRUE,0),
    ('00000099-a002-0000-0000-000000000001', '00000099-0000-0000-0000-000000000001', 'Executar processos diretamente no kernel.', FALSE,1),
    ('00000099-a003-0000-0000-000000000001', '00000099-0000-0000-0000-000000000001', 'Substituir máquinas virtuais fisicamente.', FALSE,2),
    ('00000099-a004-0000-0000-000000000001', '00000099-0000-0000-0000-000000000001', 'Gerenciar redes físicas.', FALSE,3);

-- VOTING Question 100: Software Development by Junior Dev
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count
    )
    KEY (id)
    VALUES (
    '00000100-0000-0000-0000-000000000001',
    'MULTIPLE_CHOICE',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'Map vs Set',
    'Qual é a principal diferença entre um Map e um Set em estruturas de dados?',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'VOTING',
    4,
    0
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area)
    KEY (question_id, knowledge_area)
    VALUES ('00000100-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');

MERGE INTO question_alternatives (id, question_id, text, correct, ord_index) KEY (id)
    VALUES
    ('00000100-a001-0000-0000-000000000001', '00000100-0000-0000-0000-000000000001', 'Map armazena pares chave-valor, Set armazena apenas valores únicos.', TRUE,0),
    ('00000100-a002-0000-0000-000000000001', '00000100-0000-0000-0000-000000000001', 'Set permite chaves duplicadas.', FALSE,1),
    ('00000100-a003-0000-0000-000000000001', '00000100-0000-0000-0000-000000000001', 'Map não permite iteração.', FALSE,2),
    ('00000100-a004-0000-0000-000000000001', '00000100-0000-0000-0000-000000000001', 'Set armazena pares ordenados por índice.', FALSE,3);
-- Open Question 101: Software Development by Ana Pereira
MERGE INTO questions (
    id, question_type, author_id, title, description,
    difficulty_by_community, relevance_by_community,
    submission_date, voting_end_date, status,
    relevance_byllm, recruiter_usage_count, guideline, visibility
    )
    KEY (id)
    VALUES (
    '00000101-0000-0000-0000-000000000001',
    'OPEN',
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'Revision of Pull Request',
    'Describe how you would evaluate a pull request before approving the merge.',
    'MEDIUM',
    4,
    CURRENT_TIMESTAMP,
    DATEADD('DAY', 7, CURRENT_TIMESTAMP),
    'FINISHED',
    4,
    0,
    'Consider quality, tests, security, and readability in the review.',
    'SHARED'
    );

MERGE INTO question_knowledge_areas (question_id, knowledge_area) KEY (question_id, knowledge_area)
    VALUES
    ('00000101-0000-0000-0000-000000000001', 'SOFTWARE_DEVELOPMENT');
