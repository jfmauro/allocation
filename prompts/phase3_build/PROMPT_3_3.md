Domain layer is approved. Execute the application service steps using
domain-engineer:
- Implement each application service (TDD)
- Wire to domain ports
- Handle retry logic for optimistic lock exceptions

After each step: spec-reviewer then code-reviewer.
After the last step: mvn -q test -pl application