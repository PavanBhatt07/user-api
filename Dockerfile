# ---- Build Stage ----
FROM gradle:8.4.0-jdk17-alpine AS build
WORKDIR /app
COPY . .

# Build the application
RUN gradle installDist

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy built artifacts from the build stage
COPY --from=build /app/build/install/user-api /app

# Run the app
CMD ["./bin/user-api"]
