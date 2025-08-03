# Use the official Gradle image to build the application
FROM gradle:8.4.0-jdk17 as builder

# Set the working directory
WORKDIR /app

# Copy everything to /app
COPY . .

# Build the project
RUN gradle installDist

# Use a smaller base image to run the application
FROM openjdk:17-slim

# Set working directory
WORKDIR /app

# Copy the built app from builder
COPY --from=builder /app/build/install/* /app

# Expose port (match what your app uses, 8080)
EXPOSE 8080

# Run the app
CMD ["./bin/user-api"]
