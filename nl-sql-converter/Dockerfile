FROM openjdk:17-jdk-alpine

LABEL maintainer="nl-sql-converter"

# Set working directory
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Create a new stage for runtime
FROM openjdk:17-jre-alpine

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=0 /app/target/nl-sql-converter-1.0.0.jar app.jar

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Change ownership of the app directory
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/query/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]