# Atelier 5 — Notes sur l'API REST, Postman et Swagger

Configuration : `server.servlet.context-path=/api`, `server.port=8081` (le projet utilise
8081 depuis l'Atelier 1, d'où `baseUrl = http://localhost:8081` dans l'environnement
Postman « local »). Swagger UI : `http://localhost:8081/api/swagger-ui.html`,
description OpenAPI : `http://localhost:8081/api/v3/api-docs`.

Collection Postman : `postman/AutoLoc.postman_collection.json` + environnement
`postman/local.postman_environment.json` (84 requêtes, 10 dossiers, à exécuter dans
l'ordre avec *Run collection* : les identifiants créés sont mémorisés dans
l'environnement et réutilisés par les requêtes suivantes).

## A. Endpoints

Tous les contrôleurs sont dans `web.controller`, dépendent de l'interface `I...Service`
(champ `final`, `@RequiredArgsConstructor`) et n'accèdent jamais à un repository.
Les 404 viennent de `ResourceNotFoundException`, les 400 de `IllegalArgumentException`
(ou d'un JSON illisible), traduits par `GlobalExceptionHandler`.

| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| AgenceController | GET /api/agences | findAll() | 200 | — |
| | GET /api/agences/{id} | findById(id) | 200 | 404 |
| | POST /api/agences | create(agence) | 201 | 400 (id fourni, nom/ville vide) |
| | PUT /api/agences/{id} | update(id, agence) | 200 | 404, 400 |
| | DELETE /api/agences/{id} | deleteById(id) | 204 | 404 |
| EmployeController | GET /api/employes | findAll() | 200 | — |
| | GET /api/employes/{id} | findById(id) | 200 | 404 |
| | POST /api/employes | create(employe) | 201 | 400 (nom/prénom/rôle vide) |
| | PUT /api/employes/{id} | update(id, employe) | 200 | 404, 400 |
| | DELETE /api/employes/{id} | deleteById(id) | 204 | 404 |
| VehiculeController | GET /api/vehicules | findAll() | 200 | — |
| | GET /api/vehicules/{id} | findById(id) | 200 | 404 |
| | POST /api/vehicules | create(vehicule) | 201 | 400 (immatriculation vide, tarif négatif) |
| | PUT /api/vehicules/{id} | update(id, vehicule) | 200 | 404, 400 |
| | DELETE /api/vehicules/{id} | deleteById(id) | 204 | 404 |
| EquipementController | GET /api/equipements | findAll() | 200 | — |
| | GET /api/equipements/{id} | findById(id) | 200 | 404 |
| | POST /api/equipements | create(equipement) | 201 | 400 (libellé vide) |
| | PUT /api/equipements/{id} | update(id, equipement) | 200 | 404, 400 |
| | DELETE /api/equipements/{id} | deleteById(id) | 204 | 404 |
| ClientController | GET /api/clients | findAll() | 200 | — |
| | GET /api/clients/{id} | findById(id) | 200 | 404 |
| | POST /api/clients | create(client) | 201 | 400 (nom/email vide) |
| | PUT /api/clients/{id} | update(id, client) | 200 | 404, 400 |
| | DELETE /api/clients/{id} | deleteById(id) | 204 | 404 |
| ReservationController | GET /api/reservations | findAll() | 200 | — |
| | GET /api/reservations/{id} | findById(id) | 200 | 404 |
| | POST /api/reservations | create(reservation) | 201 | 400 (date de fin < date de début) |
| | PUT /api/reservations/{id} | update(id, reservation) | 200 | 404, 400 |
| | DELETE /api/reservations/{id} | deleteById(id) | 204 | 404 |
| ContratController | GET /api/contrats | findAll() | 200 | — |
| | GET /api/contrats/{id} | findById(id) | 200 | 404 |
| | POST /api/contrats | create(contrat) | 201 | 400 (id fourni, montant négatif) |
| | PUT /api/contrats/{id} | update(id, contrat) | 200 | 404, 400 |
| | DELETE /api/contrats/{id} | deleteById(id) — cascade sur les paiements | 204 | 404 |
| PaiementController | GET /api/paiements | findAll() | 200 | — |
| | GET /api/paiements/{id} | findById(id) | 200 | 404 |
| | POST / PUT / DELETE /api/paiements | aucun (lecture seule) | — | 405 |
| MaintenanceController | GET /api/maintenances | findAll() | 200 | — |
| | GET /api/maintenances/{id} | findById(id) | 200 | 404 |
| | POST /api/maintenances | create(maintenance) | 201 | 400 (date de fin < date de début) |
| | PUT /api/maintenances/{id} | update(id, maintenance) | 200 | 404, 400 |
| | DELETE /api/maintenances/{id} | deleteById(id) | 204 | 404 |

Corps JSON : les identifiants suivent les noms du modèle (`idContrat`, `idAgence`...).
Les objets liés sont désignés par leur identifiant, ex.
`"reservation": { "idReservation": 1 }`, `"agence": { "idAgence": 1 }`.

## B. Annotations Jackson

| Association | Annotation retenue | Justification |
|---|---|---|
| Contrat.paiements ↔ Paiement.contrat | `@JsonManagedReference("contrat-paiements")` sur la liste, `@JsonBackReference("contrat-paiements")` sur `Paiement.contrat` | Seule vraie composition : les paiements sont créés dans le JSON de leur contrat (cascade ALL). Jackson sérialise la liste et, à la lecture, rattache lui-même chaque paiement à son contrat parent. |
| Agence.employes / Agence.vehicules | `@JsonIgnore` | Côté inverse : l'employé/le véhicule est créé seul et désigne son agence (`"agence": {"idAgence": 1}`). |
| Vehicule.maintenances / Vehicule.reservations | `@JsonIgnore` | Côté inverse ; `Maintenance.vehicule` et `Reservation.vehicule` restent sérialisés. |
| Equipement.vehicules | `@JsonIgnore` | Côté inverse du ManyToMany ; `Vehicule.equipements` (propriétaire) reste visible. |
| Client.reservations | `@JsonIgnore` | Côté inverse ; `Reservation.client` reste visible et lisible en POST (pas de `@JsonBackReference`, sinon la réservation serait créée sans client). |
| Reservation.contrat | `@JsonIgnore` | Côté inverse du OneToOne ; `Contrat.reservation` reste visible. |
| Toutes les entités | `@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})` | Une référence chargée en proxy Hibernate (après `save`) provoquerait « No serializer found ... ByteBuddyInterceptor ». |

**`@JsonBackReference` vs `@JsonIgnore`** : les deux empêchent la propriété d'être
écrite en sortie, mais `@JsonBackReference` fonctionne en paire avec
`@JsonManagedReference` : à la lecture, Jackson renseigne automatiquement la référence
vers le parent pour chaque enfant contenu dans le JSON du parent (d'où la cascade
Contrat → Paiements qui fonctionne en un seul POST). `@JsonIgnore` ignore la propriété
dans les deux sens, sans aucun rattachement automatique : adapté quand l'enfant est créé
seul et désigne son parent par identifiant.

## C. Postman et Swagger

- **Postman** sert à **tester** : requêtes rejouables, environnement (`baseUrl`,
  identifiants mémorisés), assertions automatiques (`pm.test`). Exemple : le scénario
  « cascade » crée un contrat avec deux paiements (201, `paiements.length === 2`),
  vérifie que `GET /api/paiements` les contient sans les avoir créés séparément, modifie
  le contrat (paiements conservés), le supprime (204) puis constate le 404 sur
  `GET /api/paiements/{paiementId}` — preuve des cascades PERSIST et REMOVE.
- **Swagger UI** sert à **documenter et explorer** : la documentation est générée depuis
  le code (toujours synchronisée), groupée par `@Tag`, avec les résumés `@Operation`, les
  codes `@ApiResponse` et les schémas déduits des entités. Exemple : sans aucune
  annotation, les endpoints apparaissent déjà ; après annotation, on lit directement que
  `POST /contrats` renvoie 201 ou 400, et l'onglet *Schemas* montre `Contrat` sans le
  champ `contrat` dans `Paiement` (effet de `@JsonBackReference`).

Les deux sont complémentaires : Postman vérifie le comportement, Swagger décrit le contrat.

## D. Anomalies SonarQube for IDE

Analyse : clic droit sur `web.controller` → *Analyze with SonarQube for IDE*.

| Anomalie | Règle / explication | Correction |
|---|---|---|
| « Replace this persistent entity with a simple POJO or DTO object » sur les paramètres `@RequestBody` des méthodes `create`/`update` | `java:S4684` — exposer une entité JPA en entrée permet à un client de renseigner des champs qu'il ne devrait pas contrôler (identifiant, associations : *mass assignment*). | Non corrigée volontairement à ce stade : l'énoncé impose l'exposition directe des entités jusqu'au Chapitre 10 (DTO). Le risque est limité côté service : `create` refuse un identifiant, `update` ne recopie que les attributs modifiables. |
| Injection par attribut (`@Autowired` sur un champ) dans un contrôleur | `java:S6813` — dépendance cachée, champ non `final`, objet partiellement construit hors conteneur. | Évitée dès l'écriture : les 9 contrôleurs utilisent `@RequiredArgsConstructor` + champ `final` typé par l'interface du service. |
| *(à confirmer dans votre IDE)* | Si l'analyse relève une autre anomalie (import inutilisé, chaîne dupliquée...), l'ajouter ici avec sa règle et sa correction. | — |

## E. Questions de compréhension

1. **Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode à appeler ?**
   Le `DispatcherServlet`, point d'entrée unique. Il interroge le `HandlerMapping`, qui a
   indexé au démarrage les `@RequestMapping`/`@GetMapping`... : il choisit la méthode
   d'après le verbe HTTP et l'URL (préfixe de classe + sous-chemin, ex. `GET /contrats/{id}`).

2. **Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de `ContratServiceImpl` ?**
   Pas du repository : le contrôleur ne doit contenir aucune logique métier ni accès aux
   données (règles, garde sur l'id, exception 404 sont dans le service). Pas de
   l'implémentation : dépendre de l'abstraction garde le couplage faible
   (implémentation remplaçable, mockable, proxifiable par Spring).

3. **Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?**
   201 *Created* signale qu'une ressource nouvelle a été créée (corps = la ressource avec
   son id) ; 204 *No Content* signale un succès sans corps à renvoyer. Une ressource
   absente est une erreur du client (mauvais identifiant), pas une panne du serveur : le
   service lève `ResourceNotFoundException`, traduite en 404 par le `GlobalExceptionHandler`
   ; 500 doit rester réservé aux erreurs inattendues côté serveur.

4. **Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST `/api/paiements` ?**
   Paiement est une partie de la composition Contrat ◆ Paiement : il est créé, modifié et
   supprimé via son contrat (cascade ALL, orphanRemoval), et `IPaiementService` est en
   lecture seule. Un POST sur `/api/paiements` reçoit **405 Method Not Allowed** : la route
   existe mais aucun handler n'accepte ce verbe.

5. **Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?**
   Risques : couplage de l'API au schéma de base (tout changement d'entité casse les
   clients), fuite de champs internes, *mass assignment* en entrée, boucles de
   sérialisation et proxies Hibernate (`LazyInitializationException`, `ByteBuddyInterceptor`).
   `Contrat.paiements` est une composition : les paiements sont envoyés **dans** le JSON du
   contrat et doivent être rattachés automatiquement à lui → paire Managed/Back.
   Les véhicules, eux, sont créés **seuls** et désignent leur agence par identifiant : la
   liste `Agence.vehicules` n'a besoin ni d'être écrite ni d'être lue → `@JsonIgnore`.
