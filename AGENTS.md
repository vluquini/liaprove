# Code Assistant Context

# 1 - Project Overview

This collaborative assessment platform is an academic TCC project for IT professionals and recruiters to create, submit, and curate multiple-choice questions and mini-projects. Submissions are pre-screened by an LLM and then evaluated by the community through voting. A Bayesian Network combines questionVotes, weighing recruiters' questionVotes more heavily, to decide which questions enter the exam pool, and Genetic Algorithms periodically adjust recruiter questionVote weights. Users take assessments (MCQs or mini-projects) and earn certificates (for example, >=70% correct on a quiz).

Important project constraints and context:
- This is an academic final-year project (TCC). You must prefer pragmatic, testable solutions over overengineered designs. When you propose complex alternatives, you must also provide a simpler option and explicit trade-offs.
- The project uses Java 21, Maven, MapStruct and Spring Boot in the infrastructure layer, and PostgreSQL. You use MapStruct for mapping, Docker to containerize the PostgreSQL database, and you may also use a file-based H2 database for development and tests when that is convenient. Core and application logic must remain framework-agnostic.

# 2 - Layers responsibilities

- **Core**: contains domain entities, use case interfaces, exceptions and algorithm entities. Core domain subpackages are organized by subject area, such as `user`, `question`, `metrics`, `assessment`, and `algorithms`.
- **Application**: contains use case implementations and gateways.
- **Infrastructure**: contains Spring-specific code, REST controllers, DTOs, JPA entities, repositories, mappers, configs, security, schedulers and validation.

# 3 - Project structure

- `core/`
    - `algorithms/`   — Bayesian and Genetic Algorithm entities
    - `exceptions/`   — domain exceptions
    - `domain/`       — domain entities
    - `usecases/`     — use cases
- `application/`
    - `gateways/`     — gateways to be implemented in the infrastructure layer
    - `services/`     — core use case implementations
    - `dto/`          — application DTOs, when needed
- `infrastructure/`
    - `configs/`      — Spring Beans and Security configs
    - `controllers/`  — REST controllers
    - `dtos/`         — DTOs
    - `entities/`     — JPA entities
    - `exceptions/`   — global exception handler
    - `mappers/`      — MapStruct mappers
    - `repositories/` — repositories
    - `security/`     — Spring Security classes
    - `services/`     — implementations of application-layer gateways
    - `tasks/`        — schedulers
    - `validation/`   — request validators

# 4 - Rules and dependency constraints

- Dependency direction must always point inward: `infrastructure -> application -> core`.
- You must not import `org.springframework.*` or `jakarta.persistence.*` in `core` or `application`.
- You must keep persistence annotations and repository implementations only under `infrastructure`.
- You must keep all JPA entities under `com.lia.liaprove.infrastructure.entities`.
- You must keep use case interfaces under `com.lia.liaprove.core.usecases`.
- You must keep application dependent only on abstractions, never on concrete infrastructure implementations.
- You must prefer simple, TCC-appropriate solutions; when you propose an advanced design, you must provide a minimal alternative and the trade-offs.

The project follows Clean Architecture and SOLID principles. You must keep business rules stable and framework-agnostic; outer layers implement technical concerns.

# 5 - Coding Standards & Practices (pragmatic for TCC)

- **Style & format:** follow common Java conventions (UpperCamelCase for classes, lowerCamelCase for fields/methods), UTF-8, spaces for indentation. Use one top-level class per file.
- **Principles:** apply KISS, DRY, YAGNI, and SOLID pragmatically. Favor simple, testable implementations; when you deviate, document the reason in the agent log.
- **Validation & security:** validate inputs at boundaries (DTOs), use `@Valid`, parameterized JPA queries, and avoid hard-coded secrets.
- **Implementation scope:** avoid speculative abstractions and unnecessary layers.

# 6 - Testing

You must generate tests only when the user explicitly requests them.

When the user requests tests, you must:
- Prefer JUnit 5 and Mockito for unit tests.
- Keep unit tests isolated from Spring context whenever the target is a pure unit.
- Cover every main and important scenario for the requested behavior, not only the minimum path.
- Include at least 2 tests, usually a happy path and one or more relevant edge cases or invalid inputs.
- Keep tests deterministic, readable, and focused on behavior.
- Use integration tests only when the requested behavior truly requires multiple layers working together.
- For regression fixes, add a test that reproduces the bug and prevents it from returning.

When you implement a feature, you must validate at minimum:
- architecture compliance,
- requested flow and behavior,
- the most important scenarios relevant to the request,
- regression coverage when applicable.
