---
description: Phase 1 - Produce a structured technical analysis from knowledge/.
agent: technical-functional-analyst
subtask: true
---
Read every file under @knowledge/ and produce a structured technical
analysis at .opencode/plans/technical-analysis.md.

Treat the directory as the authoritative input set. If multiple files
overlap, follow precedence rules in knowledge/README.md when present, or
ask the user.

Use the technical-analyst-builder skill to drive the analysis.

Cite source filenames inline. After saving, summarize the open questions
back to the user.

Once the technical analysis is saved, invoke sad-architect-reviewer to
review it using the technical-analysis-sad-review skill. Reviewer must
return APPROVED or CHANGES_REQUESTED before /plan is allowed.