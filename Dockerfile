FROM maven:3.9-eclipse-temurin-17-slim AS deps
WORKDIR /workspace/app

# Copy only Maven wrappers & POM first – lets Docker/Buildah cache all dependencies
COPY mvnw pom.xml ./
COPY .mvn ./.mvn

# BuildKit/Buildah cache for ~/.m2 speeds up every build
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q dependency:go-offline

FROM deps AS dev
# Bring in sources but mount-override them in compose for hot-reload
COPY src src
RUN ./mvnw -q spring-boot:build-image -P native -DskipTests || true # pre-compile once
# Hot-reload entrypoint; Boot DevTools will pick up changes in mounted src/
ENTRYPOINT ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=dev"]

FROM deps AS build
COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q package -DskipTests

# tiny runtime image
FROM gcr.io/distroless/java17-debian12 AS prod
WORKDIR /app
# non-root for security
USER nonroot:nonroot
COPY --from=build /workspace/app/target/*.jar app.jar
# Layer-aware Spring Boot ≥2.3 jars start faster & reuse more cache
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
