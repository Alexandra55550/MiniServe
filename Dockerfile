# =============================
# Etapa 1: Construir la aplicación
# =============================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Configurar el entorno de trabajo
WORKDIR /app

# Copiar el descriptor de Maven y resolver dependencias (cache eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Configurar UTF-8 para evitar errores de codificación
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# Compilar el proyecto (sin ejecutar tests)
RUN mvn clean package -DskipTests

# =============================
# Etapa 2: Crear una imagen ligera para ejecución
# =============================
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copiar el JAR construido desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto que usará la aplicación
EXPOSE 8080

# Render usa PORT como variable de entorno, Spring Boot debe respetarla
ENV PORT=8080
# Pasamos el puerto al arranque de Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT}"]
