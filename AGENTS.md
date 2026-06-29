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
- Vanilla HTML, CSS, and JavaScript for the frontend (optional — only when the project includes a frontend module; skip if the project is pure backend).
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
- Apply only when the project includes a frontend module.

## Quality gates

- Every implementation step must pass two reviews before completion:
  1. `spec-reviewer`: conformity to the requirements in `knowledge/`.
  2. `code-reviewer`: security, performance, architecture, test adequacy.
- No step is complete until both reviewers return APPROVED.

## Modes, refactoring, extension, and No-SAD

The framework supports four modes built on top of the same five-command pipeline, with the SAD review gate that can be enabled or disabled per execution.

### Modes

- `greenfield` (default): build a system from `knowledge/`. No existing code expected.
- `extension-business`: an existing project receives a new business analysis under `knowledge/inbox/`; the framework adds functionality while preserving existing contracts.
- `refactor-business`: a change request modifies existing behavior; the framework rewrites the impacted code while preserving non-regression contracts.
- `refactor-technical`: technical-debt remediation only; no business change is allowed.

### SAD check

- `with-sad` (default): the SAD review gate is mandatory; the SAD lives under `knowledge/`.
- `no-sad`: the SAD review gate is skipped; reviewers operate in best-effort mode against generic architecture standards. A warning is inscribed in every artifact produced under this mode.

### Command parameters

| Command | `$1` | `$2` |
|---|---|---|
| `/analyse` | mode | sad-check |
| `/plan` | mode | sad-check |
| `/build` | layer | mode (greenfield / extension / refactor) |
| `/document` | mode (greenfield / extension / refactor) | — |
| `/release` | mode (greenfield / extension / refactor) | sad-check |

Defaults reproduce the legacy greenfield-with-SAD behavior.

## Knowledge layout and artifact archival

The framework enforces a deterministic directory layout for inputs and a deterministic archival rule for outputs. These conventions are mandatory for every agent that reads `knowledge/` or writes to `.opencode/plans/` or `docs/`.

### knowledge/ layout

```
knowledge/
├── README.md
├── baseline/    historical context, already covered by past cycles
└── inbox/       new scope to process during the current cycle
```

- Files in `baseline/` are read as historical reference context; they are NOT a new scope to implement.
- Files in `inbox/` are read as the authoritative new scope for `extension-business` and `refactor-business`.
- For `refactor-technical`, `inbox/` is typically empty (the codebase is the input).
- Files placed at the root of `knowledge/` (outside both subdirectories) remain supported for backward compatibility and are treated as baseline; a warning recommends migration to the proper subdirectory.
- The SAD file (any filename containing "sad" case-insensitive) is searched in both `baseline/` and `inbox/`.

### Artifact archival rule

Before writing any file under `.opencode/plans/` or `docs/` (excluding `docs/adr/`), if the target filename already exists, the agent moves the existing file to an archive subdirectory with a timestamp prefix:

- plans → `.opencode/plans/archive/<YYYYMMDD>T<HHMMSS>-<filename>`
- docs → `docs/archive/<YYYYMMDD>T<HHMMSS>-<filename>`

This rule applies to:

- `.opencode/plans/technical-analysis.md`
- `.opencode/plans/architecture-plan.md`
- `.opencode/plans/extension-analysis.md`
- `.opencode/plans/extension-plan.md`
- `.opencode/plans/refactor-analysis.md`
- `.opencode/plans/refactor-plan.md`
- `.opencode/plans/release-report.md`
- `docs/README.md`
- `docs/architecture.md`
- `docs/api.md`

This rule does NOT apply to:

- `.opencode/plans/task-dispatch-table.md` (regenerable from the active plan; always overwritten).
- `docs/adr/NNNN-title.md` (versioned by numeric prefix; always additive; never overwritten).

Every agent that writes one of the listed files MUST:

1. check existence of the target filename;
2. if it exists, move it to the archive directory with the timestamp prefix above (creating the archive directory if missing);
3. then write the new version at the canonical path.

Each agent reports in its deliverable the list of files moved during the run.

### Post-release housekeeping

After a successful `/release extension` or `/release refactor` (user-approved), the framework proposes to promote the contents of `knowledge/inbox/` into `knowledge/baseline/` to prepare the next cycle. The promotion is explicit and confirmed by the user; it is not automatic. On name collision in `baseline/`, the promoted file's name is suffixed with the current timestamp and the user is warned.

### Discipline reminders

When operating in extension mode:

- the preserved contract listed in `.opencode/plans/extension-plan.md` is binding for every implementer and reviewer;
- new functionality lands primarily as new files; existing files are touched only for wiring as prescribed by the plan;
- new code follows TDD via the `tdd` skill;
- pre-existing tests must remain green at every step;
- schema migrations are additive by default;
- the `feature-extension-methodology` skill is the methodological reference.

When operating in refactor mode:

- the non-regression contract listed in `.opencode/plans/refactor-plan.md` is binding;
- characterization tests precede every behavior-impacting refactoring step;
- each step applies a single named refactoring pattern;
- pre-existing tests must remain green at every step;
- the `refactoring-methodology` skill is the methodological reference.

When operating without a SAD:

- reviewers use generic hexagonal, SOLID, Spring Boot, and REST standards as the validation baseline;
- every assumption that would normally come from the SAD is flagged `Requires architect confirmation (no SAD available)`;
- the release report carries the warning `WARNING: released without SAD validation`.

### Artifacts per mode

| Mode | Analysis artifact | Plan artifact | Methodological skill |
|---|---|---|---|
| greenfield | `.opencode/plans/technical-analysis.md` | `.opencode/plans/architecture-plan.md` | (project domain skills) |
| extension-business | `.opencode/plans/extension-analysis.md` | `.opencode/plans/extension-plan.md` | `feature-extension-methodology` |
| refactor-business | `.opencode/plans/refactor-analysis.md` | `.opencode/plans/refactor-plan.md` | `refactoring-methodology` |
| refactor-technical | `.opencode/plans/refactor-analysis.md` | `.opencode/plans/refactor-plan.md` | `refactoring-methodology` |

The dispatch table `.opencode/plans/task-dispatch-table.md` is shared across modes and overwritten by each `/plan` invocation, restricted to the steps of the active mode.

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
- `frontend-engineer`: vanilla HTML/CSS/JS frontend (optional — invoke only when the project includes a frontend module; skip for pure-backend projects).
- `test-engineer`: concurrency tests and integration tests.
- `spec-reviewer`: read-only spec conformity review.
- `code-reviewer`: read-only code quality review.
- `documentation-engineer`: updates and produces project documentation.