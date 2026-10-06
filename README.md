# AutoLoc API — Atelier 2 : Associations JPA

Projet Spring Boot / Maven / Spring Data JPA / Lombok — UP ASI, ESPRIT.

## 1. Travail demandé

1. Supprimer les tables existantes : `spring.jpa.hibernate.ddl-auto=create`
2. Créer les associations entre les entités (selon le diagramme de classes)
3. Regénérer la base et vérifier le nombre de tables créées
4. Remettre `spring.jpa.hibernate.ddl-auto=update`

## 2. Associations réalisées

| Association UML | Cardinalité | Annotation côté propriétaire (FK) | Annotation côté inverse |
|---|---|---|---|
| Agence — Employe | 1 — * | `Employe.agence` : `@ManyToOne` + `@JoinColumn(agence_id)` | `Agence.employes` : `@OneToMany(mappedBy="agence")` |
| Agence — Vehicule | 1 — * | `Vehicule.agence` : `@ManyToOne` | `Agence.vehicules` : `@OneToMany(mappedBy="agence")` |
| Vehicule — Maintenance | 1 — * | `Maintenance.vehicule` : `@ManyToOne` | `Vehicule.maintenances` : `@OneToMany(mappedBy="vehicule")` |
| Vehicule — Equipement | * — * | `Vehicule.equipements` : `@ManyToMany` + `@JoinTable(vehicule_equipement)` | `Equipement.vehicules` : `@ManyToMany(mappedBy="equipements")` |
| Vehicule — Reservation | 1 — * | `Reservation.vehicule` : `@ManyToOne` | `Vehicule.reservations` : `@OneToMany(mappedBy="vehicule")` |
| Client — Reservation | 1 — * | `Reservation.client` : `@ManyToOne` | `Client.reservations` : `@OneToMany(mappedBy="client")` |
| Reservation — Contrat | 1 — 1 | `Contrat.reservation` : `@OneToOne` + `@JoinColumn(reservation_id, unique)` | `Reservation.contrat` : `@OneToOne(mappedBy="reservation")` |
| Contrat ◆— Paiement | 1 — * (composition) | `Paiement.contrat` : `@ManyToOne` | `Contrat.paiements` : `@OneToMany(mappedBy="contrat", cascade=ALL, orphanRemoval=true)` |

### Justification des choix
- **Côté propriétaire** : dans une relation 1—*, la clé étrangère est toujours du côté `*` (`@ManyToOne`). Le côté `1` utilise `mappedBy` pour ne pas créer de colonne/table en double.
- **ManyToMany** : Vehicule est le côté propriétaire, il déclare la table d'association `vehicule_equipement`.
- **OneToOne** : la clé étrangère est placée dans `contrat` (un contrat est toujours lié à une réservation existante, `unique = true` garantit le 1—1).
- **Composition Contrat ◆ Paiement** : un paiement n'a pas de sens sans son contrat → `cascade = CascadeType.ALL` et `orphanRemoval = true` (supprimer le contrat supprime ses paiements).
- **Lombok** : `@Getter/@Setter` ciblés, pas de `@Data`, pour éviter les boucles infinies `toString/equals/hashCode` sur les relations bidirectionnelles.

## 3. Tables générées

Après démarrage avec `ddl-auto=create`, on obtient **10 tables** :

| # | Table | Clés étrangères |
|---|---|---|
| 1 | agence | — |
| 2 | employe | agence_id |
| 3 | vehicule | agence_id |
| 4 | equipement | — |
| 5 | vehicule_equipement | vehicule_id, equipement_id (table d'association) |
| 6 | maintenance | vehicule_id |
| 7 | client | — |
| 8 | reservation | client_id, vehicule_id |
| 9 | contrat | reservation_id (unique) |
| 10 | paiement | contrat_id |

**9 entités + 1 table d'association (ManyToMany) = 10 tables.**

Vérification dans MySQL :
```sql
USE autoloc_db;
SHOW TABLES;
SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'autoloc_db';  -- 10
```

## 4. Lancement

Le mot de passe MySQL n'est pas écrit en clair : il est lu depuis la variable d'environnement `DB_PASSWORD`
(IntelliJ : Run → Edit Configurations → Environment variables → `DB_PASSWORD=...`).
