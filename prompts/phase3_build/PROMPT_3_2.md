Domain layer is approved. Execute the adapter-out steps using
persistence-engineer:
- JPA entities with @Version and MapStruct mappers
- Spring Data repos and repository implementations
- Transactional worker(s) with pessimistic locking

After each step: spec-reviewer then code-reviewer.
After the last step: mvn -q test -pl adapter-out