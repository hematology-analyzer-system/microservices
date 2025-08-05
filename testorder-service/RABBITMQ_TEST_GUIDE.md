# TestOrder Service – RabbitMQ Test Guide

This guide shows how to **manually test** the RabbitMQ integration of the **TestOrder Service**.  After finishing the steps below you should be able to:

1. Publish *TestOrderCreated* / *TestOrderUpdated* messages by calling REST endpoints.
2. Consume *PatientCreated* / *PatientUpdated* messages from other services (or by publishing them manually from the RabbitMQ Management UI).
3. Inspect the message flow in MongoDB through the built-in **Message Log API**.
4. Verify exchanges / queues / bindings inside the RabbitMQ Management UI.

> All URLs below are written with the default compose setup (`localhost:8082` for TestOrder and `localhost:15672` for the RabbitMQ UI).

---
## 1. Prerequisites

1. Docker & Docker Compose installed.
2. Start the full stack:

   ```bash
   # from the project root
   docker compose -f compose.yml --profile testorder up -d    # or just `docker compose up -d` if you run everything
   ```

   The profile exposes:
   * TestOrder Service – `http://localhost:8082/testorder`
   * RabbitMQ – `amqp://guest:guest@localhost:5672`  (UI at `http://localhost:15672`, guest/guest)
   * MongoDB (message-logs) – `mongodb://localhost:27019` (only needed if you want to check the raw documents).

3. Obtain a JWT **access token** (see `AUTHENTICATION_GUIDE.md`).  Every request below assumes the token is passed in the header:

   ```http
   Authorization: Bearer <your-token>
   ```

---
## 2. Exchanges, Routing Keys & Queues

| Purpose                              | Exchange              | Routing key                        | Queue                               |
|--------------------------------------|-----------------------|------------------------------------|--------------------------------------|
| OUT – TestOrder Created 🟢           | `testorder.exchange`  | `testorder.event.created.v1`       | `testorder.created.q`                |
| OUT – TestOrder Updated 🟢           | `testorder.exchange`  | `testorder.event.updated.v1`       | `testorder.updated.q`                |
| IN  – Patient Created 🔵 (consumed)  | `patient.exchange`    | `patient.event.created.v1`         | `testorder.patient.created.q`        |
| IN  – Patient Updated 🔵 (consumed)  | `patient.exchange`    | `patient.event.updated.v1`         | `testorder.patient.updated.q`        |

Legend: 🟢 – produced by TestOrder service, 🔵 – consumed by TestOrder service.

---
## 3. REST End-points that Trigger RabbitMQ Messages

> Replace `<token>` with your JWT access token.

### 3.1 Create a **new** Test Order (publishes *Created* event)

```
POST /testorder/create
Host: localhost:8082
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "Nguyen Van A",
  "dateOfBirth": "01/15/1990",
  "gender": "MALE",               // MALE | FEMALE
  "address": "123 Le Loi, HCM",
  "phoneNumber": "0909000001",
  "email": "a.nguyen@example.com"
}
```

*Expected result*
* `200 OK` with the created TestOrder payload.
* A **`TestOrderCreatedEvent`** is published to `testorder.exchange` with routing-key `testorder.event.created.v1`.
* Message is logged as `OUTGOING/PROCESSED` (see section 4).

### 3.2 Create using an **existing patient**
```
POST /testorder/create/{patientId}
```
Identical to 3.1 but without a request body.  A created event will still be published.

### 3.3 Update an existing Test Order (publishes *Updated* event)
```
PUT /testorder/{testOrderId}
Host: localhost:8082
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "Nguyen Van B",
  "address": "456 Tran Hung Dao, HCM",
  "gender": "MALE",
  "dateOfBirth": "01/15/1990",
  "phone": "0909123456"
}
```
*Expected result*
* `200 OK` returned.
* A **`TestOrderUpdatedEvent`** is published (`testorder.event.updated.v1`).

---
## 4. Message Log API (MongoDB Audit)

The service writes every incoming & outgoing message to MongoDB and exposes read-only endpoints for quick inspection.

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/messages` | Paginate all messages (`page`,`size`,`sortBy`,`direction`) |
| GET | `/api/messages/direction/{direction}` | Filter by `INCOMING` or `OUTGOING` |
| GET | `/api/messages/status/{status}` | Filter by `RECEIVED`,`PROCESSED`,`FAILED` |
| GET | `/api/messages/correlation/{id}` | Find message chain using correlation-id |
| GET | `/api/messages/recent` | Messages in the last 24 h |
| GET | `/api/messages/stats` | Quick statistics summary |

Example:
```
curl -H "Authorization: Bearer <token>" \
     http://localhost:8082/testorder/api/messages?size=10
```