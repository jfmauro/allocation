# Project Rules

## Responses

- Keep responses concise and to the point unless the user asks otherwise.

## Knowledge inputs

- All functional and contextual requirements live under `knowledge/`.
- The framework is input-agnostic: any file dropped in `knowledge/` (Markdown, text, exported Confluence page, transcript, etc.) is treated as authoritative input.
- Agents and commands MUST reference `knowledge/**` rather than hardcoded filenames or page IDs.
- `knowledge/README.md` lists the inputs and any precedence rules when they overlap.

## Planning mode

- Always ask clarifying questions before producing a plan.
- Never assume design, tech stack, or features beyond what is stated in `knowledge/` and `DESIGN.md`.
- Use subagents (explore, scout) to research before planning.
- Review the plan with the `sad-architect-reviewer` agent before presenting to the user.

## Build mode

- Never implement features yourself when possible — delegate to specialist subagents.
- Identify parallelizable tasks and dispatch subagents concurrently.
- Act as a coordinator: delegate, review, integrate.
- After completing each feature or module, run: mvn -q test -pl <module>

## Tech stack

- Java 21, Spring Boot 4.x (LTS).
- Maven multi-module project.
- H2 for local/demo database.
- Lombok and MapStruct are expected where useful.
- Vanilla HTML, CSS, and JavaScript for the frontend.
- Spring AI with Groq scaffolded for future AI features.

## Technical architecture

- Hexagonal architecture (Ports and Adapters) is mandatory.
- Load the `spring-boot-hexagonal-architecture` skill for detailed rules.
- When the `spring-boot-hexagonal-architecture` skill is loaded, its rules take precedence over generic `java-springboot` advice for transaction placement, module boundaries, and dependency flow.
- Module structure: domain, application, adapter-in, adapter-out, bootstrap.
- Dependency flow is strictly unidirectional: domain has zero framework imports.
- Transactions are managed at adapter-out worker level (@Transactional), never in domain or application.

## Code standards

- Constructor injection only; never field injection with @Autowired.
- Controllers are thin HTTP orchestrators only; zero business logic.
- Business logic belongs in domain objects and application services.
- SLF4J for logging; every public method logs at start and end.
- Every log message starts with `+++` and ends with `+++`.
- All code, variable names, comments, and text in English.

## Locking

- Use @Version (optimistic locking) and @Lock(PESSIMISTIC_WRITE) (pessimistic locking).
- Choose per use case: pessimistic for high-contention writes, optimistic for low-contention updates.

## Testing

- Always use TDD: write the test first (RED), then the implementation (GREEN), then refactor.
- Use vertical slices: one test, one implementation, repeat. Never write all tests then all code.
- JUnit5 and Mockito for unit tests.
- @WebMvcTest for controller tests, @DataJpaTest for repository tests.
- Avoid @SpringBootTest unless testing full integration.
- Load the `tdd` skill for detailed methodology.

## Database schema changes

- Design production-ready schemas with proper indexes and constraints.
- Load the `database-schema-designer` skill for guidelines.

## UI design

- Follow the design system defined in `DESIGN.md` (loaded automatically via instructions).
- Load the `frontend-design` skill for aesthetic guidelines.

## Quality gates

- Every implementation step must pass two reviews before completion:
  1. `spec-reviewer`: conformity to the requirements in `knowledge/`.
  2. `code-reviewer`: security, performance, architecture, test adequacy.
- No step is complete until both reviewers return APPROVED.

## Agent and subagent team

Available in `.opencode/agents/`:

Primary:
- `orchestrator`: coordinates implementation, dispatches engineers, enforces review gates.

Subagents:
- `technical-functional-analyst`: turns `knowledge/` inputs into a structured technical analysis (uses the `technical-analyst-builder` skill).
- `sad-architect-reviewer`: produces architecture decisions and reviews them against the analysis (uses the `technical-analysis-sad-review` skill).
- `planner`: decomposes the architecture plan into a dependency-aware task dispatch table.
- `domain-engineer`: pure domain layer and application services.
- `persistence-engineer`: adapter-out with JPA, locking, transactional workers.
- `web-engineer`: adapter-in REST layer and bootstrap assembly.
- `frontend-engineer`: vanilla HTML/CSS/JS frontend.
- `test-engineer`: concurrency tests and integration tests.
- `spec-reviewer`: read-only spec conformity review.
- `code-reviewer`: read-only code quality review.
- `documentation-engineer`: updates and produces project documentation.