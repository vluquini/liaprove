
# Plataforma Colaborativa de Avaliação Técnica orientada ao Mercado de Trabalho com uso de Inteligência Artificial

> **Nota:** Este é um projeto de TCC que ainda está em sua fase inicial de desenvolvimento.

## 1 - Descrição Geral

Plataforma colaborativa onde usuários profissionais de TI e recrutadores podem submeter questões e mini projetos para compor avaliações técnicas alinhadas a contextos reais do mercado de trabalho. As contribuições passam por curadoria da comunidade e apoiam tanto a autoavaliação dos participantes quanto a criação de avaliações personalizadas por recrutadores. Ao finalizar uma avaliação de múltipla escolha e obter pelo menos 70% de acertos, o usuário recebe um certificado de comprovação de conhecimento.

## 2 - Detalhamento da Proposta

### 2.1 - Descrição

A plataforma permite que profissionais de TI e recrutadores criem e submetam questões e mini projetos para validar conhecimentos técnicos em áreas relevantes para o mercado de trabalho. As submissões passam por pré-avaliação feita por uma LLM (por exemplo, ChatGPT via API) e, depois, por votação da comunidade; cada usuário pode atribuir notas a cada questão.

Após a submissão e a votação, uma **Rede Bayesiana** apura os votos e decide se a questão será incorporada às avaliações da plataforma. **Algoritmos Genéticos** monitoram e ajustam periodicamente o peso dos votos dos recrutadores, aumentando ou diminuindo conforme sinais de uso e qualidade. Hoje, o ajuste considera: uso recente (avaliações criadas/usadas no período), média das avaliações (rating médio das assessments), quantidade de questões aprovadas, razão de likes em comentários (likes/(likes+dislikes)) e o peso atual para estabilidade. A Rede Bayesiana também auxilia recrutadores ao sugerir questões frequentemente escolhidas por outros recrutadores no momento da criação de avaliações personalizadas.

No contexto de recrutadores, a plataforma atua como ferramenta de apoio à avaliação técnica. Ela ajuda a estruturar critérios, selecionar questões e interpretar resultados, mas a decisão final de aprovar ou reprovar um candidato em um processo seletivo permanece humana e externa à regra automática da plataforma.

### 2.1.1 - Visão do Usuário Profissional

Um usuário do tipo profissional pode:

- Realizar avaliações (múltipla escolha e mini projetos).
    
- Submeter questões que julgar relevantes (essas só passam a integrar as avaliações após aprovação).
    
- Avaliar as questões enviadas por outros usuários, participando da curadoria colaborativa do acervo de questões.
    

### 2.1.2 - Visão do Usuário Recrutador

O recrutador possui as mesmas funcionalidades do profissional e, adicionalmente:

- Pode criar avaliações personalizadas (selecionar questões e definir o percentual de aprovação).
    
- Tem votos com peso maior na avaliação das questões (peso ajustável por Algoritmos Genéticos).
    
- Recebe sugestões inteligentes de questões e critérios de avaliação com base no contexto da vaga e em padrões de escolha de outros recrutadores.
    

### 2.1.3 - Tipos de Avaliação

A plataforma oferecerá dois tipos de avaliação:

1. **Prova de múltipla escolha**
    
    - Cada questão terá múltiplas alternativas (A–E ou conforme configuração).
        
    - Duração prevista: entre 30 minutos e 1 hora, dependendo do nível de dificuldade.
        
    - Aprovação padrão: 70% de acertos (configurável por avaliação).
        
2. **Mini projetos (avaliação prática)**
    
    - Simulam casos reais (criação de APIs, landing pages, scripts, etc.).
        
    - Os usuários submetem soluções práticas; tanto o enunciado quanto as respostas podem ser avaliados pela comunidade.
        
    - O certificado para mini projeto é concedido somente após avaliação comunitária das entregas.
        

### 2.1.4 - Categoria das questões

Cada questão será categorizada por:

- **Nível de dificuldade:** Fácil / Intermediário / Avançado.
    
- **Área de conhecimento:** Desenvolvimento de software, Segurança da Informação, Banco de Dados, Redes e Infraestrutura, Inteligência Artificial, etc.
    
- **Relevância:** escala de 1 a 5.
    

Os usuários terão uma área para visualizar todas as questões submetidas, avaliar ou submeter novas questões.

### 2.1.5 - Validação das questões

- A comunidade atribui meta-dados às questões (dificuldade, área, relevância).
    
- Os votos dos recrutadores têm maior peso — pois o objetivo é alinhar a plataforma às necessidades do mercado de trabalho — mas a decisão final usa uma Rede Bayesiana que combina todos os votos para evitar viés excessivo.
    
- O peso dos votos dos recrutadores é atualizado por Algoritmos Genéticos periodicamente, com base em um conjunto de sinais: uso recente (avaliações criadas/usadas), média de avaliações, quantidade de questões aprovadas, razão de likes em comentários e o peso atual para estabilidade. Esses sinais ajudam a medir eficiência, qualidade e aderência à plataforma.
    

### 2.1.6 - Certificação

- **Múltipla escolha:** certificado automático ao atingir o percentual mínimo (padrão: 70%).
    
- **Mini projetos:** certificado emitido após avaliação e validação pela comunidade.
    

### 2.1.7 - Sistema de Revisão e Feedback Colaborativo

- Todos os usuários podem fornecer feedback sobre questões e projetos.
    
- Conteúdos com feedback negativo podem ser revisados ou removidos.
    
- Histórico de avaliações e comentários podem ser mantidos para transparência e auditoria.
    

### 2.1.8 - Assistente de Revisão e Qualidade com IA

A LLM será usada para uma pré-análise das questões, com as seguintes funções sugeridas:

a) **Revisão de linguagem e clareza**

- Verifica gramática, coerência e clareza; sugere reformulações para evitar ambiguidades.
    

b) **Detecção de viés ou ambiguidade**

- Identifica itens tendenciosos ou ambíguos e propõe ajustes para neutralidade.
    

c) **Sugestão de alternativas (distratores)**

- Gera alternativas plausíveis para aumentar a qualidade das questões.
    

d) **Análise de complexidade de texto**

- Estima o nível de dificuldade a partir da complexidade lexical e sintática do enunciado.
    

e) **Avaliação de consistência entre tópicos**

- Detecta desalinhamentos de escopo, dificuldade ou relevância em conjuntos de questões sobre o mesmo tema.

