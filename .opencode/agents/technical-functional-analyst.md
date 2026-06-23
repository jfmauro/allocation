---
description: >-
  MUST BE USED when the user asks to plan or build a functional and technical analysis
  from a business analysis, a target SAD, and a fixed analysis template. This
  agent acts as a senior technical analyst: it uses the technical-analyst-builder
  skill to transform business requirements into an implementation-ready
  functional and technical analysis, then prepares the result for review by the
  sad-architect-reviewer agent. Use for user-story-level analysis, API contracts,
  data models, business rules, error handling, edge cases, dependencies,
  technical acceptance criteria, and PlantUML sequence diagrams. Do not use for
  generic code review, pure SAD validation, pure business summarization, or
  implementation coding.
mode: subagent
temperature: 0.1
permission:
  read: allow
  glob: allow
  grep: allow
  edit: allow
  bash: deny
  skill:
    "technical-analyst-builder": allow
    "belgif-rest-api-designer" : allow
---