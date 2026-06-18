Read .opencode/plans/architecture-plan.md.

Decompose the implementation steps into a task dispatch table with columns:
- Step number
- Task description
- Target subagent (domain-engineer, persistence-engineer, web-engineer,
  frontend-engineer, test-engineer)
- Dependencies (which steps must be done first)
- Can parallelize with (which other steps can run concurrently)

Group the steps by hexagonal layer:
- Layer 1 (domain): scaffold + domain models + ports
- Layer 2 (parallel): adapter-out (persistence-engineer) AND application
  services (domain-engineer)
- Layer 3 (web): adapter-in + bootstrap (web-engineer)
- Layer 4 (frontend): static pages (frontend-engineer)
- Layer 5 (hardening): concurrency tests (test-engineer)

Confirm this decomposition. I will approve it before we start building.