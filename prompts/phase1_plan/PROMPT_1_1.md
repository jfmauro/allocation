Read (confluence page ID : 7078052229) (implement only ## US-001 — Receive an Incoming Bank Payment and US-002 — Match Payment by Belgian Structured Communication)
and DESIGN.md.

Plan the full architecture for this project with:
- Spring Boot 4.x, Java 21, H2 local database, MapStruct, Lombok
- Vanilla HTML, CSS, and JavaScript for the frontend
- Hexagonal Architecture (Ports & Adapters) with strict layer separation
- Optimistic locking for low-contention operations, pessimistic locking
  for high-contention writes
- TDD with JUnit5 and Mockito (vertical slices, not horizontal)
- REST API V1 with proper HTTP status codes
- Spring AI with Groq scaffolded but unused in V1

Your plan must include:
1. Maven multi-module structure (domain, application, adapter-in, adapter-out, bootstrap)
2. Data model as a PlantUML entity diagram
3. REST API endpoint table (method, path, description, status codes)
4. Locking strategy table (operation, lock type, implementation, rationale)
5. Sequence diagrams (PlantUML) for the main write flows showing locking
6. Frontend page table (page name, file, content)
7. A numbered implementation steps table with columns:
   # | Step | Module | What | Test
   Order the steps following hexagonal dependency flow:
   scaffold → domain models (TDD) → ports → persistence (adapter-out) →
   application services → REST controllers (adapter-in) → exception handler →
   bootstrap config → frontend → concurrency integration tests