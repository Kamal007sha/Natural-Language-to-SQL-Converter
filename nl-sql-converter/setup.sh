#!/bin/bash

# NL-SQL Converter Setup Script

set -e

echo "🚀 Setting up NL-SQL Converter..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    print_success "Docker and Docker Compose are installed"
}

# Check if Java and Maven are installed (for local development)
check_java_maven() {
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
        print_status "Java version: $JAVA_VERSION"
    else
        print_warning "Java is not installed (required for local development)"
    fi
    
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version 2>&1 | head -n 1)
        print_status "Maven: $MVN_VERSION"
    else
        print_warning "Maven is not installed (required for local development)"
    fi
}

# Clean up existing containers and volumes
cleanup() {
    print_status "Cleaning up existing containers and volumes..."
    docker-compose down --volumes --remove-orphans 2>/dev/null || true
    docker system prune -f 2>/dev/null || true
    print_success "Cleanup completed"
}

# Build and start services
start_services() {
    print_status "Building and starting services..."
    
    # Build the application
    print_status "Building NL-SQL Converter application..."
    docker-compose build --no-cache
    
    # Start services
    print_status "Starting services..."
    docker-compose up -d
    
    print_success "Services started successfully"
}

# Wait for services to be ready
wait_for_services() {
    print_status "Waiting for services to be ready..."
    
    # Wait for PostgreSQL
    print_status "Waiting for PostgreSQL..."
    for i in {1..30}; do
        if docker-compose exec -T postgres pg_isready -U nlsql_user -d nlsql_db &> /dev/null; then
            print_success "PostgreSQL is ready"
            break
        fi
        if [ $i -eq 30 ]; then
            print_error "PostgreSQL failed to start"
            exit 1
        fi
        sleep 2
    done
    
    # Wait for application
    print_status "Waiting for NL-SQL Converter application..."
    for i in {1..60}; do
        if curl -s http://localhost:8080/api/query/health &> /dev/null; then
            print_success "NL-SQL Converter application is ready"
            break
        fi
        if [ $i -eq 60 ]; then
            print_error "NL-SQL Converter application failed to start"
            docker-compose logs nlsql-app
            exit 1
        fi
        sleep 2
    done
}

# Test the application
test_application() {
    print_status "Testing the application..."
    
    # Test health endpoint
    HEALTH_RESPONSE=$(curl -s http://localhost:8080/api/query/health)
    if [[ "$HEALTH_RESPONSE" == *"running"* ]]; then
        print_success "Health check passed"
    else
        print_error "Health check failed: $HEALTH_RESPONSE"
        exit 1
    fi
    
    # Test query endpoint
    print_status "Testing query endpoint..."
    QUERY_RESPONSE=$(curl -s -X POST http://localhost:8080/api/query \
        -H "Content-Type: application/json" \
        -d '{"query": "show all employees"}')
    
    if [[ "$QUERY_RESPONSE" == *"sql"* ]]; then
        print_success "Query endpoint test passed"
        echo "Sample response: $QUERY_RESPONSE" | head -c 200
        echo "..."
    else
        print_error "Query endpoint test failed: $QUERY_RESPONSE"
        exit 1
    fi
}

# Show application information
show_info() {
    echo ""
    echo "🎉 NL-SQL Converter is ready!"
    echo ""
    echo "📋 Application Information:"
    echo "   Application URL: http://localhost:8080"
    echo "   Health Check:    http://localhost:8080/api/query/health"
    echo "   Query Endpoint:  http://localhost:8080/api/query"
    echo "   Database:        PostgreSQL on localhost:5432"
    echo ""
    echo "🔧 Available Commands:"
    echo "   View logs:       docker-compose logs -f"
    echo "   Stop services:   docker-compose down"
    echo "   Restart:         docker-compose restart"
    echo "   View containers: docker-compose ps"
    echo ""
    echo "📖 Sample API Usage:"
    echo '   curl -X POST http://localhost:8080/api/query \'
    echo '        -H "Content-Type: application/json" \'
    echo '        -d '"'"'{"query": "show all employees in Mumbai"}'"'"
    echo ""
    echo "💡 Sample Queries to Try:"
    echo "   - show all employees"
    echo "   - count employees in Mumbai"
    echo "   - list employees in Engineering department"
    echo "   - show employees with salary greater than 70000"
    echo "   - get top 5 employees by salary"
    echo ""
}

# Main execution
main() {
    echo "==============================================="
    echo "   NL-SQL Converter Setup"
    echo "==============================================="
    echo ""
    
    # Parse command line arguments
    case "${1:-}" in
        "clean")
            cleanup
            exit 0
            ;;
        "test")
            test_application
            exit 0
            ;;
        "dev")
            check_java_maven
            print_status "Starting in development mode..."
            if [ -f "pom.xml" ]; then
                mvn clean compile
                mvn spring-boot:run
            else
                print_error "pom.xml not found. Are you in the project directory?"
                exit 1
            fi
            exit 0
            ;;
        "logs")
            docker-compose logs -f
            exit 0
            ;;
        "stop")
            docker-compose down
            print_success "Services stopped"
            exit 0
            ;;
        "help"|"-h"|"--help")
            echo "Usage: $0 [command]"
            echo ""
            echo "Commands:"
            echo "  (no args)  - Full setup and start"
            echo "  clean      - Clean up containers and volumes"
            echo "  test       - Test the running application"
            echo "  dev        - Run in development mode (requires Java/Maven)"
            echo "  logs       - Show application logs"
            echo "  stop       - Stop all services"
            echo "  help       - Show this help message"
            exit 0
            ;;
    esac
    
    # Full setup process
    check_docker
    check_java_maven
    cleanup
    start_services
    wait_for_services
    test_application
    show_info
}

# Handle script interruption
trap 'print_error "Script interrupted"; exit 1' INT TERM

# Run main function
main "$@"