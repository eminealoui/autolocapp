# Atelier 4 — Notes sur la couche Service, IoC et injection de dépendances

## A. Modes d'injection comparés

Expérience menée sur `ContratServiceImpl` (étape 2) : remplacement temporaire de
`@RequiredArgsConstructor` + champ `final` par une injection par attribut
(`@Autowired private IContratRepository contratRepository;`), puis retour en arrière.

| Critère | Injection par constructeur (`@RequiredArgsConstructor` + `final`) | Injection par attribut (`@Autowired` sur le champ) |
|---|---|---|
| Champ `final` possible ? | Oui — affecté une seule fois dans le constructeur généré par Lombok. | Non — le champ doit rester modifiable pour que Spring l'affecte après construction. |
| Dépendance visible dans la signature de la classe ? | Oui — le constructeur `ContratServiceImpl(IContratRepository)` liste explicitement tout ce dont la classe a besoin. | Non — rien dans la signature publique n'indique la dépendance ; il faut lire le corps de la classe. |
| Classe utilisable avec `new` hors conteneur, sans dépendance `null` ? | Oui — `new ContratServiceImpl(unRepo)` donne toujours un objet valide et complet (utile en test unitaire, sans Spring). | Non — `new ContratServiceImpl()` compile mais `contratRepository` reste `null` tant que rien ne l'affecte : objet partiellement construit, `NullPointerException` au premier appel. |
| Signalé par SonarQube for IDE ? | Non. | Oui — règle **java:S6813** *"Remove this field injection and use constructor injection instead"*, une occurrence par champ `@Autowired`. |

**Conclusion :** l'injection par constructeur est la convention retenue pour les neuf
services AutoLoc (champs `final`, `@RequiredArgsConstructor`).

## B. Services — dépendances injectées

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur | Dépendance obligatoire unique, champ `final`, pas d'état mutable. |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur | Idem — un seul repository nécessaire à toute la logique métier. |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur | Idem. |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur | Idem. |
| `ClientServiceImpl` | `IClientRepository` | Constructeur | Idem. |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur | Idem ; dépend de l'abstraction, pas de `ClientServiceImpl`/`VehiculeServiceImpl` (pas de dépendance service-à-service). |
| `ContratServiceImpl` | `IContratRepository` | Constructeur | Fil rouge de l'atelier ; `deleteById` s'appuie sur la cascade JPA (`CascadeType.ALL` + `orphanRemoval` sur `Contrat.paiements`), pas sur un appel à `IPaiementService`. |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur | Lecture seule : aucune dépendance à `IContratRepository`, le service ne fait que lire. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur | Dépendance obligatoire unique. |

Tous les champs sont `private final`, typés par l'interface du repository (jamais
l'implémentation Spring Data), et générés par `@RequiredArgsConstructor` — Spring les
injecte automatiquement puisque chaque bean n'a qu'un seul constructeur.

## C. Messages d'erreur du conteneur (étape 3)

**Message 1 — `NoSuchBeanDefinitionException` (bean `IContratService` introuvable pour `ContratController`)**
Deux causes probables côté `ContratServiceImpl` :
1. L'annotation `@Service` a été oubliée (ou mal orthographiée) sur la classe : le scan de
   composants ne la détecte alors pas et aucun bean du type `IContratService` n'existe.
2. La classe est placée hors du package racine scanné (`tn.esprit.autoloc` et ses
   sous-packages) : même annotée, elle n'est jamais vue par `@SpringBootApplication`.

**Message 2 — `NoUniqueBeanDefinitionException` (2 beans `emailNotificateur` / `smsNotificateur`)**
Deux corrections possibles :
- Poser `@Primary` **sur la classe** du bean à utiliser par défaut (ex. `@Primary` sur
  `EmailNotificateur`) : il sera choisi chaque fois qu'aucune précision n'est donnée.
- Poser `@Qualifier("smsNotificateur")` **sur le paramètre du constructeur** du
  consommateur, pour choisir explicitement l'autre bean à cet endroit précis.

**Message 3 — cycle `clientServiceImpl` ↔ `reservationServiceImpl`**
Avec l'injection par constructeur, le conteneur doit disposer de l'objet complet (donc
de sa dépendance) *avant* d'appeler le constructeur. Si A a besoin de B dans son
constructeur et B a besoin de A dans le sien, aucun des deux ne peut jamais être
construit en premier : le cycle est détecté immédiatement au démarrage
(`BeanCurrentlyInCreationException`), il ne peut pas être contourné silencieusement
(contrairement à l'injection par attribut, où Spring peut parfois le résoudre en
retardant l'affectation — masquant le problème de conception plutôt qu'en le
supprimant). Piste de correction : aucun service ne doit dépendre d'un autre service
pour cette relation — extraire la responsabilité commune dans un troisième service (ou
faire en sorte que `ReservationServiceImpl` ne dépende que de `IClientRepository`
plutôt que de `IClientService`, en respectant la couche Repository, et non pas Service
→ Service).

