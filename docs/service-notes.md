# Notes de service — autoloc-api

## A. Modes d'injection comparés : constructeur vs attribut

| Critère | Injection par constructeur | Injection par attribut (`@Autowired` sur le champ) |
|---|---|---|
| Champ `final` possible | Oui — le champ est assigné une seule fois à la construction | Non — le champ est rempli après l'instanciation |
| Dépendance visible dans la signature | Oui — le constructeur montre exactement ce dont la classe a besoin | Non — les dépendances sont cachées dans le corps de la classe |
| Utilisable avec `new` hors conteneur, sans dépendance null | Oui — `new AgenceServiceImpl(repository)` fonctionne dans un test, la dépendance est obligatoire | Non — le champ reste `null` tant que le conteneur n'a pas injecté, d'où les `NullPointerException` en test unitaire |
| Signalé par SonarQube for IDE (règle S6813) | Conforme | Anomalie : « Field injection is not recommended » |

## B. Services et leurs dépendances

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur | Champs `final` + `@RequiredArgsConstructor`, testable avec `new` |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur | Idem — le repository est indispensable à toute opération |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur | Idem |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur | Idem |
| `ClientServiceImpl` | `IClientRepository` | Constructeur | Idem |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur | Idem |
| `ContratServiceImpl` | `IContratRepository` | Constructeur | Idem |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur | Lecture seule : une seule dépendance, aucune logique d'écriture |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur | Idem |

## C. Messages d'erreur fréquents

### 1. `NoSuchBeanDefinitionException`
**Causes possibles :**
- L'annotation `@Service` (ou `@Component`) est oubliée sur la classe : elle n'est pas enregistrée comme bean.
- La classe se trouve hors du package `tn.esprit.autoloc` (ou d'un sous-package) : le scan de `@SpringBootApplication` ne la voit pas.

### 2. `NoUniqueBeanDefinitionException`
Deux beans du même type sont proposés pour un même point d'injection. **Deux corrections possibles :**
- Poser `@Primary` sur la classe de l'un des deux beans : il devient le candidat par défaut.
- Poser `@Qualifier("emailNotificateur")` sur le paramètre du constructeur : choix explicite du bean à injecter.

### 3. Dépendance circulaire `clientServiceImpl` <-> `reservationServiceImpl`
Impossible à contourner par injection **constructeur** : chaque bean exige l'autre pour être créé, le conteneur ne peut jamais instancier le premier. **Piste :** extraire la responsabilité commune dans un troisième service que les deux invoquent.

## D. Anomalies SonarQube for IDE

| Règle | Explication | Correction |
|---|---|---|
| **S6813** — injection par attribut | Un champ `@Autowired` n'est pas `final`, la dépendance n'est pas visible dans la signature et reste `null` hors conteneur | Passer à l'injection par constructeur : champ `private final` + `@RequiredArgsConstructor` |
| **S112** — exception générique | Lever `new RuntimeException(...)` masque la nature réelle de l'erreur et empêche une gestion ciblée côté web | Créer/relever une exception métier précise (`ResourceNotFoundException`, `IllegalArgumentException`) |
| **Import inutile** | Un import dont le symbole n'est jamais utilisé alourdit le fichier et peut indiquer du code mort | Supprimer la ligne d'import (ex. `java.util.Date` remplacé par `java.time.LocalDate`) |
| **Commentaire de code** | Un commentaire qui décrit le « quoi » sans apporter de sens (ou code commenté = code mort) | Supprimer le code commenté ; ne commenter que le « pourquoi », les Javadoc étant générées sur les API publiques |

## E. Questions de compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**
   Le new n'est écrit nulle part dans notre code. C'est le conteneur Spring qui exécute le constructeur de ContratServiceImpl (généré par `@RequiredArgsConstructor`) au démarrage, grâce à `@Service` et au scan de `tn.esprit.autoloc` par `@SpringBootApplication`. Il lui fournit le proxy de `IContratRepository`.

2. **Pourquoi dépendre de `IContratService` et non de `ContratServiceImpl` ?**
   Faible couplage : le contrôleur ne connaît que l'abstraction. On peut remplacer l'implémentation ou la mocker facilement en test, sans toucher au code appelant.

3. **Pourquoi un singleton doit-il être sans état ? Exemple de bug.**
   Un bean Spring est un singleton partagé par tous les threads. Avec un attribut « contrat courant », deux requêtes simultanées écrasent la valeur de l'une par celle de l'autre : la requête A traite le contrat de la requête B.

4. **Pourquoi `update` charge-t-il l'existant avant `save` ?**
   Un `save` direct sur l'objet reçu écraserait toutes les colonnes (y compris non fournies). Avec `orphanRemoval`, une collection vide ou non chargée serait prise au mot : la collection de paiements du contrat serait vidée.

5. **`@Component` vs `@Bean`, `@Primary` vs `Qualifier`, `IPaiementService` en lecture seule.**
   - `@Component` : classe annotée, découverte par scan de composants ; `@Bean` : méthode dans une `@Configuration`, pour une classe qu'on ne peut pas annoter (clé tierce, constructeur hérité).
   - `@Primary` : candidat par défaut, posé **sur la classe** ; `@Qualifier` : choix explicite, posé **au point d'injection**.
   - `IPaiementService` est en lecture seule car les paiements sont créés à travers le `Contrat` (cascade), le service ne fait que consulter.
