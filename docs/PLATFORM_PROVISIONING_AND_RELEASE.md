# Platform Provisioning, Sizing & Release Requirements

Concise reference. Use for discussion in interview.

---

## 1. Technology choices & drivers

| Driver | Choice |
|--------|--------|
| Scale & reliability | Spring Boot, PostgreSQL, stateless app |
| Productivity & ops | REST, Maven, Docker; proven stack |
| API & partners | Versioned REST (/api/v1/); Springdoc OpenAPI |
| Future async | Kafka or cloud queues (payments, notifications) |

Prefer boring, proven tech unless there’s a clear business reason.

## 2. Database, transactions & data model

- **DB:** PostgreSQL; ACID, referential integrity; read replicas for reporting/catalog.
- **Transactions:** Booking + seats + discounts in one transaction; unique (show_id, seat_id); idempotency keys; optimistic locking on Show. Payment: two-phase (reserve → pay → confirm/release), no long DB hold on gateway.
- **Model:** Theatre → Screen → Seat; Movie → Show; Booking → SeatBooking. Clear boundaries; store gross/fees/net on Booking. i18n via translation table or JSON.

## 3. COTS / enterprise systems

| Area | Examples |
|------|----------|
| Payments | Stripe, Razorpay, PayPal |
| Identity | Auth0, Keycloak, Cognito |
| Monitoring | Datadog, Prometheus + Grafana |
| Logging | ELK or cloud-native |
| Notifications | Twilio, SendGrid, Firebase |
| Analytics | Segment, Mixpanel, GA |

Interfaces/adapters so providers can be swapped.

## 4. Hosting & sizing (cloud)

- **Where:** AWS / Azure / GCP; managed Kubernetes or containers; managed PostgreSQL; CDN; LB in front of stateless pods.
- **Sizing (example):** 2–3 app pods per region (auto-scale); PG primary + replica; Redis for cache/idempotency. Multi-region later with geo-routing and regional DBs.

## 5. Release & i18n

- **Release:** CI/CD; blue/green or rolling; feature flags; region-by-region capability.
- **i18n:** Locale in API (Accept-Language); region-specific pricing and catalog; currency/tax per country; config-driven rules (fees, commissions).

## 6. Monitoring & logging

- **Metrics:** Latency (p95/p99), error rate, booking success, payment failures, DB load; dashboards and alerts.
- **Logs:** Structured (JSON); correlation IDs; centralized (ELK or cloud). Fast root-cause and security review.

## 7. KPIs (examples)

| Category | Examples |
|----------|----------|
| Product | Conversion, payment success, seat utilization, checkout drop-off |
| Engineering | Availability, MTTR, deploy frequency, incident count |
| Ops | Partner sync success, inventory accuracy, support volume |

Reviewed regularly; drive prioritization and SLOs.

## 8. Project plan & estimates (high-level)

| Phase | Scope | Duration (example) |
|-------|--------|--------------------|
| 1 – Core | Browse → book, seat reservation, APIs, basic admin | 4–6 weeks |
| 2 – Payments | Gateway, webhooks, confirm, refunds | 2–3 weeks |
| 3 – Partners & i18n | Theatre onboarding, localization, regional catalog | 2–3 weeks |
| 4 – Hardening | Monitoring, alerts, load test, security review | 1–2 weeks |

Estimates as ranges; re-forecast as scope and uncertainty change.
