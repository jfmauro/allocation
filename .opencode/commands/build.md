---
description: Phase 3 - Build one hexagonal layer end-to-end with two-stage review gates. Supports greenfield, extension and refactor modes.
agent: orchestrator
---
Parameters:
- $1 = LAYER (mandatory). Allowed values: domain | adapter-out | application | adapter-in | bootstrap | frontend | hardening.
- $2 = MODE (optional). Allowed values: greenfield | extension | refactor. Default: greenfield.

Resolution rules:
- If $1 is empty, abort and ask the user which layer to execute.
- If $2 is empty, treat MODE as greenfield.

Mode: greenfield
- Read @.opencode/plans/architecture-plan.md and @.opencode/plans/task-dispatch-table.md.
- Execute the steps for layer $1 in dependency order, dispatching the correct specialist subagent per the dispatch table.

Mode: extension
- Read @.opencode/plans/extension-plan.md and @.opencode/plans/task-dispatch-table.md.
- Read the impacted source files first (read-only) to map the current state and identify integration points.
- Apply the feature-extension-methodology skill.
- For every implementation step, the specialist subagent must:
   - prefer creating new files over modifying existing files;
   - when modifying an existing file is necessary (wiring a new bean, registering a new endpoint, extending a configuration, adding a new column via migration), edit additively;
   - preserve every public contract listed under the preserved-contract section of the extension plan (APIs, persistence schema, observable behaviors);
   - keep all existing tests green;
   - apply TDD to all newly written code (RED -> GREEN -> refactor) using the tdd skill;
   - perform the smallest reviewable change.

Mode: refactor
- Read @.opencode/plans/refactor-plan.md and @.opencode/plans/task-dispatch-table.md.
- Read the impacted source files first (read-only).
- Apply the refactoring-methodology skill: characterization tests first, then small reviewable steps invoking named patterns.
- For every implementation step, the specialist subagent must:
   - preserve every public contract listed under the non-regression section of the refactor plan;
   - keep all existing tests green;
   - add or extend tests as prescribed by the plan;
   - perform the smallest reviewable change, never bundle multiple refactorings.

After each step (all modes):
1. Dispatch spec-reviewer.
   - CHANGES_REQUESTED -> send feedback to implementer, fix, re-review.
   - APPROVED -> proceed.
2. Dispatch code-reviewer.
   - CHANGES_REQUESTED -> send feedback to implementer, fix, re-review.
   - APPROVED -> step complete.

In extension mode, both reviewers must additionally verify that:
- the preserved contract is unchanged;
- pre-existing tests are still green;
- the change set is bounded to what the extension plan prescribes for that step.

In refactor mode, both reviewers must additionally verify that:
- the non-regression contract is preserved;
- no test that was green before the step is now red;
- the change set is bounded to what the refactor plan prescribes;
- the refactor produces a measurable quality improvement.

Parallelize independent steps when the dispatch table allows it.

After the last step of the layer, run:
!`mvn -q test -pl $1`

In extension and refactor modes, also run the full module test suite of every module touched (not only $1) to catch cross-module regressions:
!`mvn -q test`

Report: completed steps, both reviewer verdicts per step, mvn test output, what comes next.