## D. Anomalies SonarQube for IDE

Analyse lancée sur les packages `service` et `exception` (clic droit → *Analyze with
SonarQube for IDE*). Les deux anomalies suivantes sont **provoquées volontairement**
par les expériences demandées dans l'énoncé, puis corrigées :

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| "Remove this field injection and use constructor injection instead" (expérience étape 2, sur `ContratServiceImpl`) | `java:S6813` — l'injection par attribut rend la classe non testable sans conteneur et autorise un objet partiellement construit. | Restauration de `@RequiredArgsConstructor` + champ `final` (constructeur). |
| "Define and throw a dedicated exception instead of using a generic one" (expérience étape 4, `ContratServiceImpl.findById` avec `RuntimeException` à la place de `ResourceNotFoundException`) | `java:S112` — une exception générique ne documente pas l'intention et empêche un appelant de filtrer spécifiquement ce cas. | Restauration de `throw new ResourceNotFoundException("Contrat", id)`. |
| *(troisième anomalie, à confirmer dans votre IDE)* | Les 9 implémentations sont volontairement écrites sans Lombok `@Data`, sans import inutilisé ni code mort ; si SonarQube for IDE relève une anomalie supplémentaire chez vous (duplication de chaîne de caractères, complexité, etc.), ajoutez-la ici avec sa règle et sa correction. | — |

## E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**
   Il n'y en a pas dans le code applicatif. C'est le conteneur Spring
   (`ApplicationContext`, créé par `SpringApplication.run(...)`) qui instancie le bean
   au démarrage, en appelant le constructeur généré par `@RequiredArgsConstructor`,
   après avoir résolu `IContratRepository` (lui-même un proxy généré par Spring Data).

2. **Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?**
   Pour dépendre d'une abstraction plutôt que d'une implémentation concrète (faible
   couplage) : l'implémentation peut changer, être remplacée par un mock en test, ou
   par un proxy, sans toucher au contrôleur.

3. **Pourquoi un service singleton doit-il rester sans état ? Exemple de bug.**
   Une seule instance est partagée par toutes les requêtes/threads. Si elle portait un
   état mutable propre à un appel (ex. un champ `contratCourant` modifié dans
   `create()`), deux requêtes concurrentes écraseraient la valeur l'une de l'autre :
   un utilisateur pourrait se voir répondre avec le contrat créé par un autre
   utilisateur au même instant.

4. **Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?**
   Pour ne recopier que les attributs modifiables et préserver ce que le client ne doit
   pas pouvoir écraser (identifiant, associations, collections gérées comme
   `Contrat.paiements`). Passer directement l'objet reçu à `save` écraserait toutes les
   colonnes avec les valeurs (souvent partielles) envoyées par le client, et avec
   `orphanRemoval`, pourrait supprimer des données existantes (ex. tous les paiements).

5. **`@Component` vs `@Bean` ? `@Primary` vs `@Qualifier` ? Pourquoi `IPaiementService` n'expose-t-il ni création, ni modification, ni suppression ?**
   `@Component` (et ses spécialisations `@Service`/`@Repository`/`@Controller`) annote
   une classe détectée par le scan de composants, qui devient un bean automatiquement.
   `@Bean` annote une méthode de fabrique dans une classe `@Configuration`, utilisée
   quand on ne peut pas annoter la classe elle-même (bibliothèque externe, classe du
   JDK). `@Primary` se pose sur un bean candidat par défaut et résout l'ambiguïté de
   façon globale ; `@Qualifier` se pose au point d'injection et choisit explicitement
   un bean par son nom, localement. `IPaiementService` reste en lecture seule
   (`findById`, `findAll`) car `Paiement` est en relation de composition avec `Contrat`
   (`cascade = CascadeType.ALL`, `orphanRemoval = true`) : un paiement n'a pas de sens
   sans son contrat, donc son cycle de vie (création, modification, suppression) est
   entièrement piloté par `IContratService` — lui donner ses propres méthodes
   d'écriture permettrait de créer des paiements orphelins et romprait cette règle
   métier.
