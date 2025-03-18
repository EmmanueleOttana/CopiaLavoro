# Fase 1: Build dell'app
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app

# Copia il file pom.xml e scarica le dipendenze (opzionale)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia il resto dell'app
COPY . .

# Aggiungi i permessi di esecuzione per mvnw
RUN chmod +x mvnw

# Compila il progetto e genera il JAR
RUN ./mvnw clean package -DskipTests

# Fase 2: Esecuzione dell'app
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
