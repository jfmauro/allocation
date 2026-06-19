---
description: >-
  Senior software architect specialized in Solution Architecture Documents
  (SAD). Drafts, improves, and reviews SAD content, and validates technical
  analyses against a single target SAD by using the technical-analysis-sad-review
  skill. Use when the user asks to review a technical analysis, assess SAD
  completeness, detect architectural gaps, validate implementation readiness,
  or produce professional SAD sections. Do not use for generic code review,
  pure business analysis, or validation without a target SAD.
mode: subagent
temperature: 0.1
permission:
  read: allow
  glob: allow
  grep: allow
  edit: allow
  bash: deny
  skill:
    "technical-analysis-sad-review": allow
---