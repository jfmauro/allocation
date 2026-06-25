# Task Dispatch Table - Payment Allocation to Debts

## Layer 1 (domain): scaffold + domain models + ports

| Step number | Task description | Target subagent | Dependencies | Can parallelize with |
|---|---|---|---|---|
| 1 | Scaffold multi-module Maven project (parent aggregator, module POMs, baseline package structure, CI test profiles). | domain-engineer | None | None |
| 2 | Implement core domain models with TDD (`Payment`, `Debt`, `Debtor`, `AllocationProposal`, `PaymentAllocation`) and state invariants/transitions. | domain-engineer | 1 | None |
| 3 | Implement value objects and matching rules with TDD (structured communication, NISS/BCE/VAT validators, name normalization/confidence). | domain-engineer | 2 | None |
| 4 | Define inbound and outbound ports (`port.in`, `port.out`) for use cases, repositories, gateways, and transactional workers. | domain-engineer | 2, 3 | None |

## Layer 2 (parallel): adapter-out and application services

| Step number | Task description | Target subagent | Dependencies | Can parallelize with |
|---|---|---|---|---|
| 5 | Implement persistence entities, repositories, MapStruct mappers, and DB constraints/indexes with optimistic-lock support. | persistence-engineer | 4 | 7 |
| 6 | Implement adapter-out transactional workers with locking strategy (`@Transactional`, `PESSIMISTIC_WRITE`, invariant rechecks, idempotency, audit writes). | persistence-engineer | 5 | 7 |
| 7 | Implement application service orchestration for intake, matching, proposal lifecycle, validation, and allocation (no transaction annotations). | domain-engineer | 4 | 5, 6 |

## Layer 3 (web): adapter-in + bootstrap

| Step number | Task description | Target subagent | Dependencies | Can parallelize with |
|---|---|---|---|---|
| 8 | Implement REST controllers and DTO mappers (thin controllers, Bean Validation, endpoint contract coverage). | web-engineer | 7 | None |
| 9 | Implement global exception handler (`@RestControllerAdvice`) and Problem Details style error mapping. | web-engineer | 8 | None |
| 10 | Implement bootstrap wiring/configuration (Spring Boot app, bean composition, H2 config, static resource routing). | web-engineer | 6, 7, 8, 9 | None |

## Layer 4 (frontend): static pages

| Step number | Task description | Target subagent | Dependencies | Can parallelize with |
|---|---|---|---|---|
| 11 | Implement frontend static pages (PipelinePro HTML/CSS/JS pages for intake, detail, proposals, validation, debts, allocation detail, audit access log). | frontend-engineer | 10 | None |

## Layer 5 (hardening): concurrency tests

| Step number | Task description | Target subagent | Dependencies | Can parallelize with |
|---|---|---|---|---|
| 12 | Implement concurrency hardening tests (multi-thread contention, no duplicate effective allocation, no negative remaining amounts, rollback safety). | test-engineer | 6, 10 | None |
