# -------- Stage 1: Build the JAR --------
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies first (build cache optimization)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copy the full project and build
COPY . .
RUN mvn -q clean package -DskipTests

# -------- Stage 2: Run the JAR --------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the jar from Stage 1
COPY --from=builder /app/target/*.jar app.jar

# Expose port (Render will override)
EXPOSE 8080

# -------- Environment variables for Render --------
# (Render will replace them automatically)
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
