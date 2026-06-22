---
description: Phase 8 - Final consolidated release validation.
agent: orchestrator
---
Final validation. Produce .opencode/plans/release-report.md.

Step 1 - Run full test suite:
!`mvn -q verify`

Step 2 - Dispatch sad-architect-reviewer to review the assembled system
against @.opencode/plans/architecture-plan.md. Returns APPROVED or
CHANGES_REQUESTED with a list of architectural deviations.

Step 3 - Dispatch spec-reviewer once more against the whole codebase
versus @knowledge/.

Step 4 - Assemble the release report:
- Layer-by-layer completion status.
- Final mvn verify summary.
- Architect verdict.
- Spec-reviewer verdict.
- Open issues.
- Recommended next steps.

Step 5 - Present the report to the user for explicit release approval.