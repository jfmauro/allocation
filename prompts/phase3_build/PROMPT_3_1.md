Read .opencode/plans/architecture-plan.md & .opencode/plans/task-dispatch-table.md

Execute the domain layer steps sequentially:
1. Scaffold the root parent POM and empty module POMs
2. Implement each domain entity using domain-engineer (TDD: test first)
3. Define all port interfaces (inbound use cases, outbound repositories)

After each step: dispatch spec-reviewer, then code-reviewer.
Fix any CHANGES_REQUESTED before proceeding to the next step.
After the last domain step: run mvn -q test -pl domain
Report results before continuing.