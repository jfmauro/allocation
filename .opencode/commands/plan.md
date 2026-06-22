---
description: Phase 2 - Produce the architecture plan and the task dispatch table.
agent: sad-architect-reviewer
subtask: true
---
Read @.opencode/plans/technical-analysis.md and @DESIGN.md.

Step 1 - Produce .opencode/plans/architecture-plan.md with:
1. Maven multi-module structure (domain, application, adapter-in,
   adapter-out, bootstrap).
2. Data model as a PlantUML entity diagram.
3. REST API endpoint table (method, path, request/response, status codes).
4. Locking strategy table (operation, lock type, implementation, rationale).
5. Sequence diagrams (PlantUML) for main write flows showing locking.
6. Frontend page table (page name, file, content).
7. Numbered implementation steps with columns: # | Step | Module | What | Test.
   Order steps following hexagonal dependency flow: scaffold -> domain
   models (TDD) -> ports -> persistence -> application services -> REST
   controllers -> exception handler -> bootstrap config -> frontend ->
   concurrency tests.

Apply the spring-boot-hexagonal-architecture skill for module boundaries,
dependency flow, transaction placement, and outbound port design.

Step 2 - Hand off to the planner subagent to produce
.opencode/plans/task-dispatch-table.md with columns:
- Step number
- Task description
- Target subagent
- Dependencies
- Can parallelize with

Group rows by layer:
- Layer 1 (domain): scaffold + domain models + ports.
- Layer 2 (parallel): adapter-out and application services.
- Layer 3 (web): adapter-in + bootstrap.
- Layer 4 (frontend): static pages.
- Layer 5 (hardening): concurrency tests.

Step 3 - Present the two artifacts to the user for explicit approval
before /build is allowed.