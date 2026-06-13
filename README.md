# Distributed Wallet Ledger System 💳

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-brightgreen)
![Kafka](https://img.shields.io/badge/Kafka-EventDriven-black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![Status](https://img.shields.io/badge/Status-MVP%20Complete-success)

A **highly scalable distributed wallet system** demonstrating:
- Event-driven microservices
- Saga pattern for distributed transactions
- Idempotent APIs
- Concurrency-safe wallet updates
- Kafka-based asynchronous communication

---

# 📌 System Architecture

## 🔷 High-Level Flow

```mermaid
graph TD

A[Wallet Service] -->|Transfer Request| B[PostgreSQL]
A -->|MoneyTransferredEvent| C[Kafka]

C --> D[Notification Service]

D -->|NotificationSentEvent| C
D -->|NotificationFailedEvent| C

C --> E[Wallet Service Consumer]

E -->|SUCCESS| B
E -->|COMPENSATED| B
````

---

# 🧠 Saga Workflow

```mermaid
sequenceDiagram
participant W as Wallet Service
participant K as Kafka
participant N as Notification Service

W->>W: Create Transaction (PENDING)
W->>K: MoneyTransferredEvent

K->>N: Consume Event

alt Success
N->>K: NotificationSentEvent
K->>W: Mark SUCCESS
else Failure
N->>K: NotificationFailedEvent
K->>W: Mark COMPENSATED
end
```

---

# ⚙️ Tech Stack

| Layer            | Technology           |
| ---------------- | -------------------- |
| Backend          | Java 17, Spring Boot |
| Database         | PostgreSQL           |
| Messaging        | Apache Kafka         |
| Containerization | Docker               |
| ORM              | Hibernate / JPA      |

---

# 🚀 Features

## 💰 Wallet System

* Create wallet per user
* Credit / Debit support
* Ledger-based audit trail

---

## 🔁 Distributed Transfer System

* Wallet-to-wallet transfers
* Kafka-based async processing
* Event-driven architecture

---

## 🧠 Saga Pattern

Implements distributed transaction states:

* `PENDING`
* `SUCCESS`
* `COMPENSATED`

---

## 🔐 Idempotency

* Prevents duplicate transfers
* Safe retry mechanism using idempotency key

---

## ⚡ Concurrency Control

* Optimistic locking using `@Version`
* Prevents race conditions in balance updates

---

# 📡 Kafka Topics

* `money-transferred`
* `notification-sent`
* `notification-failed`

---

# 🔄 Transaction Flow

## Step 1: Transfer Request

```http
POST /wallet/transfer
```

```json
{
  "fromWalletId": 1,
  "toWalletId": 3,
  "amount": 500,
  "idempotencyKey": "abc-123"
}
```

---

## Step 2: Wallet Service

* Creates transaction → `PENDING`
* Publishes `MoneyTransferredEvent`

---

## Step 3: Notification Service

* Processes event
* Publishes:

### Success

```json
{
  "referenceId": "uuid",
  "timestamp": "2026-06-13T10:00:00"
}
```

### Failure

```json
{
  "referenceId": "uuid",
  "reason": "Simulated failure",
  "timestamp": "2026-06-13T10:00:00"
}
```

---

## Step 4: Wallet Saga Completion

* SUCCESS → Transaction completed
* COMPENSATED → Transaction rolled back logically

---

# 🧾 Database Schema

## wallet_transactions

| Field          | Type                            |
| -------------- | ------------------------------- |
| reference_id   | UUID                            |
| status         | PENDING / SUCCESS / COMPENSATED |
| from_wallet_id | BIGINT                          |
| to_wallet_id   | BIGINT                          |

---

## ledger_entries

| Type | CREDIT / DEBIT |
| ---- | -------------- |

---

# 🧪 Testing Scenarios

## ✔ Success Flow

* Amount < threshold
* Status → SUCCESS

## ❌ Failure Flow

* Amount > threshold
* Status → COMPENSATED

## 🔁 Idempotency

* Same request key → no duplicate processing

## ⚡ Concurrency

* Multiple parallel debits handled safely

---

# 🐳 Infrastructure

Currently running:

* Kafka (Docker)
* PostgreSQL (Docker)

---

# 📌 Future Enhancements

* 🔴 Real compensation (reverse wallet transfer)
* 🔴 Redis distributed locking
* 🔴 Prometheus + Grafana monitoring
* 🔴 Full Docker Compose orchestration

---

# 🧠 Key Learnings

* Kafka-based microservices communication
* Saga pattern implementation
* Idempotent API design
* Distributed transaction handling
* Optimistic locking in high concurrency systems

---

# ▶️ Run Instructions

```bash
docker-compose up -d

cd wallet-service
mvn spring-boot:run

cd notification-service
mvn spring-boot:run
```

---

# 📊 Project Status

| Feature             | Status |
| ------------------- | ------ |
| Wallet Core         | ✅      |
| Ledger System       | ✅      |
| Kafka Integration   | ✅      |
| Saga Workflow       | ✅      |
| Idempotency         | ✅      |
| Concurrency Control | ✅      |
| Redis Locking       | ⏳ Next |
| Observability       | ⏳ Next |

---

