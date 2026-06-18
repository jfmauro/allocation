Adapter-out and application services are approved.
Execute the adapter-in and bootstrap steps using web-engineer:
- REST controllers with DTOs and MapStruct mappers
- GlobalExceptionHandler
- Spring Boot application class, bean configuration, data initializer
- application.yml (H2, logging, Spring AI scaffold)

After each step: spec-reviewer then code-reviewer.
After the last step: mvn -q test -pl bootstrap