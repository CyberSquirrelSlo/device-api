# Devices REST API

---

A simple Spring Boot REST API for managing devices.  
It uses **PostgreSQL** and **MySQL** as the databases and **Docker Compose** for containerization.
The database can be any relational database

---

## 🚀 Features
- Create, read, update, and delete devices
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

### Build and start the containers for the app with PostgreSQL database:

```bash

docker compose build --no-cache
docker compose up

```

### Stop and remove containers

```bash

docker compose down

```

### Build and start the containers for the app with MySQL database:

```bash

docker compose -f .\docker-compose-mysql.yml build --no-cache
docker compose -f .\docker-compose-mysql.yml up

```

### Stop and remove containers

```bash

docker compose -f .\docker-compose-mysql.yml down

```

### Services

You try one at a time. First, build with Docker as shown above, then start it after 
If you want to test the other one, first stop and remove containers and then start with the other database 

#### Ports

- **devices-api** → Runs Spring Boot on port **8085** (both MySql and Postgres app)
- **devices-db** → PostgreSQL database on port **5432**
- **devices-db-mysql** → PostgreSQL database on port **3307**


## ▶️ Run the aplication localy

---

First start coresponding DB container.

---

And use coresponding applicaton.yml file

---

then run

```bash

 mvn spring-boot:run
 
```


## 🧪 Tests

---

Be sure you have started docker desktop

---

### To run the tests

```bash

mvn clean verify

```

## 📊 Jacoco results are at:

```
/target/site/jacoco/index.html
```

## 📝 Exposes **Swagger UI** at: 

  [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

## 📖 More documentation at: 

[DOCUMENTATION](DOCUMENTATION.md)
