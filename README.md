# Devices API

A simple Spring Boot REST API for managing devices.  
It uses **PostgreSQL** as the database and **Docker Compose** for containerization.

---

## 🚀 Features
- Create, read, update, delete devices
- Filter devices by:
    - `brand`
    - `state` (AVAILABLE, IN_USE, INACTIVE)
- Auto-generated `UUID` IDs
- Stores device state as a string enum

---

## 📦 Requirements
- [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/)
- Java 21 (if you run locally without Docker)
- Maven (if you build outside Docker)

---

## ▶️ Running with Docker Compose

Build and start the containers:

```bash

docker compose build
docker compose up

```

## Notes
- Enum **DeviceState** is stored as a **STRING** in DB.
- Uses **Lombok** for boilerplate reduction.
- Exposes **Swagger UI** at:  
  [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
