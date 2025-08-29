# Natural Language to SQL Converter

A complete system (backend + frontend) that converts natural language queries into SQL and executes them against a PostgreSQL database.

## Features

### Backend

* **Natural Language Processing**: Rule-based NLP to parse and understand natural language queries
* **SQL Generation**: Automatic conversion of parsed queries into valid SQL statements
* **Database Schema Mapping**: Dynamic mapping of natural language terms to database tables and columns
* **Query Execution**: Safe execution of generated SQL queries with result formatting
* **REST API**: Clean REST endpoints for query processing
* **Sample Data**: Pre-populated database with employee, department, product, and order data

### Frontend

* **React-based UI**: Simple interface for entering queries and viewing results
* **SQL Display**: Shows generated SQL with syntax highlighting
* **Results Table**: Displays query results in a clean table
* **Error Handling**: Graceful handling of API/database errors
* **Tailwind CSS**: Modern styling and layout

---

## Tech Stack

* **Backend**: Java 17, Spring Boot 3.2.0, Maven, PostgreSQL, JPA/Hibernate
* **Frontend**: React 18, Axios, Tailwind CSS
* **Other**: Docker & Docker Compose (optional)

---

## Quick Start

### Backend Setup

1. **Create PostgreSQL database**:

   ```sql
   CREATE DATABASE nlsql_db;
   CREATE USER nlsql_user WITH ENCRYPTED PASSWORD 'strongpassword';
   GRANT ALL PRIVILEGES ON DATABASE nlsql_db TO nlsql_user;
   ```

2. **Configure application** (`src/main/resources/application.yml`):

   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/nlsql_db
       username: nlsql_user
       password: strongpassword
   ```

3. **Build and run backend**:

   ```bash
   mvn clean package
   java -jar target/nl-sql-converter-1.0.0.jar
   ```

   Backend runs at: `http://localhost:8080/api`

---

### Frontend Setup

1. Navigate to frontend folder:

   ```bash
   cd frontend
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Start development server:

   ```bash
   npm start
   ```

   Frontend runs at: `http://localhost:3000`

---

## API Documentation

### Query Endpoint

**POST** `/api/query`

Request:

```json
{
  "query": "show all employees in Mumbai"
}
```

Response:

```json
{
  "sql": "SELECT * FROM employees WHERE city = 'Mumbai' LIMIT 1000",
  "results": [
    { "id": 1, "name": "John Doe", "city": "Mumbai", "salary": 75000 }
  ],
  "execution_time_ms": 45
}
```

### Health Check

**GET** `/api/query/health` → Returns: `"NL-SQL Converter is running"`

---

## How It Works

1. User enters query in **frontend UI**
2. Frontend sends query to **backend API** (`/api/query`)
3. Backend:

   * Parses natural language
   * Maps to schema
   * Generates SQL
   * Executes SQL on PostgreSQL
   * Returns results as JSON
4. Frontend:

   * Displays generated SQL
   * Renders results in a table

---

## Database Schema

### Employees

* `id`, `name`, `email`, `city`, `department`, `salary`, `age`, `hire_date`, `is_active`

### Departments

* `id`, `name`, `manager_id`, `budget`, `location`

### Products

* `id`, `name`, `price`, `category`, `stock_quantity`, `created_date`

### Orders

* `id`, `customer_id`, `product_id`, `quantity`, `total_amount`, `order_date`, `status`

---

## Development Workflow

* Start PostgreSQL

* Run backend:

  ```bash
  mvn spring-boot:run
  ```

  Runs at: `http://localhost:8080/api`

* Run frontend:

  ```bash
  npm start
  ```

  Runs at: `http://localhost:3000`

* Access the complete system via frontend.

---

## Troubleshooting

1. **Database Connection Failed**:

   * Ensure PostgreSQL is running
   * Verify credentials in `application.yml`

2. **Backend Won't Start**:

   * Check logs: `mvn spring-boot:run`
   * Verify Java 17 is installed

3. **Frontend Issues**:

   * Ensure backend is running on port 8080
   * Check console for CORS/API errors

---
