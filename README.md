
# Plataforma Colaborativa de Avaliação Técnica orientada ao Mercado de Trabalho com uso de Inteligência Artificial

> **Nota:** Este é um projeto de TCC que ainda está em sua fase inicial de desenvolvimento.

## 1 - Descrição Geral

Plataforma colaborativa onde usuários profissionais de TI e recrutadores podem submeter questões e mini projetos para compor avaliações técnicas alinhadas a contextos reais do mercado de trabalho. As contribuições passam por curadoria da comunidade e apoiam tanto a autoavaliação dos participantes quanto a criação de avaliações personalizadas por recrutadores. A plataforma também utiliza Inteligência Artificial para apoiar a pré-análise de questões, a estruturação de descrições de vaga e a interpretação de tentativas em avaliações personalizadas. Ao finalizar uma avaliação de múltipla escolha e obter pelo menos 70% de acertos, o usuário recebe um certificado de comprovação de conhecimento.

## 2 - Detalhamento da Proposta

### 2.1 - Descrição

A plataforma permite que profissionais de TI e recrutadores criem e submetam questões e mini projetos para validar conhecimentos técnicos em áreas relevantes para o mercado de trabalho. As submissões passam por pré-análise feita por uma LLM e, depois, por votação da comunidade; cada usuário pode atribuir notas a cada questão.

Após a submissão e a votação, uma **Rede Bayesiana** apura os votos e decide se a questão será incorporada às avaliações da plataforma. **Algoritmos Genéticos** monitoram e ajustam periodicamente o peso dos votos dos recrutadores, aumentando ou diminuindo conforme sinais de uso e qualidade. Hoje, o ajuste considera: uso recente (avaliações criadas/usadas no período), média das avaliações (rating médio das assessments), quantidade de questões aprovadas, razão de likes em comentários (likes/(likes+dislikes)) e o peso atual para estabilidade. A Rede Bayesiana também auxilia recrutadores ao sugerir questões frequentemente escolhidas por outros recrutadores no momento da criação de avaliações personalizadas.

No contexto de recrutadores, a plataforma atua como ferramenta de apoio à avaliação técnica. Ela ajuda a estruturar critérios a partir de descrições de vaga, sugerir pesos entre hard skills, soft skills e experiência, selecionar questões e interpretar tentativas com apoio de IA. A decisão final de aprovar ou reprovar um candidato em um processo seletivo permanece humana e externa à regra automática da plataforma.

### 2.1.1 - Visão do Usuário Profissional

Um usuário do tipo profissional pode:

- Realizar avaliações (múltipla escolha e mini projetos).
    
- Submeter questões que julgar relevantes (essas só passam a integrar as avaliações após aprovação).
    
- Avaliar as questões enviadas por outros usuários, participando da curadoria colaborativa do acervo de questões.
    

### 2.1.2 - Visão do Usuário Recrutador

O recrutador possui as mesmas funcionalidades do profissional e, adicionalmente:

- Pode criar avaliações personalizadas (selecionar questões e definir o percentual de aprovação).

- Pode criar questões abertas para uso em avaliações personalizadas, com visibilidade privada ou compartilhada entre recrutadores.
    
- Tem votos com peso maior na avaliação das questões (peso ajustável por Algoritmos Genéticos).
    
- Pode solicitar à IA uma análise estruturada da descrição de uma vaga, obtendo áreas de conhecimento, hard skills, soft skills e sugestão de pesos para apoiar a montagem da avaliação.

- Pode solicitar uma pré-análise explicável das tentativas realizadas em suas avaliações personalizadas, como apoio à interpretação técnica do desempenho do candidato.

- Recebe sugestões inteligentes de questões e critérios de avaliação com base no contexto da vaga e em padrões de escolha de outros recrutadores.
    

### 2.1.3 - Tipos de Avaliação

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
    

### 2.1.8 - Assistente de Revisão e Apoio com IA

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

