# Notes sur les repositories

## Choix d'interface

| Interface | Étend | Justification |
| --- | --- | --- |
| IAgenceRepository | JpaRepository<Agence, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible, tri et pagination |
| IEmployeRepository | JpaRepository<Employe, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible, tri et pagination |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible, tri et pagination |
| IEquipementRepository | JpaRepository<Equipement, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible, tri et pagination |
| IClientRepository | JpaRepository<Client, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible, tri et pagination |
| IReservationRepository | JpaRepository<Reservation, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible, tri et pagination |
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible, tri et pagination |
| IPaiementRepository | JpaRepository<Paiement, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible, tri et pagination ; sert à lire les paiements ; la création et le retrait passent par le Contrat (composition, cascade et orphanRemoval) |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible, tri et pagination |

## Points d'attention

- `CrudRepository.findAll()` renvoie un `Iterable`, `JpaRepository.findAll()` renvoie une `List`.
- `deleteAllInBatch()` et `deleteAllByIdInBatch()` contournent le contexte de persistance : ni cascade ni orphanRemoval (risque de paiements orphelins ou d'erreur de clé étrangère).
- `save()` : id null = persist (INSERT), id renseigné = merge (SELECT puis UPDATE).
- `deleteById()` ne lève pas d'exception si l'id n'existe pas (Spring Data 3).

## Anomalies SonarQube for IDE

| Anomalie | Règle / explication | Correction apportée |
| --- | --- | --- |
| Import inutilisé `java.util.Optional` dans IVehiculeRepository | java:S1128 – Unused imports should be removed : l'import n'est référencé nulle part dans le fichier | Supprimer la ligne `import java.util.Optional;` |
| Commentaire `// TODO: ajouter des méthodes de recherche personnalisées` dans IVehiculeRepository | java:S1135 – Track uses of TODO tags : un tag TODO signale du travail non terminé | Retirer le commentaire (ou créer un ticket suivi) |
| Code commenté `// String recherche = "select v from Vehicule v where v.marque = ?1";` dans IVehiculeRepository | java:S125 – Sections of code should not be commented out : du code mort laissé en commentaire | Supprimer le code commenté (le placer dans l'historique Git) |
