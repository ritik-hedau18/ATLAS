# 🚀 ATLAS — Advanced Talent & Leadership Acquisition System

> **Project Status**: 🛠️ *Under Active Development*

A production-grade professional networking platform built with **Java Spring Boot microservices**, inspired by LinkedIn.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?logo=springboot)
![React](https://img.shields.io/badge/React-19-blue?logo=react)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Helm-326CE5?logo=kubernetes)

---

## 🏗️ Architecture

```
Client (React 19 + TypeScript + Tailwind CSS)
        │
        ▼
API Gateway (Spring Cloud Gateway — Port 8080)
        │  JWT Validation + Rate Limiting (Redis)
        │
        ├── User Service          (8081) — PostgreSQL
        ├── Post Service          (8082) — PostgreSQL
        ├── Connection Service    (8083) — Neo4j + PostgreSQL
        ├── Feed Service          (8084) — Kafka Streams + Redis
        ├── Job Service           (8085) — PostgreSQL
        ├── Notification Service  (8086) — MongoDB + Spring Mail
        ├── Search Service        (8087) — Elasticsearch
        ├── AI Service            (8088) — Hugging Face API
        └── Audit Log Service     (8089) — Elasticsearch
```

All services register with **Eureka Discovery Server** (8761) and pull configuration from **Spring Cloud Config Server** (8888).

---

## 📦 Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 17, Spring Boot 3.3.5, Spring Cloud 2023.0.3 |
| **API Gateway** | Spring Cloud Gateway, JWT, Redis Rate Limiter |
| **Databases** | PostgreSQL, Neo4j (Graph), MongoDB, Redis, Elasticsearch |
| **Messaging** | Apache Kafka, Kafka Streams |
| **AI/ML** | Hugging Face Inference API (`unitary/toxic-bert`), Cosine Similarity |
| **Frontend** | React 19, TypeScript, Tailwind CSS, Lucide Icons |
| **Observability** | Zipkin (Tracing), Prometheus + Grafana (Metrics), ELK Stack (Logs) |
| **Deployment** | Docker Compose, Kubernetes, Helm Charts |
| **CI/CD** | GitHub Actions |
| **Testing** | JUnit 5, Mockito, MockMvc, JMeter |

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- Node.js 18+

### 1. Start Infrastructure
```bash
docker-compose up -d
```

### 2. Build All Services
```bash
mvn clean install -DskipTests
```

### 3. Run Services
Start in order: `discovery-server` → `config-server` → remaining services.

### 4. Start Frontend
```bash
cd atlas-frontend
npm install
npm run dev
```

---

## 🧩 Microservices

| Service | Port | Database | Key Features |
|---------|------|----------|--------------|
| **Discovery Server** | 8761 | — | Eureka service registry |
| **Config Server** | 8888 | — | Centralized configuration (native profile) |
| **API Gateway** | 8080 | Redis | JWT auth, request routing, rate limiting |
| **User Service** | 8081 | PostgreSQL | Auth, profiles, skills, education, experience |
| **Post Service** | 8082 | PostgreSQL | Posts, comments, likes, AI content moderation |
| **Connection Service** | 8083 | Neo4j | Graph connections, PYMK, mutual connections, shortest path |
| **Feed Service** | 8084 | Redis | Kafka Streams topology, feed scoring, cursor pagination |
| **Job Service** | 8085 | PostgreSQL | Companies, job postings, applications, AI matching |
| **Notification Service** | 8086 | MongoDB | Kafka consumer, in-app + email notifications |
| **Search Service** | 8087 | Elasticsearch | Full-text search, autocomplete, Kafka index sync |
| **AI Service** | 8088 | — | Toxicity detection, job matching, PYMK ranking |
| **Audit Log Service** | 8089 | Elasticsearch | Multi-topic Kafka consumer, structured audit logs |

---

## 📊 Monitoring & Observability

| Tool | URL | Purpose |
|------|-----|---------|
| Zipkin | `http://localhost:9411` | Distributed tracing |
| Prometheus | `http://localhost:9090` | Metrics collection |
| Grafana | `http://localhost:3000` | Metrics dashboards |
| Kibana | `http://localhost:5601` | Log visualization |
| Eureka | `http://localhost:8761` | Service registry dashboard |

---

## 🧪 Testing

```bash
# Unit & Integration Tests
mvn clean test

# Load Testing (JMeter)
jmeter -n -t jmeter-load-test.jmx -l results.jtl
```

---

## ☸️ Kubernetes Deployment

```bash
# Apply manifests directly
kubectl apply -f k8s/

# Or deploy via Helm
helm upgrade --install atlas ./helm --values ./helm/values.yaml
```

---

## 📁 Project Structure

```
ATLAS/
├── pom.xml                          # Root Maven Parent POM
├── discovery-server/                # Eureka Discovery Server
├── config-server/                   # Spring Cloud Config Server
├── config-repo/                     # Centralized YAML configs
├── api-gateway/                     # Spring Cloud Gateway
├── user-service/                    # User profiles & auth
├── connection-service/              # Neo4j graph service
├── post-service/                    # Posts & comments
├── feed-service/                    # Kafka Streams feed
├── job-service/                     # Jobs & applications
├── notification-service/            # Notifications
├── search-service/                  # Elasticsearch search
├── ai-service/                      # AI/ML service
├── audit-log-service/               # Audit logging
├── atlas-frontend/                  # React frontend
├── docker-compose.yml               # Local infrastructure
├── docker-compose.monitoring.yml    # Observability stack
├── k8s/                             # Kubernetes manifests
├── helm/                            # Helm charts
└── .github/workflows/ci-cd.yml     # CI/CD pipeline
```

---

## 📄 License

This project is built for educational and portfolio purposes.
