# Natural Language to SQL Converter

A complete backend system built with Java Spring Boot that converts natural language queries into SQL and executes them against a PostgreSQL database.

## Features

- **Natural Language Processing**: Rule-based NLP to parse and understand natural language queries
- **SQL Generation**: Automatic conversion of parsed queries into valid SQL statements
- **Database Schema Mapping**: Dynamic mapping of natural language terms to database tables and columns
- **Query Execution**: Safe execution of generated SQL queries with result formatting
- **REST API**: Clean REST endpoints for query processing
- **Containerization**: Full Docker support with docker-compose
- **Sample Data**: Pre-populated database with employee, department, product, and order data

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Maven** for dependency management
- **PostgreSQL** database
- **JPA/Hibernate** for database operations
- **Docker & Docker Compose** for containerization

## Quick Start

### Using Docker (Recommended)

1. **Clone and setup**:
   ```bash
   git clone <repository-url>
   cd nl-sql-converter
   chmod +x setup.sh
   ```

2. **Run the complete setup**:
   ```bash
   ./setup.sh
   ```

3. **Test the application**:
   ```bash
   curl -X POST http://localhost:8080/api/query \
        -H "Content-Type: application/json" \
        -d '{"query": "show all employees in Mumbai"}'
   ```

### Manual Setup

1. **Start PostgreSQL**:
   ```bash
   docker-compose up postgres -d
   ```

2. **Build and run the application**:
   ```bash
   mvn clean package
   java -jar target/nl-sql-converter-1.0.0.jar
   ```

## API Documentation

### Query Endpoint

**POST** `/api/query`

**Request**:
```json
{
  "query": "show all employees in Mumbai"
}
```

**Response**:
```json
{
  "sql": "SELECT * FROM employees WHERE city = 'Mumbai' LIMIT 1000",
  "results": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@company.com",
      "city": "Mumbai",
      "department": "Engineering",
      "salary": 75000.00,
      "age": 30
    }
  ],
  "execution_time_ms": 45
}
```

### Health Check

**GET** `/api/query/health`

Returns: `"NL-SQL Converter is running"`

## Sample Queries

The system supports various types of natural language queries:

### Basic Queries
- `"show all employees"`
- `"list all products"`
- `"get all departments"`

### Filtered Queries
- `"show employees in Mumbai"`
- `"list employees in Engineering department"`
- `"find products in Electronics category"`

### Count Queries
- `"count employees"`
- `"how many employees in Mumbai"`
- `"number of products"`

### Conditional Queries
- `"show employees with salary greater than 70000"`
- `"list employees aged over 30"`

### Limited Results
- `"show top 5 employees"`
- `"first 10 products"`
- `"limit 3 departments"`

### Sorted Results
- `"show employees order by salary"`
- `"list products sort by price"`

## Database Schema

The system includes sample tables:

### Employees Table
- `id`, `name`, `email`, `city`, `department`, `salary`, `age`, `hire_date`, `is_active`

### Departments Table  
- `id`, `name`, `manager_id`, `budget`, `location`

### Products Table
- `id`, `name`, `price`, `category`, `stock_quantity`, `created_date`

### Orders Table
- `id`, `customer_id`, `product_id`, `quantity`, `total_amount`, `order_date`, `status`

## Architecture

### Core Components

1. **NLSQLController**: REST API endpoints
2. **NLSQLConverterService**: Main orchestration service
3. **NLPProcessorService**: Natural language parsing and intent extraction
4. **DatabaseSchemaService**: Schema introspection and mapping
5. **QueryExecutionService**: Safe SQL execution with validation

### Processing Flow

1. **Input**: Natural language query via REST API
2. **Parsing**: Extract intent, entities, and query structure
3. **Mapping**: Map natural language terms to database schema
4. **Generation**: Convert parsed query to SQL
5. **Validation**: Ensure SQL safety and add limits
6. **Execution**: Run SQL against database
7. **Response**: Return SQL and results as JSON

## Configuration

### Application Properties (`application.yml`)

Key configuration options:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nlsql_db
    username: nlsql_user
    password: nlsql_password

nlsql:
  query:
    max-results: 1000
    timeout: 30
```

### Environment Variables

- `OPENAI_API_KEY`: OpenAI API key (optional, for future AI integration)
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

## Development

### Running Locally

```bash
# Start database
docker-compose up postgres -d

# Run application in development mode
./setup.sh dev
```

### Running Tests

```bash
mvn test
```

### Building

```bash
mvn clean package
```

## Docker Commands

### Basic Operations
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild and restart
docker-compose up --build -d
```

### Utilities
```bash
# Clean setup
./setup.sh clean

# View application logs
./setup.sh logs

# Test running application
./setup.sh test

# Stop all services
./setup.sh stop
```

## Security Features

- SQL injection prevention through parameterized queries
- Query validation to block dangerous SQL operations
- Limited query results to prevent resource exhaustion
- Safe query execution with timeout handling

## Extensibility

The system is designed for easy extension:

- **Add new tables**: Update schema mappings in `DatabaseSchemaService`
- **Enhance NLP**: Extend patterns in `NLPProcessorService`
- **Add query types**: Support INSERT, UPDATE, DELETE operations
- **AI Integration**: Replace rule-based NLP with ML models
- **Custom functions**: Add aggregations, joins, subqueries

## Troubleshooting

### Common Issues

1. **Database Connection Failed**:
   - Ensure PostgreSQL is running: `docker-compose ps`
   - Check credentials in `application.yml`

2. **Application Won't Start**:
   - Check logs: `docker-compose logs nlsql-app`
   - Verify Java 17 is available

3. **Query Parsing Issues**:
   - Check supported query patterns in `NLPProcessorService`
   - Ensure table/column names exist in schema

### Getting Help

- Check application logs: `./setup.sh logs`
- Test database connection: Access PostgreSQL directly
- Verify API endpoints: `curl http://localhost:8080/api/query/health`

## License

This project is licensed under the MIT License.
