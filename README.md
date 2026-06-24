# ms-seguimiento

Microservicio de **Seguimiento** del sistema SPSRT (UNIR, MISSI): asignaciones,
seguimiento diario (bitácora), actividades, variaciones y línea base.

Parte del sistema **SPSRT — Sistema de Planificación y Seguimiento de Recursos Técnicos**.
El stack completo se orquesta desde el repo
[`orquestacion`](https://github.com/tfm-missi-2026/orquestacion).

## Datos del servicio

| | |
|---|---|
| Puerto | **8083** |
| Base de datos | `spsrt_seguimiento` (PostgreSQL 16) |
| Prefijo de tablas | `mss_` |
| Paquete base | `pe.unir.tfm.srp.seguimiento` |
| Stack | Java 21 · Spring Boot 3.5.14 · MyBatis · Flyway · Eureka client |

## URLs útiles (con el stack completo levantado)

- Eureka dashboard — http://localhost:8761
- API Gateway — http://localhost:8080 (`/actuator/health`)
- Swagger — ms-administracion `:8081` · ms-proyectos `:8082` · ms-seguimiento `:8083` (`/swagger-ui.html`)

## Requisitos

- **Docker Desktop 24+** con docker compose v2 (forma recomendada), **o**
- Java 21 + Maven 3.9+ para compilar/ejecutar fuera de contenedor.

## Levantar standalone (solo este servicio + su PostgreSQL)

```bash
cp .env.example .env
docker compose up -d --build
```

Arranca el microservicio + un PostgreSQL propio (sin Eureka). La BD y el usuario se crean
solos con las variables del `.env`; Flyway aplica las migraciones al arrancar.

- API:     http://localhost:8083
- Health:  http://localhost:8083/actuator/health
- Swagger: http://localhost:8083/swagger-ui.html

> Los endpoints de negocio exigen un **JWT** emitido por `ms-administracion`. En standalone
> puedes levantar también `ms-administracion` para obtener el token, o probar
> `/actuator/health` sin autenticación.

## Desarrollo local en IntelliJ (perfil `dev`)

Para iterar rápido sin rebuildear la imagen Docker, corré **solo este servicio** desde
IntelliJ (▶ Run de la clase `@SpringBootApplication`) y dejá su PostgreSQL en Docker:

```bash
cd ../../orquestacion && docker compose up -d postgres
```

El perfil **`dev`** desactiva la exigencia de JWT e inyecta un usuario simulado (admin del
seed, con todos los roles), así probás los endpoints **sin token**. Activalo de una de estas
formas:

- **Run Configuration** → campo *Active profiles*: `dev`, **o**
- VM options: `-Dspring.profiles.active=dev`, **o**
- Variable de entorno: `SPRING_PROFILES_ACTIVE=dev`

Con el perfil activo, pegale directo: `GET http://localhost:8083/api/asignaciones` (sin header
`Authorization`) → responde `200`.

> ⚠️ El perfil `dev` **solo** aplica corriendo así. En Docker / `orquestacion` / producción
> (sin perfil) la seguridad JWT sigue intacta.

## Dentro del stack completo

Para correrlo junto a Eureka, el gateway y los demás microservicios, usa el repo
[`orquestacion`](https://github.com/tfm-missi-2026/orquestacion). Allí entra por el gateway
(`http://localhost:8080/api/...`).

## Endpoints principales

| Recurso | Ruta |
|---|---|
| Asignaciones | `/api/asignaciones` |
| Actividades | `/api/actividades` |
| Bitácora | `/api/bitacora` |
| Línea base | `/api/linea-base` |
| Variaciones | `/api/variaciones` |

## Migraciones

`src/main/resources/db/migration/V*.sql` (Flyway, se aplican al arrancar). El `V1` instala
la extensión `pgcrypto` necesaria para `gen_random_uuid()`.
