# Flight Reservation System - High Concurrency Case Study

## Overview

This project is designed as a practical case study for software architects and backend engineers who want to explore and implement solutions for high-concurrency database access scenarios.

The business domain is intentionally simple: an airline seat reservation system.

Despite its simplicity, the system introduces many real-world challenges frequently encountered in large-scale distributed systems:

* High concurrent access
* Overselling prevention
* Strong consistency requirements
* Distributed transactions
* Optimistic locking
* Pessimistic locking
* Horizontal scalability
* Idempotency
* Eventual consistency
* Failure recovery
* Distributed coordination

The primary objective is to guarantee that a seat can never be sold more than once, regardless of the number of simultaneous reservation attempts.

---

# Business Context

An airline company sells seats for scheduled flights.

Customers can browse available flights and reserve seats.

During peak demand periods, thousands of users may attempt to reserve the last remaining seats of the same flight simultaneously.

Examples include:

* Promotional campaigns
* Holiday periods
* Black Friday sales
* Special events
* Major international conferences

The system must ensure that inventory remains consistent under all circumstances.

---

# Core Business Rule

## Fundamental Invariant

The following business rule must always be respected:

> The total number of reserved seats for a flight must never exceed the aircraft capacity.

This invariant must remain true even under extreme concurrency conditions.

---

# Version 1 - Minimal Functional Requirements

## FR-001 - View Flight Information

A customer can view flight details.

### Displayed Information

* Flight number
* Origin
* Destination
* Flight date
* Aircraft capacity
* Available seats

### Example

| Flight | Route         | Capacity | Available Seats |
| ------ | ------------- | -------- | --------------- |
| AF123  | Paris → Tokyo | 300      | 12              |

---

## FR-002 - Reserve Seats

A customer can request a reservation for a given flight.

### Request

```text
Reserve N seats on flight AF123
```

### Constraints

```text
N >= 1
```

```text
N <= available seats
```

### Success Response

```text
Reservation confirmed
```

### Failure Response

```text
Reservation rejected
```

---

## FR-003 - Cancel Reservation

A customer can cancel an existing reservation.

### Expected Behavior

* Reserved seats become available again.
* Inventory is updated immediately.
* Capacity constraints remain respected.

---

# Minimal Data Model

## Flight

```java
Flight
------
id
flightNumber
departureAirport
arrivalAirport
flightDate
capacity
availableSeats
```

### Description

Represents a scheduled flight.

---

## Reservation

```java
Reservation
-----------
id
flightId
customerId
numberOfSeats
status
createdAt
```

### Status Values

```java
CONFIRMED
CANCELLED
```

---

# First Concurrency Problem

## Initial State

```text
Flight AF123

Capacity: 300
Available Seats: 1
```

Two customers attempt to reserve simultaneously.

---

## Transaction A

Reads:

```text
availableSeats = 1
```

---

## Transaction B

Reads:

```text
availableSeats = 1
```

---

## Transaction A

Updates:

```text
availableSeats = 0
```

Reservation succeeds.

---

## Transaction B

Updates:

```text
availableSeats = -1
```

Reservation also succeeds.

---

## Result

```text
2 reservations
for 1 available seat
```

This situation is known as:

* Lost update
* Overselling
* Inventory corruption

The system is now inconsistent.

---

# Critical Consistency Requirement

## NFR-001

The system must never sell more seats than the flight capacity.

### Example

```text
Aircraft Capacity = 300

Maximum Reserved Seats = 300
```

Any operation violating this rule must fail.

---

# Version 2 - Temporary Seat Hold

To introduce more realistic concurrency scenarios, reservations become a multi-step process.

---

## FR-004 - Hold Seats Temporarily

A customer may temporarily hold seats before payment.

### Hold Duration

```text
5 minutes
```

### During Hold

The held seats:

* Are unavailable to other customers
* Cannot be purchased by anyone else
* Count against available inventory

---

## New Reservation States

```java
PENDING
CONFIRMED
EXPIRED
CANCELLED
```

---

## Example

### Initial State

```text
Available Seats = 10
```

### Customer A

Holds:

```text
8 seats
```

### Remaining Inventory

```text
2 seats
```

### After 5 Minutes

The hold expires.

### Result

```text
8 seats returned to inventory
```

