# AutoLoc API

Projet Spring Boot / Maven / Spring Data JPA / Lombok — UP ASI, ESPRIT.
API REST de gestion de location de véhicules (agences, véhicules, clients,
réservations, contrats, paiements, maintenances...).

## Architecture (package `tn.esprit.autoloc`)

| Package | Contenu | Atelier |
|---|---|---|
| `domain` | Entités JPA, énumérations, annotations Jackson | 1, 2, 5 |
| `repository` | Interfaces `I...Repository` (`JpaRepository<Entite, Long>`) | 3 |
| `service` | Interfaces `I...Service` + implémentations `...ServiceImpl` | 4 |
| `exception` | `ResourceNotFoundException` | 4 |
| `web.controller` | Contrôleurs REST, `GlobalExceptionHandler`, `OpenApiConfig` | 5 |

## Lancement

1. MySQL démarré en local (base `autoloc_db` créée automatiquement, identifiants dans
   `src/main/resources/application.properties`).
2. `mvn clean spring-boot:run` (ou lancer `AutolocApiApplication` depuis IntelliJ).

## Documentation et tests de l'API

- Swagger UI : http://localhost:8081/api/swagger-ui.html
- OpenAPI JSON : http://localhost:8081/api/v3/api-docs
- Postman : importer `postman/AutoLoc.postman_collection.json` et
  `postman/local.postman_environment.json`, sélectionner l'environnement « local »,
  puis *Run collection* (exécution dans l'ordre).

## Notes des ateliers

- `docs/repository-notes.md` — Atelier 3 (couche Repository)
- `docs/service-notes.md` — Atelier 4 (couche Service, IoC/DI)
- `docs/api-notes.md` — Atelier 5 (API REST, Postman, Swagger)
