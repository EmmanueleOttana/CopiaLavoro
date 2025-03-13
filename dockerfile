# Fase 1: Build del progetto
FROM eclipse-temurin:17-jdk AS builder

# Imposta la directory di lavoro
WORKDIR /app

# Copia i file del progetto dentro il container
COPY . .

# Compila il progetto e genera il JAR
RUN ./mvnw clean package -DskipTests

# Fase 2: Esecuzione dell'app
FROM eclipse-temurin:17-jre

# Imposta la directory di lavoro
WORKDIR /app

# Copia solo il file JAR dalla fase di build
COPY --from=builder /app/target/*.jar app.jar

# Espone la porta 8080 (Spring Boot usa questa porta di default)
EXPOSE 8080

# Comando per avviare l'applicazione
CMD ["java", "-jar", "app.jar"]