Available seats become:

```text
10
```

---

# Additional Technical Challenges Introduced

The temporary hold feature introduces:

* Time-based workflows
* Scheduled jobs
* Reservation expiration
* Inventory restoration
* Concurrent updates
* Compensation logic

---

# Version 3 - Explicit Seat Selection

Customers now select specific seats rather than requesting a quantity.

---

## FR-005 - Choose Specific Seats

Example:

```text
12A
12B
12C
```

instead of:

```text
Any 3 seats
```

---

# Seat Entity

```java
Seat
----
id
flightId
seatNumber
status
```

---

## Seat Status

```java
AVAILABLE
HELD
BOOKED
```

---

# Concurrency Scenario

Two customers attempt to reserve:

```text
12A
```

at exactly the same time.

---

# Requirement

The system must guarantee:

```text
Only one customer wins.
```

The seat must never be assigned to multiple reservations.

---

# Version 4 - Extreme Load Scenario

The system must operate correctly under heavy contention.

---

## NFR-002 - High Concurrency Support

The system shall support:

```text
10,000 reservation attempts
```

on the same flight within:

```text
30 seconds
```

---

## Example Scenario

```text
Flight Capacity = 100 seats

Reservation Attempts = 10,000
```

Possible business situations:

* Flash sales
* Promotional campaigns
* Limited-time offers
* Holiday bookings

---

# Expected Outcome

Exactly:

```text
100 successful reservations
```

and

```text
9,900 failures
```

No overselling is permitted.

---

# Version 5 - Payment Integration

A reservation becomes final only after successful payment.

---

## FR-006 - Reservation Workflow

### Step 1

```text
HOLD
```

### Step 2

```text
PAYMENT
```

### Step 3

```text
BOOKED
```

---

# Failure Scenario A

Payment succeeds.

Immediately afterward:

```text
System crashes
```

before reservation confirmation.

---

## Questions

* Was payment accepted?
* Is the seat booked?
* Should the reservation be retried?

---

# Failure Scenario B

Reservation is marked:

```text
BOOKED
```

but payment is later rejected.

---

## Questions

* Should inventory be restored?
* Should a compensating transaction execute?
* Should the booking be cancelled?

---

# Advanced Architectural Topics Introduced

This phase introduces:

* Distributed transactions
* Event-driven architectures
* Saga pattern
* Outbox pattern
* Retry mechanisms
* Failure recovery
* Exactly-once processing challenges

---

# Non-Functional Requirements

## NFR-003 - Consistency

The system must guarantee inventory correctness at all times.

### Mandatory Rule

```text
Reserved Seats <= Aircraft Capacity
```

Always.

---

## NFR-004 - Performance

At least:

```text
95%
```

of reservation requests must complete within:

```text
200 ms
```

---

## NFR-005 - Availability

Target availability:

```text
99.99%
```

---

## NFR-006 - Horizontal Scalability

The system must support deployment across multiple application instances.

Example:

```text
reservation-service-1
reservation-service-2
reservation-service-3
reservation-service-4
...
```

All instances must maintain consistent inventory information.

---

# Suggested API Operations

## Flight APIs

```http
GET /flights
GET /flights/{id}
```

---

## Reservation APIs

```http
POST /reservations
GET /reservations/{id}
DELETE /reservations/{id}
```

---

## Seat APIs

```http
GET /flights/{id}/seats
POST /flights/{id}/seat-holds
```

---

# Suggested Test Scenarios

## Scenario 1

100 users compete for:

```text
10 seats
```

Expected:

```text
10 successes
90 failures
```

---

## Scenario 2

1,000 users reserve simultaneously.

Verify:

* No duplicate reservations
* No negative inventory
* No deadlocks

---

## Scenario 3

Reservation expiration occurs while a payment is in progress.

Verify:

* Correct final state
* No seat duplication
* No inventory corruption

---


# Flight Reservation System - High Concurrency Case Study (V1 → V6)

## Overview

This project is designed as a practical case study for software architects and backend engineers who want to explore and implement solutions for high-concurrency database access scenarios.

The business domain is intentionally simple: an airline seat reservation system.

Despite its simplicity, the system introduces many real-world challenges frequently encountered in large-scale distributed systems:

