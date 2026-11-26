# Atelier 7 — Tâches planifiées (`@Scheduled`)

Package `tn.esprit.autoloc.scheduler`.

| Classe | Rôle |
|---|---|
| `SchedulingConfig` | `@Configuration` + `@EnableScheduling` : active les annotations `@Scheduled` |
| `ContratSchedulerService` | repère chaque jour ouvré les contrats qui arrivent à échéance sous 48 h |
| `VehiculeSchedulerService` | clôture chaque nuit les locations terminées et libère les véhicules |

Format d'un cron Spring : `seconde minute heure jour-du-mois mois jour-de-la-semaine`. Les deux tâches utilisent
le fuseau `Africa/Tunis`.

## 1. Contrats arrivant à échéance — `ContratSchedulerService`

```java
@Scheduled(cron = "${autoloc.scheduler.contrats.cron:0 0 7 * * MON-FRI}", zone = "Africa/Tunis")
public void identifierContratsEcheanceSous48h()
```

- **Quand** : du lundi au vendredi à 7h00 (`0 0 7 * * MON-FRI`).
- **Échéance d'un contrat** : le contrat n'a pas de date de fin propre ; son échéance est la **date de fin de sa
  réservation**. Un contrat est signalé si `aujourd'hui <= dateFin <= aujourd'hui + 2 jours` (48 h).
- Les réservations `ANNULEE` ou `TERMINEE` sont ignorées.
- **Résultat** : un message `WARN` par contrat dans les logs (identifiant, date de fin, client, immatriculation) ou un
  message `INFO` s'il n'y en a aucun.
- Requête : `IContratRepository.findEcheanceEntre(debut, fin, statutsExclus)` (JPQL avec `join fetch`, une seule requête).

## 2. Fin de location — `VehiculeSchedulerService`

```java
@Scheduled(cron = "${autoloc.scheduler.vehicules.cron:0 0 1 * * *}", zone = "Africa/Tunis")
public void cloturerLocationsTerminees()
```

- **Quand** : tous les jours à 1h00 (`0 0 1 * * *`).
- **Location terminée** : réservation `CONFIRMEE` dont la `dateFin` est strictement antérieure à aujourd'hui.
- **Actions**, dans une seule transaction :
  1. la réservation passe au statut `StatutReservation.TERMINEE` ;
  2. le véhicule repasse à `StatutVehicule.DISPONIBLE`, sauf s'il est en `MAINTENANCE` ou s'il a une autre location
     confirmée en cours aujourd'hui.
- **Résultat** : un message `INFO` par réservation clôturée, puis un récapitulatif (locations clôturées, véhicules libérés).
- Requêtes : `IReservationRepository.findByStatutAndDateFinBefore` et `existsLocationEnCours`.

## 3. Comment les tester sans attendre 1h ou 7h

Le cron est lu dans `application.properties` s'il y est défini, sinon la valeur par défaut de l'annotation s'applique.
Pour une démonstration, ajoute temporairement dans `src/main/resources/application.properties` (exécution toutes les
30 secondes) :

```properties
autoloc.scheduler.contrats.cron=0/30 * * * * *
autoloc.scheduler.vehicules.cron=0/30 * * * * *
```

puis relance l'application et regarde la console. **Retire ces deux lignes ensuite** pour revenir aux horaires demandés.

**Scénario contrats (échéance sous 48 h)** — avec Postman, `POST /api/reservations/avec-contrat` :

```json
{ "dateDebut": "2026-11-20", "dateFin": "2026-11-27", "statut": "CONFIRMEE", "idClient": 1, "idVehicule": 1,
  "contrat": { "dateSignature": "2026-11-19", "valide": true } }
```

Adapte `dateFin` à demain ou après-demain. Au tick suivant, les logs affichent `[Echeances] ... Contrat ...`.

**Scénario fin de location** :

1. `PUT /api/vehicules/{id}` avec `"statut": "LOUE"` (le véhicule est loué) ;
2. `POST /api/reservations` avec une `dateFin` passée (ex. `2026-11-10`), `"statut": "CONFIRMEE"`, pour ce véhicule ;
3. au tick suivant, les logs affichent `[Locations] Reservation ... terminee ... vehicule ... -> DISPONIBLE` ;
4. `GET /api/reservations/{id}` renvoie `"statut": "TERMINEE"` et `GET /api/vehicules/{id}` renvoie `"statut": "DISPONIBLE"`.
