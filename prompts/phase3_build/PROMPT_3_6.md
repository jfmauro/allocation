All layers are assembled. Execute the test hardening step using
test-engineer:
- Concurrency integration test: N threads competing for limited capacity,
  proving the fundamental invariant holds (zero oversell)
- Optimistic lock retry test: concurrent modifications, verify exactly one
  succeeds per version
- Application smoke test: full context loads, beans wired, data initialized

After completion: spec-reviewer then code-reviewer.
Run: mvn -q test -pl bootstrap