# Product Management & Stakeholder Management

Short reference. Expand with examples in interview.

---

## 1. Stakeholder management

**Approach:** Align early on problem and success criteria; make trade-offs explicit (scope vs time vs quality); use data or technical constraints to move from opinion to decision.

**Example — seat hold:** Product wanted “temporarily hold” seats (conversion); Ops wanted inventory accuracy; Engineering flagged complexity.  
**Actions:** Design session (Product + Ops + Eng); two options on the table (hard reservation with TTL vs soft hold + cleanup); documented risks, user impact, timelines.  
**Closure:** Hard reservation with TTL for phase 1; soft hold in backlog. Shipped on time; architecture stayed extensible.

**Principle:** Document trade-offs and ownership; avoid endless discussion.

## 2. Technology management

- **Goals:** Maintainable, observable, evolvable.
- **In practice:** Layered architecture (Controller → Service → Repository); clear domain boundaries; defensive design (concurrency, failures); consistent patterns (DTOs, global exception handling, idempotency).
- **Debt & quality:** Make debt visible; refactoring windows; shared standards; design review for non-trivial changes.
- **New tech:** Evaluate team familiarity, ops overhead, long-term cost. Prefer boring, proven tech unless clear business benefit.

## 3. Enabling the team & efficiencies

- **Engineering:** Templates, common error-handling/logging, reusable components (e.g. discount engine, adapters), PR guidelines, lightweight design docs.
- **Process:** Smaller PRs; early API contracts; short alignment sessions before big work.
- **Knowledge:** Walkthroughs, architecture diagrams, onboarding; feature ownership over silos.
- **Outcome:** Faster onboarding, fewer prod issues, better predictability.

## 4. Delivery planning & estimates

- **Mindset:** Estimates as planning tools, not promises.
- **Practice:** Break into small tasks (e.g. seat reservation, payment, discount engine, APIs, edge cases); tag uncertainty (low/medium/high); spikes for high-uncertainty; use ranges (e.g. 3–4 weeks MVP, 1–2 weeks payment); re-forecast when scope, dependencies, or tech surprises change.
- **Goal:** No surprises at the end.
