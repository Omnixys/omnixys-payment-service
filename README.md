# 💸 Omnixys Payment Service

Der **Omnixys Payment Service** ist ein sicherer, modularer Microservice zur Zahlungsabwicklung innerhalb des Omnixys-Ökosystems. Er übernimmt Transaktionsverarbeitung, Validierung, Audit-Logging und Event-Publishing via Kafka. Zugriffe werden vollständig über Keycloak abgesichert. Alle Interaktionen erfolgen über eine GraphQL-Schnittstelle.

> Powered by **OmnixysOS**
> Teil von **OmnixysSphere** – The Fabric of Modular Innovation

---

## 🧠 Features

* 💳 Zahlungsvalidierung & Autorisierung
* 🔐 Integration mit **Keycloak** (OAuth2, Rollen)
* 📊 Vollständiges **Distributed Tracing** mit **OpenTelemetry + Tempo**
* 📦 Event-basierte Kommunikation über **Apache Kafka**
* 📟 DSGVO-konformes Audit-Logging (JSON, Kafka, Loki-ready)
* 🧪 Unit-Tests mit **JUnit / Mockito**
* 📈 Exporte & Metriken über **Prometheus**
* 🌐 GraphQL API (kein REST)

---

## ⚙️ Tech Stack

| Kategorie         | Technologie           |
| ----------------- | --------------------- |
| Sprache           | Java (Spring Boot)    |
| Authentifizierung | Keycloak              |
| Messaging         | Apache Kafka          |
| Tracing           | OpenTelemetry + Tempo |
| Logging           | LoggerPlus + Loki     |
| Monitoring        | Prometheus + Grafana  |
| API               | GraphQL (Code First)  |
| Port              | `7201`                |

---

## 📀 Architektur

```mermaid
flowchart LR
    A[Client (UI/Gateway)] --> B(GraphQL API)
    B --> C{Keycloak Auth}
    B --> D(PaymentService)
    D --> E(Kafka Producer)
    D --> F(LoggerPlus)
    F --> G(Kafka Topic: logs.payment)
    E --> H(Kafka Topic: payment.created)
```

---

## 🚀 Getting Started

```bash
# 1. Repository klonen
git clone https://github.com/omnixys/omnixys-payment-service.git
cd omnixys-payment-service

# 2. Build mit Gradle
./gradlew build

# 3. Starten mit Docker
docker-compose up
```

> Siehe `docker-compose.yml` für Abhängigkeiten wie Kafka, Keycloak und Tempo.

---

## 📌 GraphQL Endpoint

* URL: `http://localhost:7201/graphql`
* Schema: Code First (`/src/main/java/com/omnixys/payment/graphql/`)

---

## ✅ Tests

```bash
./gradlew test
```

* Testabdeckung: Ziel > 80 %
* Tools: JUnit, Mockito, JaCoCo

---

## 📦 Kafka Topics

| Event                  | Topic Name        |
| ---------------------- | ----------------- |
| Zahlung erstellt       | `payment.created` |
| Zahlung fehlgeschlagen | `payment.failed`  |
| Logs                   | `logs.payment`    |

---

## 🔐 Security

* Keycloak konfiguriert mit Rollen wie `user`, `admin`
* Nur berechtigte Clients dürfen Mutationen ausführen
* Token-Prüfung über Spring Security + Keycloak Adapter

---

## 📈 Observability

* Traces via **OpenTelemetry → Tempo**
* Logs via **LoggerPlus → Kafka → Loki**
* Metriken via **Prometheus**

---

## 🤝 Contribution

Siehe [CONTRIBUTING.md](../CONTRIBUTING.md) für Branch-Strategie, Teststandards und PR-Richtlinien.

---

## 📄 License

Dieses Projekt steht unter der [GNU GPL v3.0](./LICENSE).

© 2025 [Omnixys](https://omnixys.com) – Modular Thinking. Infinite Possibilities.
