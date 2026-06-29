---
description: Phase 1 - Produce a structured technical analysis from knowledge/. Supports greenfield, extension-business, refactor-business and refactor-technical modes. SAD review is optional.
agent: technical-functional-analyst
subtask: true
---
Parameters:
- $1 = MODE (optional). Allowed values: greenfield | extension-business | refactor-business | refactor-technical. Default: greenfield.
- $2 = SAD_CHECK (optional). Allowed values: with-sad | no-sad. Default: with-sad.

Resolution rules:
- If $1 is empty, treat MODE as greenfield.
- If $2 is empty, treat SAD_CHECK as with-sad.
- If SAD_CHECK is with-sad but no SAD file is present under knowledge/ (no filename containing "sad" case-insensitive, in any subdirectory), downgrade to no-sad and warn the user explicitly.

Knowledge directory convention:
- knowledge/baseline/ holds the historical context (already covered by past cycles).
- knowledge/inbox/ holds the new scope to process during the current cycle.
- Files placed at the root of knowledge/ (outside both subdirectories) are treated as baseline by default with a backward-compatibility warning.

Artifact archival rule:
- Before writing any analysis file under .opencode/plans/, if the target filename already exists, move the existing file to .opencode/plans/archive/<YYYYMMDD>T<HHMMSS>-<filename>. Create the archive directory if missing. This rule applies to every mode in this command.

Mode: greenfield
- Read every file under @knowledge/ (baseline/, inbox/, and root for backward compatibility).
- Apply the archival rule on .opencode/plans/technical-analysis.md if it exists.
- Produce .opencode/plans/technical-analysis.md.
- Use the technical-analyst-builder skill to drive the analysis.
- Cite source filenames inline.
- After saving, summarize the open questions back to the user.

Mode: extension-business
- The project already exists. New business analyses have been deposited under @knowledge/inbox/ (new epic, new feature, or new user stories).
- Read every file under @knowledge/baseline/ as historical context.
- Read every file under @knowledge/inbox/ as the authoritative new scope.
- Read .opencode/plans/technical-analysis.md if present, as the historical baseline (the business scope already covered).
- Read .opencode/plans/architecture-plan.md if present, as the architectural baseline.
- Read the existing codebase (read-only) to identify integration points and preserved contracts.
- Use the technical-analyst-builder skill in extension mode.
- Apply the archival rule on .opencode/plans/extension-analysis.md if it exists.
- Produce .opencode/plans/extension-analysis.md following the template at @.opencode/templates/extension-analysis.md, containing:
  - the new epic, features, and user stories described per the technical-analyst-builder template;
  - explicit integration points with the existing codebase (modules, classes, endpoints, tables that must be touched for wiring);
  - the preserved contract: APIs, schema entries, and observable behaviors that must remain unchanged;
  - migration considerations (data, API versioning) if any;
  - open questions.
- Do not overwrite .opencode/plans/technical-analysis.md.
- Do not modify the existing codebase.
- Abort and ask the user if @knowledge/inbox/ is empty.

Mode: refactor-business
- Read every file under @knowledge/baseline/ as historical context.
- Read every file under @knowledge/inbox/ as the authoritative new scope, including the change request (typically inbox/change-request.md).
- Read the existing codebase (read-only).
- Read .opencode/plans/architecture-plan.md if present.
- Read .opencode/plans/technical-analysis.md if present.
- Use the technical-analyst-builder skill in refactor mode and the refactoring-methodology skill.
- Apply the archival rule on .opencode/plans/refactor-analysis.md if it exists.
- Produce .opencode/plans/refactor-analysis.md following the template at @.opencode/templates/refactor-analysis.md.
- Cite source filenames inline.
- Do not overwrite .opencode/plans/technical-analysis.md.
- Abort and ask the user if @knowledge/inbox/ is empty.

Mode: refactor-technical
- No business change request expected. @knowledge/inbox/ may be empty.
- Read every file under @knowledge/baseline/ as historical context.
- Read the existing codebase (read-only).
- Read .opencode/plans/architecture-plan.md if present.
- Read .opencode/plans/technical-analysis.md if present.
- Use the refactoring-methodology skill.
- Apply the archival rule on .opencode/plans/refactor-analysis.md if it exists.
- Produce .opencode/plans/refactor-analysis.md following the template at @.opencode/templates/refactor-analysis.md.
- Do not propose business changes.

After the analysis file is saved:

If SAD_CHECK = with-sad:
- Invoke sad-architect-reviewer to review the analysis using the technical-analysis-sad-review skill.
- Reviewer must return APPROVED or CHANGES_REQUESTED before /plan is allowed.

If SAD_CHECK = no-sad:
- Invoke sad-architect-reviewer in no-sad best-effort mode.
- Write a clear warning at the top of the produced analysis file: "WARNING: produced without SAD validation. Findings are best-effort against generic architecture standards."
- Inform the user that the analysis is conditionally usable and recommend producing a SAD when budget allows.

In all modes, summarize the open questions back to the user after saving, and indicate whether the analyst suggests confirming or changing the mode.