# Non-Functional Requirements — Design & Architecture

Short reference for the movie ticket booking platform. Expand in discussion as needed.

---

## 1. Transactions & core design

- **Booking:** Single transaction (create booking → reserve seats → apply discounts → confirm). Rollback on any failure or seat conflict.
- **Idempotency:** Client sends idempotency key; same key → return existing booking (no double charge/booking).
- **Seat locking:** DB unique constraint on (show_id, seat_id). First commit wins; concurrent attempt gets 409.
- **Optimistic locking:** `@Version` on Show for concurrent updates (price, cancellation).
- **Payment (future):** Split flow: reserve seats → call gateway → confirm or release. Saga/outbox for consistency.

## 2. Theatre integration & localization

- **Existing IT:** REST sync or pull; CSV for partners without API; webhooks for real-time updates. Adapter pattern: one interface, partner-specific adapters; map external IDs to internal.
- **No IT:** B2B portal for onboarding, screens, seats, shows; we are source of truth.
- **Localization:** Movie metadata (language, genre, region). Translation table or JSON for titles/descriptions. API: Accept-Language or locale param. Regional filtering for catalog and distribution rights.

## 3. Scale & 99.99% availability

- **Multi-region:** City/country in model; geo-routing and CDN; per-region catalog/pricing.
- **HA:** Stateless app + LB; PostgreSQL primary + replicas + failover; multi-AZ; timeouts/retries/circuit breakers for external deps; monitoring and runbooks; failover drills.

## 4. Payment gateway

- **Abstraction:** PaymentGateway interface (initiate, capture, refund, status); one impl per provider (Stripe, Razorpay, PayPal).
- **Flow:** Reserve + discount → initiate payment → webhook/poll → confirm booking.
- **Safeguards:** Idempotency on payment calls; no card storage (tokenization/hosted checkout); release seats after TTL on repeated failure (e.g. 10–15 min).

## 5. Monetization

- **B2C:** Convenience fee, margin on tickets, subscriptions (e.g. fee waiver).
- **B2B:** Commission per ticket, monthly fee, volume tiers.
- **Other:** Analytics as add-on; light ads (revenue share with theatres). Fees configurable per city/partner/product; store gross/net on booking for reconciliation.

## 6. OWASP Top 10

- **Access:** RBAC (customer, theatre, admin); ownership checks.
- **Crypto:** TLS; strong hashing; no secrets in logs/URLs; optional PII encryption at rest.
- **Injection:** Parameterized queries; validation; allowlists.
- **Design:** Threat modelling; least privilege; rate limit + idempotency.
- **Config:** Hardened env; security headers; no defaults; dependency scan in CI.
- **Auth:** OAuth2/OIDC; MFA for admin/theatre; lockout and logging.
- **Integrity:** Signed releases; verified webhooks.
- **Logging:** Auth and high-value actions; alerting on anomalies.
- **SSRF:** Allowlist outbound URLs; no server-side fetch of user-controlled URLs.

*(Demo: validation only; production adds full auth, headers, automated checks.)*

## 7. Compliance

- **Data:** Consent; access/delete/export; retention; PII minimization (GDPR and local).
- **PCI:** Tokenization and hosted checkout to minimize scope.
- **Finance/tax:** Jurisdiction-aware tax and invoicing; audit trail for bookings and payments.
- **Industry:** Age ratings; WCAG; local ticketing rules.
