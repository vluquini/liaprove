-- =================================================================
--  USERS SCRIPT
-- =================================================================
--  Este script popula a tabela 'users' com dados de exemplo.
--  Os UUIDs são definidos explicitamente para permitir a referência
--  cruzada em outros scripts (como o de questions).
--  A senha para todos os usuários é 'password', e o hash correspondente
--  aqui é gerado por um Bcrypt.
-- =================================================================


-- =================================================================
--  ADMIN Users
-- =================================================================
MERGE INTO users (
    id,
    user_type,
    name,
    email,
    password_hash,
    occupation,
    bio,
    experience_level,
    role,
    vote_weight,
    total_assessments_taken,
    average_score,
    registration_date,
    last_login,
    status
    )
    KEY (id)
    VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'PROFESSIONAL',
    'Admin User',
    'admin@liaprove.com',
    '$2y$10$BdcrHJqQKS8J7lq6q1VRBulzONdSNeivReDpcUivKRh1C9o/dc62u',
    'System Administrator',
    'The main administrator account.',
    NULL,
    'ADMIN',
    10,
    0,
    0.0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE'
    );

-- =================================================================
--  PROFESSIONAL Users
-- =================================================================
MERGE INTO users (
    id,
    user_type,
    name,
    email,
    password_hash,
    occupation,
    bio,
    experience_level,
    role,
    vote_weight,
    total_assessments_taken,
    average_score,
    registration_date,
    last_login,
    status
    )
    KEY (id)
    VALUES
    (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12',
    'PROFESSIONAL',
    'Carlos Silva',
    'carlos.silva@example.com',
    '$2a$12$G4QsYvWPAPrwlICZ2Tgbeuplx3JywN2dB5ACBuARpAyVfs0mskvCe',
    'Senior Java Developer',
    '10+ years of experience in building scalable backend systems with Java and Spring.',
    'SENIOR',
    'PROFESSIONAL',
    5,
    15,
    88.5,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE'
    ),
    (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13',
    'PROFESSIONAL',
    'Mariana Costa',
    'mariana.costa@example.com',
    '$2a$12$EnF7iftkyeO2wBF4Y5bFCOruBtgVk7zOu4SxJVhT.Fv1kEk0O5EcS',
    'Data Scientist',
    'Specialist in machine learning models and data analysis with Python.',
    'PLENO',
    'PROFESSIONAL',
    3,
    5,
    92.0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE'
    ),
    (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14',
    'PROFESSIONAL',
    'Junior Dev',
    'junior.dev@example.com',
    '$2a$12$ydESUGHdYfYXDUtxvcpox.yvuYDYl6tx0Yq5D2/RL2rFTB/r1JhxW',
    'Frontend Developer',
    'Just starting my journey with React and TypeScript.',
    'JUNIOR',
    'PROFESSIONAL',
    1,
    2,
    75.0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE'
    );

-- =================================================================
--  RECRUITER Users
-- =================================================================
MERGE INTO users (
    id,
    user_type,
    name,
    email,
    password_hash,
    role,
    registration_date,
    status,
    company_name,
    company_email,
    vote_weight,
    average_score,
    total_assessments_taken,
    experience_level
    )
    KEY (id)
    VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15',
    'RECRUITER',
    'Ana Pereira',
    'ana.p@techrecruit.com',
    '$2a$12$ZeCUK5Nch8u4ARcN45OA5uU/Oh/ywkcOP.oxePI3/8N7P7V851JvG',
    'RECRUITER',
    CURRENT_TIMESTAMP,
    'ACTIVE',
    'TechRecruit',
    'contact@techrecruit.com',
    10,
    4.8,
    50,
    'SENIOR'
    );

MERGE INTO users (
    id,
    user_type,
    name,
    email,
    password_hash,
    role,
    registration_date,
    status,
    company_name,
    company_email,
    vote_weight,
    average_score,
    total_assessments_taken,
    experience_level
    )
    KEY (id)
    VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a16',
    'RECRUITER',
    'Roberto Lima',
    'roberto.l@hiredev.com',
    '$2a$12$f3kegv/uXWQdydlTtMzi6.X8H8Wy9xQSnM4UL.MV3v7wQ4GBkSPne',
    'RECRUITER',
    CURRENT_TIMESTAMP,
    'ACTIVE',
    'HireDev Solutions',
    'contact@hiredev.com',
    5,
    4.5,
    25,
    'PLENO'
    );
