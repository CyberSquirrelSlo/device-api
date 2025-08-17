# Devices REST API

---

A simple Spring Boot REST API for managing devices.  
It uses **PostgreSQL** and **MySql** as the database and **Docker Compose** for containerization.
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




### Services

You try one at the time. First build with docker as shown above then start it after 
if you want to test the other one you first stop and remove containers and start with the other database 

#### Ports

- **devices-api** → Runs Spring Boot on port **8085** (both MySql and Postgres app)
- **devices-db** → PostgreSQL database on port **5432**
- **devices-db-mysql** → PostgreSQL database on port **3307**

### Exposes **Swagger UI** at: 

  [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

***More documentation at:*** [DOCUMENTATION.md](DOCUMENTATION.md)
