# Sistema de Gestión de Inventario

Sistema completo de gestión de inventario de productos con API REST, interfaz web, base de datos PostgreSQL, pruebas automatizadas completas y pipeline de CI/CD.

## Descripción del Proyecto

Este proyecto implementa un sistema integral de gestión de inventario que permite administrar categorías y productos. El sistema incluye:

- **API REST** con arquitectura por capas (controladores, servicios, repositorios, entidades)
- **Interfaz web** moderna y responsiva que consume la API
- **Base de datos PostgreSQL** con migraciones versionadas usando Flyway
- **Suite completa de pruebas**: unitarias, de integración y end-to-end
- **Análisis estático de código** con Checkstyle, PMD y SpotBugs
- **Pipeline de CI/CD** con GitHub Actions

## Tabla de Contenidos

- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Arquitectura](#arquitectura)
- [Base de Datos](#base-de-datos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Ejecución del Proyecto](#ejecución-del-proyecto)
  - [Con Docker](#opción-1-con-docker-recomendado)
  - [Sin Docker](#opción-2-ejecución-local-sin-docker)
- [Pruebas](#pruebas)
- [API Endpoints](#api-endpoints)
- [Pipeline CI/CD](#pipeline-cicd)
- [Análisis Estático](#análisis-estático)
- [Docker](#docker)
- [Estructura del Proyecto](#estructura-del-proyecto)

## Tecnologías Utilizadas

### Backend
- **Java 21**
- **Spring Boot 3.4.0**
  - Spring Web (API REST)
  - Spring Data JPA (ORM)
  - Spring Validation (Validaciones)
- **PostgreSQL** (Base de datos producción)
- **H2** (Base de datos para pruebas)
- **Flyway** (Migraciones de base de datos)
- **Lombok** (Reducción de código boilerplate)

### Frontend
- **HTML5**
- **CSS3**
- **JavaScript** (Vanilla JS)

### Testing
- **JUnit 5** (Framework de pruebas)
- **Mockito** (Mocking)
- **Spring Boot Test** (Pruebas de integración)
- **Selenium WebDriver** (Pruebas E2E)
- **WebDriverManager** (Gestión de drivers)

### Análisis Estático
- **Checkstyle** (Estándares de código)
- **PMD** (Detección de problemas)
- **SpotBugs** (Detección de bugs)

### CI/CD
- **GitHub Actions** (Pipeline de integración continua)
- **Gradle** (Build y gestión de dependencias)

### Containerización
- **Docker** (Containerización de aplicación)
- **Docker Compose** (Orquestación multi-contenedor)
- **Multi-stage builds** (Optimización de imágenes)

## Arquitectura

El proyecto sigue una arquitectura por capas limpia:

```
┌─────────────────────────────────────────┐
│         Capa de Presentación            │
│  (Controllers - API REST Endpoints)     │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Capa de Aplicación              │
│    (Services - Lógica de Negocio)       │
│           (DTOs - Transferencia)        │
│         (Mappers - Conversión)          │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│          Capa de Dominio                │
│      (Entities - Modelos de Datos)      │
│   (Repositories - Acceso a Datos)       │
│     (Exceptions - Excepciones)          │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│       Capa de Infraestructura           │
│    (Database - PostgreSQL/H2)           │
│   (Exception Handlers - Global)         │
└─────────────────────────────────────────┘
```

### Paquetes Principales

- `cue.edu.co.inventariopruebas.domain`: Entidades, repositorios y excepciones de dominio
- `cue.edu.co.inventariopruebas.application`: Servicios, DTOs y mappers
- `cue.edu.co.inventariopruebas.infrastructure`: Controladores REST y manejo de excepciones

## Base de Datos

### Esquema de Base de Datos

#### Tabla: `categories`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | BIGSERIAL | PRIMARY KEY |
| name | VARCHAR(100) | NOT NULL, UNIQUE |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

#### Tabla: `products`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| id | BIGSERIAL | PRIMARY KEY |
| name | VARCHAR(200) | NOT NULL |
| description | TEXT | - |
| price | DECIMAL(10,2) | NOT NULL, CHECK (>= 0) |
| stock | INTEGER | NOT NULL, DEFAULT 0, CHECK (>= 0) |
| category_id | BIGINT | NOT NULL, FOREIGN KEY → categories(id) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

### Migraciones Flyway

Las migraciones se encuentran en `src/main/resources/db/migration/`:

- `V1__create_categories_table.sql`: Crea tabla de categorías
- `V2__create_products_table.sql`: Crea tabla de productos
- `V3__insert_sample_data.sql`: Datos de prueba iniciales

## Instalación y Configuración

### Prerrequisitos

- **Java JDK 21** o superior
- **PostgreSQL 15** o superior
- **Gradle 8.x** (incluido con Gradle Wrapper)
- **Git**

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone <url-del-repositorio>
cd inventario-pruebas
```

2. **Configurar PostgreSQL**

Crear base de datos:
```sql
CREATE DATABASE inventario_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE inventario_db TO postgres;
```

3. **Configurar credenciales de base de datos**

Editar `src/main/resources/application.properties` si es necesario:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inventario_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

4. **Instalar dependencias**
```bash
./gradlew build -x test
```

## Ejecución del Proyecto

### Opción 1: Con Docker (Recomendado)

**Requisito:** Docker Desktop instalado

```bash
# Levantar base de datos + aplicación
docker-compose up -d

# Verificar que esté ejecutándose
docker-compose ps

# Ver logs
docker-compose logs -f
```

La aplicación estará disponible en: `http://localhost:8080`

**Ventajas:**
- ✅ No requiere instalar PostgreSQL localmente
- ✅ Configuración automática de la base de datos
- ✅ Migraciones ejecutadas automáticamente
- ✅ Fácil de limpiar y reiniciar

**Documentación completa:** [DOCKER.md](DOCKER.md)

### Opción 2: Ejecución Local (Sin Docker)

**Requisitos:** Java 21 + PostgreSQL instalado

```bash
# 1. Crear base de datos
createdb inventario_db

# 2. Ejecutar aplicación
./gradlew bootRun
```

La API estará disponible en: `http://localhost:8080`

### Acceder a la Interfaz Web

Abrir en el navegador: `http://localhost:8080`

La interfaz web permite:
- Crear, listar y eliminar categorías
- Crear, listar, actualizar y eliminar productos
- Buscar productos por nombre
- Filtrar productos por categoría

## Pruebas

### Ejecutar Todas las Pruebas

```bash
./gradlew test
```

### Ejecutar Pruebas por Tipo

**Todas las pruebas excepto E2E** (recomendado si no tienes Chrome instalado):
```bash
./gradlew testWithoutE2E
```

**Pruebas Unitarias:**
```bash
./gradlew test --tests "*ServiceTest" --tests "*ControllerTest"
```

**Pruebas de Integración:**
```bash
./gradlew test --tests "*IntegrationTest"
```

**Pruebas E2E** (requiere Chrome instalado):
```bash
./gradlew test --tests "*E2ETest"
```

**NOTA:** Las pruebas E2E requieren Google Chrome instalado en el sistema. Si no tienes Chrome, usa `./gradlew testWithoutE2E` para ejecutar todas las demás pruebas.

### Cobertura de Pruebas

El proyecto incluye:
- **10 pruebas unitarias** (servicios y controladores)
- **8 pruebas de integración** (API + Base de datos)
- **5 pruebas E2E** (flujo completo con Selenium)

Ver documento completo: [PLAN_DE_PRUEBAS.md](PLAN_DE_PRUEBAS.md)

## API Endpoints

### Categorías

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/categories` | Obtener todas las categorías |
| GET | `/api/categories/{id}` | Obtener categoría por ID |
| POST | `/api/categories` | Crear nueva categoría |
| PUT | `/api/categories/{id}` | Actualizar categoría |
| DELETE | `/api/categories/{id}` | Eliminar categoría |

### Productos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/products` | Obtener todos los productos |
| GET | `/api/products/{id}` | Obtener producto por ID |
| GET | `/api/products?categoryId={id}` | Filtrar por categoría |
| GET | `/api/products?search={text}` | Buscar por nombre |
| GET | `/api/products/low-stock?threshold={n}` | Productos con stock bajo |
| POST | `/api/products` | Crear nuevo producto |
| PUT | `/api/products/{id}` | Actualizar producto |
| DELETE | `/api/products/{id}` | Eliminar producto |

### Ejemplos de Uso

**Crear Categoría:**
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Electronics"}'
```

**Crear Producto:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High performance laptop",
    "price": 999.99,
    "stock": 10,
    "categoryId": 1
  }'
```

**Obtener Productos:**
```bash
curl http://localhost:8080/api/products
```

## Pipeline CI/CD

El proyecto incluye un pipeline completo de GitHub Actions que se ejecuta en cada push y pull request.

### Etapas del Pipeline

1. **Checkout del código**
2. **Configuración de Java 25**
3. **Instalación de dependencias**
4. **Ejecución de pruebas unitarias**
5. **Ejecución de pruebas de integración**
6. **Instalación de Chrome** (para pruebas E2E)
7. **Ejecución de pruebas E2E**
8. **Análisis estático con Checkstyle**
9. **Análisis estático con PMD**
10. **Análisis estático con SpotBugs**
11. **Generación de reportes**
12. **Impresión de "OK"** si todo pasa

### Ver el Pipeline

El archivo de configuración está en: `.github/workflows/ci.yml`

## Análisis Estático

### Checkstyle

Verifica estándares de codificación:
```bash
./gradlew checkstyleMain checkstyleTest
```

Configuración: `config/checkstyle/checkstyle.xml`

### PMD

Detecta problemas de código:
```bash
./gradlew pmdMain pmdTest
```

Configuración: `config/pmd/ruleset.xml`

### SpotBugs

Encuentra bugs potenciales:
```bash
./gradlew spotbugsMain
```

Reportes generados en: `build/reports/spotbugs/`

### Ejecutar Todo el Análisis

```bash
./gradlew check
```

## Docker

El proyecto incluye soporte completo para Docker y Docker Compose, lo que facilita la ejecución sin necesidad de instalar dependencias localmente.

### Inicio Rápido con Docker

**1. Levantar toda la aplicación:**
```bash
docker-compose up -d
```

Esto levantará:
- ✅ PostgreSQL en el puerto 5432
- ✅ Aplicación Spring Boot en el puerto 8080
- ✅ Migraciones de Flyway ejecutadas automáticamente

**2. Verificar estado:**
```bash
docker-compose ps
```

**3. Ver logs:**
```bash
docker-compose logs -f
```

**4. Acceder a la aplicación:**
- Interfaz Web: http://localhost:8080
- API: http://localhost:8080/api
- Health Check: http://localhost:8080/actuator/health

**5. Detener servicios:**
```bash
docker-compose down
```

### Características Docker

**Multi-Stage Build:**
- Stage 1: Compilación con Gradle + JDK 21
- Stage 2: Runtime con JRE 21 Alpine (imagen optimizada ~180MB)

**Health Checks:**
- PostgreSQL: Verifica conexión cada 10 segundos
- Aplicación: Verifica endpoint `/actuator/health` cada 30 segundos

**Persistencia:**
- Volumen Docker para datos de PostgreSQL
- Los datos persisten entre reinicios

**Red Privada:**
- Los servicios se comunican en red privada `inventario-network`
- Solo los puertos necesarios están expuestos al host

### Comandos Útiles

**Reconstruir después de cambios:**
```bash
docker-compose up -d --build
```

**Ver logs de un servicio específico:**
```bash
docker-compose logs -f app
docker-compose logs -f postgres
```

**Ejecutar comandos en contenedor:**
```bash
# Acceder a PostgreSQL
docker-compose exec postgres psql -U postgres -d inventario_db

# Acceder a shell de la app
docker-compose exec app sh
```

**Limpiar completamente (incluyendo datos):**
```bash
docker-compose down -v
```

## Estructura del Proyecto

```
inventario-pruebas/
├── .github/
│   └── workflows/
│       └── ci.yml                          # Pipeline de CI/CD
├── config/
│   ├── checkstyle/
│   │   └── checkstyle.xml                  # Configuración Checkstyle
│   └── pmd/
│       └── ruleset.xml                     # Configuración PMD
├── src/
│   ├── main/
│   │   ├── java/cue/edu/co/inventariopruebas/
│   │   │   ├── application/                # Capa de Aplicación
│   │   │   │   ├── dto/                    # DTOs
│   │   │   │   ├── mapper/                 # Mappers
│   │   │   │   └── service/                # Servicios
│   │   │   ├── domain/                     # Capa de Dominio
│   │   │   │   ├── entity/                 # Entidades JPA
│   │   │   │   ├── repository/             # Repositorios
│   │   │   │   └── exception/              # Excepciones de dominio
│   │   │   ├── infrastructure/             # Capa de Infraestructura
│   │   │   │   ├── controller/             # Controladores REST
│   │   │   │   └── exception/              # Manejo global de excepciones
│   │   │   └── InventarioPruebasApplication.java
│   │   └── resources/
│   │       ├── db/migration/               # Migraciones Flyway
│   │       ├── static/                     # Frontend (HTML, CSS, JS)
│   │       │   ├── css/
│   │       │   │   └── styles.css
│   │       │   ├── js/
│   │       │   │   └── app.js
│   │       │   └── index.html
│   │       └── application.properties
│   └── test/
│       ├── java/cue/edu/co/inventariopruebas/
│       │   ├── application/service/        # Pruebas unitarias de servicios
│       │   ├── infrastructure/controller/  # Pruebas unitarias de controladores
│       │   ├── integration/                # Pruebas de integración
│       │   └── e2e/                        # Pruebas E2E
│       └── resources/
│           └── application-test.properties
├── build.gradle.kts                        # Configuración de Gradle
├── settings.gradle.kts
├── Dockerfile                              # Dockerfile para la aplicación
├── docker-compose.yml                      # Orquestación de contenedores
├── .dockerignore                           # Archivos excluidos de Docker build
├── DOCKER.md                               # Guía completa de Docker
├── PLAN_DE_PRUEBAS.md                      # Plan detallado de pruebas
├── QUICKSTART.md                           # Guía de inicio rápido
├── SOLUCIÓN_PROBLEMAS.md                   # Guía de troubleshooting
└── README.md                               # Este archivo
```

## Decisiones Técnicas

### 1. Arquitectura por Capas
Se eligió una arquitectura por capas para:
- Separación clara de responsabilidades
- Facilitar el mantenimiento
- Mejorar la testabilidad
- Seguir principios SOLID

### 2. Spring Boot 3.4.0
Se utilizó esta versión estable para:
- Compatibilidad con Java 21
- Características modernas de Spring
- Soporte a largo plazo
- Ecosistema maduro y estable

### 3. PostgreSQL
Se eligió PostgreSQL por:
- Robustez y confiabilidad
- Soporte completo de ACID
- Excelente rendimiento
- Amplia adopción en la industria

### 4. Flyway para Migraciones
Flyway permite:
- Versionado de base de datos
- Migraciones repetibles
- Rollback controlado
- Integración con CI/CD

### 5. Frontend en Vanilla JavaScript
Se optó por JavaScript puro para:
- Simplicidad
- Sin dependencias adicionales
- Enfoque en funcionalidad sobre diseño
- Facilidad de comprensión

### 6. Selenium para E2E
Selenium ofrece:
- Automatización real del navegador
- Pruebas del flujo completo
- Validación de integración frontend-backend
- Amplio soporte y documentación

### 7. Múltiples Herramientas de Análisis Estático
Se utilizan Checkstyle, PMD y SpotBugs porque:
- Cada herramienta detecta diferentes tipos de problemas
- Cobertura completa de calidad de código
- Cumplimiento de estándares profesionales
