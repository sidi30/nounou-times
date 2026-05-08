# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Development with live reload
./mvnw compile quarkus:dev

# Run tests
./mvnw test

# Package as JAR
./mvnw package

# Package as über-JAR
./mvnw package -Dquarkus.package.jar.type=uber-jar

# Build native image (requires GraalVM)
./mvnw package -Dnative

# Build native image via Docker (no local GraalVM needed)
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Tests use `@QuarkusTest` / `@QuarkusIntegrationTest` annotations and run with Maven Surefire.

## Architecture

Quarkus 3 REST API (Java 17) for a French childcare management platform. Three-layer architecture:

- **Resources** (`src/main/java/.../resources/`) — JAX-RS endpoints, token validation via `@HeaderParam("Authorization")`
- **Services** (`src/main/java/.../services/`) — Business logic, `@ApplicationScoped`, `@Transactional`
- **Repositories** (`src/main/java/.../repository/`) — EntityManager-based data access
- **Models** (`src/main/java/.../model/`) — JPA entities extending `PanacheEntity`
- **DTOs** (`src/main/java/.../dto/`) — Request/response objects (e.g., `SignupRequest`, `LoginRequest`, `AbsenceRequest`)

Dependency injection is CDI via Quarkus Arc. Error responses use `ErrorResponse`/`SuccessResponse` DTOs consistently.

## Domain Model

Core entities and their relationships:

- **Utilisateur** — base user (parent or nounou); fields: email, password, civilité, typeUtilisateur
- **Nounou** — childcare provider; linked to a parent `Utilisateur`
- **Enfant** — child; linked to a parent `Utilisateur`
- **Garde** — care session linking `Enfant` ↔ `Nounou` with date/time
- **Absence** — time-off record; links a `Nounou` and optional `remplacant` (replacement)
- **Evenement** — base event entity; `HeuresSupplementaires` extends it
- **HeuresSupplementaires** — extra hours with `Duration` and a validation flag
- **FicheDePaie** — payroll record per `Nounou`; fields: heuresNormales, heuresSupplementaires, montantTotal, tauxHoraire
- **RapportMensuel** — monthly summary per `Utilisateur`; fields: mois, heuresTotales, montantTotal

## API Routes

Main resource prefix is `/api/nounous`. Notable endpoints:

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/nounous/signup` | Nounou registration |
| POST | `/api/nounous/signup/page-emploi` | Register via employment portal |
| POST | `/api/nounous/login` | Authentication, returns token |
| GET | `/api/nounous/enfants` | List children |
| POST | `/api/nounous/enfants/invitation` | Send parent invitation |
| GET | `/api/nounous/garde/jour` | Today's care sessions |
| GET | `/api/nounous/garde/historique` | Historical care sessions |
| POST/GET | `/api/nounous/absences` | Declare / list absences |
| GET | `/api/nounous/evenements` | List events |
| POST | `/api/nounous/imprevus` | Report unexpected event |
| PATCH | `/api/nounous/evenements/{id}/accept\|refuse\|reprogrammer` | Event lifecycle actions |
| GET | `/api/nounous/rapports` | Monthly reports |
| GET | `/api/nounous/rapports/download` | PDF download |
| GET | `/api/nounous/fiche-paie` | Payroll details |

Other top-level resources: `/utilisateurs`, `/gardes`, `/enfants`, `/absences`, `/evenements`, `/heures-supplementaires`, `/fiches-de-paie`, `/rapports`.

## Authentication

Tokens are generated on signup/login and stored in an in-memory `HashMap` inside `NounouService`. They are validated per-request via `nounouService.isValidToken()`. Tokens are lost on server restart and won't work in multi-instance deployments.

## Database

PostgreSQL, configured in `src/main/resources/application.properties`. Schema is rebuilt on every start (`hibernate-orm.database.generation=drop-and-create`) — do not point at a database with persistent data. Mailer is mocked in dev mode (`quarkus.mailer.mock=true`).

## Known Inconsistencies

- `NounouService.signup()` sets fields (prenom, telephone, adresse, pageEmploiId) that do not exist on the `Nounou` entity.
- `NounouRepository` is called with `findByEmail()` and `findByPageEmploiId()` methods that are not implemented.
- Some service calls reference entity fields or relationships not present in the model classes — check models carefully before writing new service logic.
- `MyEntity` / `MyEntityResource` are scaffolding leftovers with no business logic.
