# Usa un'immagine di base con JDK 17 (o una versione compatibile con il tuo progetto)
FROM eclipse-temurin:17-jdk

# Imposta la directory di lavoro
WORKDIR /app

# Copia il file JAR generato nel container
COPY target/vendita-prodotti-1.0.0.jar app.jar

# Specifica la porta che l'app usa (modifica se necessario)
EXPOSE 8080

# Comando per avviare l'applicazione
CMD ["java", "-jar", "app.jar"]
