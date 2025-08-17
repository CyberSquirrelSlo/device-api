# Devices API

A simple Spring Boot REST API for managing devices.  
It uses **PostgreSQL** and **MySql** as the database
and **Docker Compose** for containerization.
The database can be any relational databse
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

### Build and start the containers for the app with postgresql database:

```bash

docker compose build --no-cache
docker compose up

```

### Stop and remove containers

```bash

docker compose down

```

### Build and start the containers for the app with mysql database:

```bash

docker compose -f .\docker-compose-mysql.yml build --no-cache
docker compose -f .\docker-compose-mysql.yml up

```

### Stop and remove containers

```bash

docker compose -f .\docker-compose-mysql.yml down

```

## Notes
- Enum **DeviceState** is stored as a **STRING** in DB.
- Uses **Lombok** for boilerplate reduction.
- 
### Exposes **Swagger UI** at:  
  [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

***MORE*** [DOCUMENTATION.md](DOCUMENTATION.md)
