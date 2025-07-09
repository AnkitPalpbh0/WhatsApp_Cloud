# 📲 WhatsApp Business Backend API

A Spring Boot-based backend service to send messages using the **WhatsApp Cloud API**, integrated with **PostgreSQL**, **RabbitMQ**, **OpenTelemetry**, and **Prometheus**. Containerized using Docker for local development and testing.

---

## 🚀 Features

- Send WhatsApp messages using the [Meta WhatsApp Cloud API](https://developers.facebook.com/docs/whatsapp/cloud-api)
- Persist message data in PostgreSQL
- Use RabbitMQ for internal async processing (webhook events)
- Collect metrics using OpenTelemetry
- Docker-based local development

---

## 🧾 Environment Configuration

Create a `.env` file in the root of your project with the following values:

```env
# ===== Database Credentials =====
DB_HOST=localhost
DB_NAME=whatsapp_db
DB_USERNAME=postgres
DB_PASSWORD=postgres

# ===== WhatsApp Cloud API Config =====
WHATSAPP_TOKEN=your_actual_token
WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id

# ===== OpenTelemetry Config =====
OTEL_SERVICE_NAME=whatsapp-backend
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
OTEL_EXPORTER_OTLP_PROTOCOL=grpc

# ===== Monitoring & Firebase =====
PROMETHEUS_ALLOWED_IPS=127.0.0.1,172.17.0.1
FIREBASE_CONFIG_PATH=sm-shop/src/main/resources/firebase-adminsdk-ol-7cc4d7bcd8.json

# ===== CORS & RabbitMQ =====
CORS_ALLOWED_ORIGIN=http://localhost:3000
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

## 🚀 Running the Application

```bash
  ./gradlew clean build -x test
```

```bash
  docker-compose up --build
```