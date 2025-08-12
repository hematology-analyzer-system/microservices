# Hematology Analyzer System - Microservices

## 📌 Overview

The **Hematology Analyzer System** is a healthcare-focused microservices application designed to manage **identity and access**, **patient records**, and **test orders** for hematology analysis.
It follows a **microservices architecture** and leverages modern technologies for scalability, modularity, and integration with external systems.

This system consists of three main services:

1. **IAM Service** – Identity and Access Management (user authentication, authorization, role/privilege control).
2. **Patient Service** – Handles patient registration, updates, and patient data storage.
3. **Test Order Service** – Manages hematology test orders, results, and associated workflows.

---

## 🏗 Architecture

* **Microservices**: Each service is independent, containerized with Docker, and communicates via **RabbitMQ** (message broker).
* **Databases**:

  * **PostgreSQL** (shared relational DB for core services)
  * **MongoDB** (NoSQL storage for additional patient and test order data)
* **Message Broker**: **RabbitMQ** for event-driven communication.
* **API Gateway & Security**: Spring Security with JWT authentication.
* **CI/CD**: GitLab pipelines for build & packaging.

---

## 📂 Project Structure

```
hematology-analyzer-system-microservices/
│
├── compose.yml                  # Docker Compose configuration
├── scripts/                      # Helper scripts (start, reset, logs, etc.)
├── iam-service/                  # Identity & Access Management service
├── patient-service/              # Patient Management service
├── testorder-service/            # Test Order Management service
└── .gitlab-ci.yml                # Root CI/CD pipeline configuration
```

---

## 🛠 Technologies Used

* **Backend**: Java 21, Spring Boot 3, Spring Data JPA, Spring Security
* **Databases**: PostgreSQL, MongoDB
* **Messaging**: RabbitMQ
* **Build Tools**: Maven, Docker
* **Testing**: JUnit, Mockito
* **CI/CD**: GitLab CI
* **Containerization**: Docker & Docker Compose

---

## 🚀 Getting Started

### 1️⃣ Prerequisites

Make sure you have installed:

* [Docker & Docker Compose](https://docs.docker.com/get-docker/)
* [Java 21+](https://adoptium.net/)
* [Maven 3.9+](https://maven.apache.org/)

### 2️⃣ Clone the Repository

```bash
git clone https://github.com/hematology-analyzer-system/microservices.git
cd microservices
```

### 3️⃣ Run with Docker Compose

```bash
python ./scripts/up.py all
```

This will start:

* PostgreSQL (port `5433`)
* MongoDB (port `27017`)
* RabbitMQ (management UI on `15672`)
* IAM Service (port `8080`)
* Patient Service (port `8081`)
* Test Order Service (port `8082`)

---

## 📜 Environment Variables

Each service contains an `example.env` file.

---

## 📡 Service Endpoints

| Service            | Port | Base Path    | Description                                           |
| ------------------ | ---- | ------------ | ----------------------------------------------------- |
| IAM Service        | 8080 | `/iam`       | Authentication, authorization, user & role management |
| Patient Service    | 8081 | `/patient`   | Patient data CRUD operations                          |
| Test Order Service | 8082 | `/testorder` | Hematology test order & result management             |

---

## 🔄 Messaging (RabbitMQ)

RabbitMQ is used for:

* User registration/login events
* Patient data updates
* Test order creation & updates

Management UI: [http://localhost:15672](http://localhost:15672)
Default credentials: `guest / guest`

---

## ⚙ CI/CD

* GitLab CI pipelines build and package Docker images for each service.
* Versioned images are pushed to the GitLab Container Registry.

---

## 📄 License

This project is licensed under the **MIT License**.
