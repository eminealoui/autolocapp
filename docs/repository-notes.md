# Atelier 3 — Notes sur la couche Repository (Spring Data JPA)

## 1. Choix d'interface par entite

Toutes les interfaces du package `tn.esprit.autoloc.repository` etendent
`JpaRepository<Entite, Long>` : c'est l'interface complete (CRUD + List +
tri/pagination + flush), retenue pour tout le projet AutoLoc.

| Interface | Etend | Justification |
|---|---|---|
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible. |
| IAgenceRepository | JpaRepository<Agence, Long> | CRUD complet + tri/pagination si besoin plus tard (liste des agences). |
| IEmployeRepository | JpaRepository<Employe, Long> | CRUD complet, meme besoin standard. |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | CRUD complet ; utile pour trier/paginer le catalogue de vehicules. |
| IEquipementRepository | JpaRepository<Equipement, Long> | CRUD complet, entite simple cote proprietaire du ManyToMany. |
| IClientRepository | JpaRepository<Client, Long> | CRUD complet, base pour les reservations. |
| IReservationRepository | JpaRepository<Reservation, Long> | CRUD complet, entite centrale reliant Client et Vehicule. |
| IPaiementRepository | JpaRepository<Paiement, Long> | CRUD complet ; lecture seule en pratique (voir regle ci-dessous). |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | CRUD complet. |

**Regle de composition (Contrat <> Paiement) :** `IPaiementRepository` existe
pour pouvoir *lire* les paiements, mais la creation/suppression d'un paiement
passe toujours par son `Contrat` (cascade = ALL, orphanRemoval = true), pas
par un appel direct a `IPaiementRepository.delete(...)`.

## 2. Comportement observe

- Au demarrage avec les 9 repositories : log `Found 9 JPA repository interfaces`.
- `save()` sur une entite avec id = null -> `INSERT` (persist).
- `save()` sur une entite avec id renseigne -> `SELECT` puis `UPDATE` (merge).
- `deleteById()` sur `JpaRepository` passe par le contexte de persistance :
  la cascade et l'orphanRemoval s'appliquent (verifie sur Contrat -> Paiement
  dans `CrudDemoRunner` : supprimer un contrat genere 2 `delete from paiement`
  puis 1 `delete from contrat`).
- `deleteAllInBatch()` (non utilise ici) contournerait ce contexte : a eviter
  sur Contrat/Paiement, sous peine de laisser des paiements orphelins.

## 3. Anomalies SonarQube for IDE (SonarLint)

Analyse lancee sur tout le projet (clic droit > *Analyze with SonarQube for
IDE*) : **31 problemes trouves dans 10 fichiers**, regroupes en 3 regles
distinctes.

| Anomalie SonarQube for IDE | Regle / explication | Correction apportee |
|---|---|---|
| "Remove this field injection and use constructor injection instead" (9 occurrences, une par `...ServiceImpl.java`) | `java:S6813` — l'injection par `@Autowired` sur un champ rend la classe difficile a tester (impossible d'injecter un mock sans Spring) et autorise un objet partiellement construit. | Remplace par un constructeur explicite qui prend le repository en parametre et l'assigne a un champ `private final`. Spring injecte automatiquement via ce constructeur (pas besoin de `@Autowired` des qu'il n'y a qu'un seul constructeur). |
| "Replace this use of System.out by a logger" (10 occurrences dans `CrudDemoRunner.java`) | `java:S106` — `System.out.println` n'est pas configurable (niveau, format, sortie fichier) et reste actif meme en production. | Remplace par un `Logger` SLF4J (`LoggerFactory.getLogger(CrudDemoRunner.class)`) avec `logger.info("...")`. |
| "Explicitly specify the time zone by passing a ZoneId or a Clock to the .now() method" (6 occurrences dans `CrudDemoRunner.java`) | `java:S8688` — `LocalDate.now()` sans fuseau horaire explicite depend du fuseau du serveur ou tourne l'application ; resultat non reproductible si le serveur change de fuseau. | Remplace par `LocalDate.now(ZoneId.of("Africa/Tunis"))`, fuseau explicite. |

**Non corrigees volontairement :** aucune — les 31 anomalies relevent toutes
de ces 3 regles et ont ete corrigees dans leur integralite.
