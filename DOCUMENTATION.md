# Devices API Documentation

This document provides extended details about the **Devices API** project, including API endpoints, database schema, and Docker setup.

---

## 1. Overview

The Devices API is a Spring Boot application that manages devices with attributes like **name**, **brand**, **state**, and **creation date**.
It uses **PostgreSQL** for persistence, **Flyway** for migrations, and **Docker Compose** for container orchestration.

---

## 2. Database Schema

Table: **devices**

| Column       | Type      | Constraints                   |
|--------------|----------|-------------------------------|
| id           | UUID      | Primary Key                   |
| name         | VARCHAR   | Not null                      |
| brand        | VARCHAR   | Not null                      |
| state        | VARCHAR   | Not null (enum: AVAILABLE, IN_USE, INACTIVE) |
| created_at   | TIMESTAMP | Default: now(), not null      |

---

## 3. API Endpoints

Base URL: `http://localhost:8085/api/devices`

### Create Device
**POST** `/api/devices`  
Request body:
```json
{
  "name": "Laptop X",
  "brand": "Lenovo",
  "state": "AVAILABLE"
}
```
Response `201 Created`:
```json
{
  "id": "uuid",
  "name": "Laptop X",
  "brand": "Lenovo",
  "state": "AVAILABLE",
  "createdAt": "2025-08-16T12:00:00Z"
}
```

---

### Get All Devices
**GET** `/api/devices`  
Response:
```json
[
  {
    "id": "uuid",
    "name": "Laptop X",
    "brand": "Lenovo",
    "state": "AVAILABLE",
    "createdAt": "2025-08-16T12:00:00Z"
  }
]
```

---

### Get Device by ID
**GET** `/api/devices/{id}`  
Example:
```
GET /api/devices/3fa85f64-5717-4562-b3fc-2c963f66afa6
```

---

### Get Devices by Brand
**GET** `/api/devices/brand/{brand}`  
Example:
```
GET /api/devices/brand/Apple
```

---

### Get Devices by State
**GET** `/api/devices/state/{state}`  
Example:
```
GET /api/devices/state/IN_USE
```

---

### Update Device
**PUT** `/api/devices/{id}`  
Request body:
```json
{
  "name": "Updated Laptop",
  "brand": "Dell",
  "state": "INACTIVE"
}
```

---

### Delete Device
**DELETE** `/api/devices/{id}`  
Response: `204 No Content`

---

## 4. Docker Setup

### Build and Start
```sh

docker compose build
docker compose up 

```

### Services
- **devices-api** → Runs Spring Boot on port **8085**
- **devices-db** → PostgreSQL database on port **5432**

---

## 5. Flyway Migrations

Flyway manages DB migrations automatically on application startup.  
Migration scripts should be placed in:
```
src/main/resources/db/migration
```

Example:
```sql
-- src/main/resources/db/migration/V1__create_devices_table.sql
CREATE TABLE devices (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    state VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

## 6. Notes
- Enum **DeviceState** is stored as a **STRING** in DB.
- Uses **Lombok** for boilerplate reduction.
- Exposes **Swagger UI** at:  
  [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

