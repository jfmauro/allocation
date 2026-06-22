---
description: Phase 3 - Build one hexagonal layer end-to-end with two-stage review gates.
agent: orchestrator
---
Read @.opencode/plans/architecture-plan.md and
@.opencode/plans/task-dispatch-table.md.

Layer to execute: $1
(valid values: domain | adapter-out | application | adapter-in |
bootstrap | frontend | hardening)

Execute the steps for that layer in dependency order, dispatching the
correct specialist subagent per the dispatch table.

After each step:
1. Dispatch spec-reviewer.
    - CHANGES_REQUESTED -> send feedback to implementer, fix, re-review.
    - APPROVED -> proceed.
2. Dispatch code-reviewer.
    - CHANGES_REQUESTED -> send feedback to implementer, fix, re-review.
    - APPROVED -> step complete.

Parallelize independent steps when the dispatch table allows it.

After the last step of the layer, run:
!`mvn -q test -pl $1`

Report: completed steps, both reviewer verdicts per step, mvn test output,
what comes next.