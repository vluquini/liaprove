
# Plataforma Colaborativa de Avaliação Técnica orientada ao Mercado de Trabalho com uso de Inteligência Artificial

> **Nota:** Este projeto foi desenvolvido como Trabalho de Conclusão de Curso (TCC) e sua versão MVP foi finalizada.

## Sumário

- [Tecnologias e pré-requisitos](#1---tecnologias-e-pré-requisitos)
- [Execução local](#2---execução-local)
- [Profiles de execução](#3---profiles-de-execução)
- [Principais endpoints](#4---principais-endpoints)
- [Descrição geral](#5---descrição-geral)
- [Detalhamento da proposta](#6---detalhamento-da-proposta)

## 1 - Tecnologias e pré-requisitos

| Tecnologia | Versão / uso |
| --- | --- |
| Java | 21 |
| Spring Boot | 3.4.13 |
| Maven Wrapper | Maven 3.9.9 (já incluído no repositório) |
| PostgreSQL | 18, via Docker Compose, para o profile padrão |
| Docker e Docker Compose | Necessários para subir o PostgreSQL local |
| Node.js | 20 ou superior, para o frontend |
| Vue | 3.5.32 |
| Vite | 8.0.10 |

Também são utilizadas as bibliotecas MapStruct 1.6.3, Spring Data JPA, Spring Security, H2 e JWT.

## 2 - Execução local

### Backend com o profile `dev` (recomendado)

O profile `dev` usa uma base H2 local em arquivo e carrega dados de exemplo; portanto, não requer Docker nem variáveis de ambiente.

```bash
./mvnw clean install
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

A API ficará disponível em `http://localhost:8080`. Para executar somente os testes do backend:

```bash
./mvnw test -Dspring.profiles.active=dev
```

### Backend com PostgreSQL e Docker

Defina credenciais locais e inicie o banco:

```bash
export DB_USERNAME=liaprove
export DB_PASSWORD=liaprove
export JWT_SECRET='substitua-por-um-segredo-local-seguro'
export JWT_EXPIRATION=3600000
docker compose up -d
./mvnw spring-boot:run
```

Para encerrar o banco, execute `docker compose down`.

### Frontend

Em outro terminal, instale as dependências e inicie a interface correspondente ao tipo de usuário desejado:

```bash
cd frontend
npm install
npm run dev:professional
```

Há ainda os comandos `npm run dev:recruiter` e `npm run dev:admin`. Os testes e a compilação do frontend podem ser executados com:

```bash
npm test
npm run build
```

## 3 - Profiles de execução

| Profile | Banco de dados | Autenticação | Finalidade |
| --- | --- | --- | --- |
| Padrão (sem profile) | PostgreSQL local | JWT | Execução local com Docker e dados iniciais. |
| `dev` | H2 em arquivo | Sem login ou validação JWT | Desenvolvimento e testes manuais rápidos. |
| `e2e` | H2 em memória | JWT | Execução de testes ponta a ponta em ambiente efêmero. |
| `prod` | Banco externo configurado | JWT | Configuração base para produção, sem carga automática de dados. |

No profile `dev`, as requisições não exigem login nem token JWT. Para acessar endpoints protegidos, informe a identidade de um usuário carregado na base pelo cabeçalho `X-Dev-User-Email`; por exemplo: `X-Dev-User-Email: carlos.silva@example.com`. Esse mecanismo é exclusivo de desenvolvimento e não deve ser usado em produção. O console H2 também fica disponível em `http://localhost:8080/h2-console`.

## 4 - Principais endpoints

Com a aplicação em execução, os endpoints mais usados estão sob `http://localhost:8080`:

| Área | Endpoints principais |
| --- | --- |
| Autenticação | `POST /api/auth/register`, `POST /api/auth/login` |
| Usuários | `GET/PUT /api/v1/users/me`, `GET /api/v1/users/{id}`, `GET /api/v1/users/me/certificates` |
| Questões | `POST /api/v1/questions`, `POST /api/v1/questions/open`, `POST /api/v1/questions/pre-analysis`, `GET /api/v1/questions/voting` |
| Votos e feedbacks | `POST /api/v1/questions/{questionId}/vote`, `POST /api/v1/questions/{questionId}/feedback` |
| Avaliações | `POST /api/v1/assessments/start-system`, `POST /api/v1/assessments/{attemptId}/submit`, `POST /api/v1/assessments/personalized`, `GET /api/v1/assessments/personalized` |
| Certificados | `GET /api/v1/certificates/{certificateNumber}` |
| Administração | `/api/v1/admin/users`, `/api/v1/admin/questions`, `/api/v1/admin/assessments`, `/api/v1/admin/algorithms/genetic` |

Os endpoints administrativos exigem que a identidade utilizada tenha as permissões adequadas. Consulte os controllers em `src/main/java/com/lia/liaprove/infrastructure/controllers` para os payloads, parâmetros e operações completas.

## 5 - Descrição Geral

Plataforma colaborativa onde usuários profissionais de TI e recrutadores podem submeter questões e mini projetos para compor avaliações técnicas alinhadas a contextos reais do mercado de trabalho. As contribuições passam por curadoria da comunidade e apoiam tanto a autoavaliação dos participantes quanto a criação de avaliações personalizadas por recrutadores. A plataforma também utiliza Inteligência Artificial para apoiar a pré-análise de questões, a estruturação de descrições de vaga e a interpretação de tentativas em avaliações personalizadas. Ao finalizar uma avaliação de múltipla escolha e obter pelo menos 70% de acertos, o usuário recebe um certificado de comprovação de conhecimento.

## 6 - Detalhamento da Proposta

### 6.1 - Descrição

A plataforma permite que profissionais de TI e recrutadores criem e submetam questões e mini projetos para validar conhecimentos técnicos em áreas relevantes para o mercado de trabalho. As submissões passam por pré-análise feita por uma LLM e, depois, por votação da comunidade; cada usuário pode atribuir notas a cada questão.

Após a submissão e a votação, uma **Rede Bayesiana** apura os votos e decide se a questão será incorporada às avaliações da plataforma. Até a camada de aplicação, o motor bayesiano já contempla duas funções: decidir aprovação/reprovação de questões em votação e sugerir questões para recrutadores durante a criação de avaliações personalizadas. A integração demonstrativa da infraestrutura ainda mantém a aprovação/reprovação conectada a uma implementação mockada, preservando a lógica real pronta para futura ativação em produção. **Algoritmos Genéticos** monitoram e ajustam periodicamente o peso dos votos dos recrutadores, aumentando ou diminuindo conforme sinais de uso e qualidade. Hoje, o ajuste considera: uso recente (avaliações criadas/usadas no período), média das avaliações (rating médio das assessments), quantidade de questões aprovadas, razão de likes em comentários (likes/(likes+dislikes)) e o peso atual para estabilidade.

No contexto de recrutadores, a plataforma atua como ferramenta de apoio à avaliação técnica. Ela ajuda a estruturar critérios a partir de descrições de vaga, sugerir pesos entre hard skills, soft skills e experiência, selecionar questões e interpretar tentativas com apoio de IA. A decisão final de aprovar ou reprovar um candidato em um processo seletivo permanece humana e externa à regra automática da plataforma.

### 6.1.1 - Visão do Usuário Profissional

Um usuário do tipo profissional pode:

- Realizar avaliações (múltipla escolha e mini projetos).
    
- Submeter questões que julgar relevantes (essas só passam a integrar as avaliações após aprovação).
    
- Avaliar as questões enviadas por outros usuários, participando da curadoria colaborativa do acervo de questões.
    

### 6.1.2 - Visão do Usuário Recrutador

O recrutador possui as mesmas funcionalidades do profissional e, adicionalmente:

- Pode criar avaliações personalizadas (selecionar questões e definir o percentual de aprovação).

- Pode criar questões abertas para uso em avaliações personalizadas, com visibilidade privada ou compartilhada entre recrutadores.
    
- Tem votos com peso maior na avaliação das questões (peso ajustável por Algoritmos Genéticos).
    
- Pode solicitar à IA uma análise estruturada da descrição de uma vaga, obtendo áreas de conhecimento, hard skills, soft skills e sugestão de pesos para apoiar a montagem da avaliação.

- Pode solicitar uma pré-análise explicável das tentativas realizadas em suas avaliações personalizadas, como apoio à interpretação técnica do desempenho do candidato.

- Recebe sugestões inteligentes de questões e critérios de avaliação com base no contexto da vaga e em padrões de escolha de outros recrutadores.
    

### 6.1.3 - Tipos de Avaliação

A plataforma trabalha com avaliações do sistema e avaliações personalizadas:

1. **Prova de múltipla escolha**
    
    - Cada questão terá múltiplas alternativas (A–E ou conforme configuração).
        
    - Duração prevista: entre 30 minutos e 1 hora, dependendo do nível de dificuldade.
        
    - Aprovação padrão: 70% de acertos (configurável por avaliação).
        
2. **Mini projetos (avaliação prática)**
    
    - Simulam casos reais (criação de APIs, landing pages, scripts, etc.).
        
    - Os usuários submetem soluções práticas; tanto o enunciado quanto as respostas podem ser avaliados pela comunidade.
        
    - O certificado para mini projeto é concedido somente após avaliação comunitária das entregas.

3. **Avaliações personalizadas**

    - São criadas por recrutadores a partir do banco de questões da plataforma.

    - Podem combinar questões de múltipla escolha, mini projetos e questões abertas.

    - Podem incorporar critérios e pesos definidos pelo recrutador, além de um snapshot da análise da vaga realizada por IA.
        

### 6.1.4 - Categoria das questões

Cada questão será categorizada por:

- **Nível de dificuldade:** Fácil / Intermediário / Avançado.
    
- **Área de conhecimento:** Desenvolvimento de software, Segurança da Informação, Banco de Dados, Redes e Infraestrutura, Inteligência Artificial, etc.
    
- **Relevância:** escala de 1 a 5.
    

Os usuários terão uma área para visualizar todas as questões submetidas, avaliar ou submeter novas questões.

### 6.1.5 - Validação das questões

- A comunidade atribui meta-dados às questões (dificuldade, área, relevância).
    
- Os votos dos recrutadores têm maior peso — pois o objetivo é alinhar a plataforma às necessidades do mercado de trabalho — mas a decisão final usa uma Rede Bayesiana que combina todos os votos para evitar viés excessivo.
    
- O peso dos votos dos recrutadores é atualizado por Algoritmos Genéticos periodicamente, com base em um conjunto de sinais: uso recente (avaliações criadas/usadas), média de avaliações, quantidade de questões aprovadas, razão de likes em comentários e o peso atual para estabilidade. Esses sinais ajudam a medir eficiência, qualidade e aderência à plataforma.

> **Observação:** a lógica real de decisão bayesiana de aprovação/reprovação está implementada até a camada `application`, mas, para fins demonstrativos, a infraestrutura ainda utiliza `MockEvaluateVotingResultUseCaseImpl`, acionada periodicamente pelo scheduler `QuestionVotingEvaluatorScheduler`.
    

### 6.1.6 - Certificação

- **Múltipla escolha:** certificado automático ao atingir o percentual mínimo (padrão: 70%).
    
- **Mini projetos:** certificado emitido após avaliação e validação pela comunidade.
    

### 6.1.7 - Sistema de Revisão e Feedback Colaborativo

- Todos os usuários podem fornecer feedback sobre questões e projetos.
    
- Conteúdos com feedback negativo podem ser revisados ou removidos.
    
- Histórico de avaliações e comentários podem ser mantidos para transparência e auditoria.
    

### 6.1.8 - Assistente de Revisão e Apoio com IA

A LLM já é utilizada em três fluxos principais da plataforma:

1. **Pré-análise de questões**

- Apoia a revisão de clareza, coerência e relevância das questões submetidas.

- Pode sugerir ajustes no conteúdo antes que ele siga para a curadoria colaborativa.

2. **Análise de descrição de vaga**

- Recebe a descrição textual de uma vaga informada pelo recrutador.

- Retorna uma estrutura com áreas de conhecimento sugeridas, hard skills, soft skills e pesos entre hard skills, soft skills e experiência.

- Esse resultado pode ser salvo como contexto de uma avaliação personalizada.

3. **Pré-análise explicável de tentativas**

- Permite que o recrutador solicite uma análise textual auxiliar de uma tentativa realizada em sua avaliação personalizada.

- A IA pode considerar questões de múltipla escolha e questões abertas, resumindo pontos fortes, pontos de atenção e uma justificativa textual.

- Questões de mini-projeto são explicitamente ignoradas nesse fluxo por enquanto, permanecendo dependentes de avaliação humana.