* High concurrent access
* Overselling prevention
* Strong consistency requirements
* Distributed transactions
* Optimistic locking
* Pessimistic locking
* Horizontal scalability
* Idempotency
* Eventual consistency
* Failure recovery
* Distributed coordination

The primary objective is to guarantee that a seat can never be sold more than once, regardless of the number of simultaneous reservation attempts.

---

# Business Context

An airline company sells seats for scheduled flights.

Customers can browse available flights and reserve seats.

During peak demand periods, thousands of users may attempt to reserve the last remaining seats of the same flight simultaneously.

Examples include:

* Promotional campaigns
* Holiday periods
* Black Friday sales
* Special events
* Major international conferences

The system must ensure that inventory remains consistent under all circumstances.

---

# Core Business Rule

> The total number of reserved seats for a flight must never exceed the aircraft capacity.

---

# Version 1 - Minimal Functional Requirements

## FR-001 - View Flight Information

* Flight number
* Origin
* Destination
* Flight date
* Capacity
* Available seats

## FR-002 - Reserve Seats

Request:
Reserve N seats

Constraints:
N >= 1 and N <= available seats

Responses:
Success / Failure

## FR-003 - Cancel Reservation

Reserved seats are restored immediately.

---

# Version 2 - Temporary Seat Hold

* Hold duration: 5 minutes
* New states: PENDING, CONFIRMED, EXPIRED, CANCELLED
* Inventory is temporarily reduced

---

# Version 3 - Seat Selection

Seats are explicitly selected:

* 12A, 12B, 12C

Seat states:

* AVAILABLE
* HELD
* BOOKED

Concurrency must prevent double booking.

---

# Version 4 - Extreme Load

* 10,000 concurrent requests
* 100 seats available

Expected outcome:
* 100 success
* 9,900 failure

No overselling allowed.

---

# Version 5 - Payment Integration

Workflow:

HOLD → PAYMENT → BOOKED

Failure scenarios include:

* payment success + crash
* booking confirmed + payment failure

Requires:

* saga pattern
* outbox pattern
* retries
* idempotency

---

# Version 6 - AI Waitlist Prediction

## Overview

When a flight is full, the system introduces AI-driven waitlist intelligence.

---

## Business Scenario

Available seats = 0

User requests:

Reserve 1 seat → Flight is full

But AI computes:

87% probability of seat availability within 24h

User can join waitlist.

---

## AI Waitlist Features

### FR-007 - AI Waitlist Enrollment

Users can join waitlist when flight is full.

### FR-008 - Seat Availability Prediction

Model inputs:

* cancellation history
* no-show rate
* user behavior
* time window

Output:

P(seat available within Δt)

---

## FR-009 - Intelligent Seat Allocation

When a seat is released:

1000 candidates → 1 winner

Scoring factors:

* seniority
* loyalty
* purchase probability
* expected revenue

Final score:

final_score =
w1 * seniority +
w2 * loyalty +
w3 * purchase_probability +
w4 * expected_revenue

---

## FR-010 - Real-Time Allocation

Steps:

1. seat released
2. recompute scores
3. select winner
4. notify user
5. time-limited response

---

## Constraints

* Exactly one winner per seat event
* explainability required
* < 100ms decision time
* no duplicate allocation

---

## Architectural Challenges

* real-time ML inference
* event streaming
* distributed decision making
* fairness enforcement
* idempotency under load

---

# Final System Objective

A single seat release becomes:

1 event → 1000 candidates → 1 winner → explained decision

---

# Architectural Experiments

This project provides an excellent platform for evaluating:

## Database Isolation Levels

```text
READ_COMMITTED
REPEATABLE_READ
SERIALIZABLE
```

---

## Locking Strategies

### Optimistic Locking

```java
@Version
```

### Pessimistic Locking

```sql
SELECT ... FOR UPDATE
```

---

## Distributed Synchronization

* Redis locks
* Distributed mutexes
* Leader election
* Inventory partitioning

---

## Messaging and Event Processing

* Kafka
* RabbitMQ
* Event-driven architecture
* Event sourcing

---

## Advanced Patterns

* CQRS
* Saga
* Outbox Pattern
* Inbox Pattern
* Retry Pattern
* Circuit Breaker
* Bulkhead
* Idempotency Keys

---

