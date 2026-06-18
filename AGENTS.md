# ai-tivair — Project Rules

## Responses

- Keep responses concise and to the point unless the user asks otherwise.

## Planning mode

- Always ask clarifying questions before producing a plan.
- Never assume design, tech stack, or features.
- Use subagents (explore, scout) to research before planning.
- Review the plan with a subagent before presenting to the user.

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

- Hexagonal architecture (Ports & Adapters) is mandatory.
- Load the hexagonal-architecture skill for detailed rules.
- When the hexagonal-architecture skill is loaded, its rules take precedence over generic java-springboot advice for transaction placement, module boundaries, and dependency flow.NO
- Module structure: domain, application, adapter-in, adapter-out, bootstrap.
- Dependency flow is strictly unidirectional: domain has zero framework imports.
- Transactions managed per hexagonal rules: @Transactional adapter-out worker level, never in domain or application level.

## Code standards

- Constructor injection only; never field injection with @Autowired.
- Controllers are thin HTTP orchestrators only; zero business logic.
- Business logic belongs in domain objects and application services.
- SLF4J for logging; every public method logs at start and end.
- Every log message starts with +++ and ends with +++.
- All code, variable names, comments, and text in English.

## Locking

- Use @Version (optimistic locking) and @Lock(PESSIMISTIC_WRITE) (pessimistic locking).
- Choose the correct strategy per use case: pessimistic for high-contention writes, optimistic for low-contention updates.

## Testing

- Always use TDD: write the test first (RED), then the implementation (GREEN), then refactor.
- Use vertical slices: one test → one implementation → repeat. Never write all tests then all code.
- JUnit5 and Mockito for unit tests.
- @WebMvcTest for controller tests, @DataJpaTest for repository tests.
- Avoid @SpringBootTest unless testing full integration.
- Load the tdd skill for detailed methodology.

## Database schema changes

- Design production-ready schemas with proper indexes and constraints.
- Load the database-schema-designer skill for guidelines.

## UI design

- Follow the design system defined in DESIGN.md (loaded automatically via instructions).
- Load the frontend-design skill for aesthetic guidelines.

## Quality gates

- Every implementation step must pass two reviews before completion:
  1. spec-reviewer: conformity to functional requirements (confluence page ID : 7078052229)
  2. code-reviewer: security, performance, architecture, test adequacy
- No step is complete until both reviewers return APPROVED.

## Subagent team

The following specialist subagents are available in .opencode/agents/:
- domain-engineer: pure domain layer and application services
- persistence-engineer: adapter-out with JPA, locking, transactional workers
- web-engineer: adapter-in REST layer and bootstrap assembly
- test-engineer: concurrency tests and integration tests
- frontend-engineer: vanilla HTML/CSS/JS frontend
- spec-reviewer: read-only spec conformity review
- code-reviewer: read-only code quality review
