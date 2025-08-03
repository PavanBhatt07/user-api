# Use official Gradle image to build the app
FROM gradle:8.2.1-jdk17 AS build

WORKDIR /app

COPY . .

RUN gradle installDist --no-daemon

# Use a smaller JDK image for running the app
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/build/install/user-api /app

EXPOSE 8080

CMD ["./bin/user-api"]
