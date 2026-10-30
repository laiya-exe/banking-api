# BANKING-API

> API REST robuste de gestion de comptes bancaires et de virements atomiques, construite avec Spring Boot et orientée production.

---

## 1. Fonctionnalités

Le système expose un ensemble de services financiers critiques :
- **Gestion des comptes** : Création et consultation de comptes bancaires.
- **Opérations courantes** : Dépôts et retraits sécurisés.
- **Moteur de virement** : Transferts de compte à compte atomiques et transactionnels.
- **Audit trail** : Historique complet des transactions par compte.

---

## 2. Stack Technique

- **Langage** : Java 21
- **Framework Core** : Spring Boot 4.1, Spring Data JPA, Bean Validation
- **Bases de Données** : 
  - PostgreSQL (Environnement de production / Dockerized)
  - H2 Database (Environnement de développement local et tests)
- **Tests** : JUnit 5, Mockito, AssertJ
- **Conteneurisation** : Docker & Docker Compose

---

## 3. Démarrage Rapide

### Option 1 : Avec Docker (Recommandé - PostgreSQL)
Lancez l'ensemble de l'architecture (Application + Base de données PostgreSQL) en une seule commande :
```bash
docker compose up --build
```

### Option 2 : Mode Local (Sans Docker - Base H2)
L'application bascule automatiquement sur une base en mémoire H2. 

**Sur Windows (PowerShell/CMD) :**
```cmd
.\mvnw spring-boot:run
```
**Sur Linux / macOS :**
```bash
./mvnw spring-boot:run
```

### Exécution des Tests
Pour lancer la suite de tests unitaires et d'intégration :
```bash
.\mvnw test # (ou .\mvnw test sur Windows)
```

---

## Documentation des Endpoints

| Méthode | Chemin | Description | Code Succès |
| :--- | :--- | :--- | :--- |
| **POST** | `/accounts` | Création d'un nouveau compte bancaire | `201 Created` |
| **GET** | `/accounts/{id}` | Consultation du solde et détails d'un compte | `200 OK` |
| **POST** | `/accounts/{id}/deposit` | Dépôt d'argent sur un compte | `200 OK` |
| **POST** | `/accounts/{id}/withdraw` | Retrait d'argent sur un compte | `200 OK` |
| **POST** | `/transfers` | Virement de compte à compte | `201 Created` |
| **GET** | `/accounts/{id}/transactions` | Historique complet des transactions | `200 OK` |

---

## Exemples de Requêtes (cURL)

Ouvrez votre terminal pour interagir avec l'API locale lancée sur le port `8080`.

### 1. Création de comptes (Jean et Paul)
```bash
curl -i -X POST localhost:8080/accounts -H "Content-Type: application/json" -d '{"ownerName": "Jean"}'
curl -i -X POST localhost:8080/accounts -H "Content-Type: application/json" -d '{"ownerName": "Paul"}'
```

### 2. Opérations financières
**Dépôt initial de 10 000 sur le compte de Jean (ID: 1) :**
```bash
curl -i -X POST localhost:8080/accounts/1/deposit -H "Content-Type: application/json" -d '{"amount": 10000}'
```

**Virement de 3 000 depuis le compte de Jean (ID: 1) vers Paul (ID: 2) :**
```bash
curl -i -X POST localhost:8080/transfers -H "Content-Type: application/json" -d '{"sourceAccountId": 1, "destinationAccountId": 2, "amount": 3000}'
```

### 3. Vérification des soldes après transfert
```bash
curl -i localhost:8080/accounts/1
curl -i localhost:8080/accounts/2
```

### 4. Gestion des erreurs standardisée (`ProblemDetail`)
L'API implémente la spécification RFC 9457 pour renvoyer des erreurs claires. Exemple avec un montant de dépôt invalide :
```bash
curl -i -X POST localhost:8080/accounts/2/deposit -H "Content-Type: application/json" -d '{"amount": -5}'
```
**Réponse `400 Bad Request` :**
```json
{
  "title": "Un des champs de la requête est invalide.",
  "status": 400,
  "detail": "La requête contient des champs invalides.",
  "instance": "/accounts/2/deposit",
  "errors": {
    "amount": "Le montant doit être supérieur à 0.01."
  }
}
```

---

## Architecture et Choix Techniques

* **`BigDecimal` pour la précision monétaire** : L'argent étant une donnée critique, l'utilisation de types flottants (`double`, `float`) a été proscrite pour éviter les erreurs d'approximation binaire. `BigDecimal` étant plus exact en base décimale, il est plus adapté.
* **`@Transactional` pour l'atomicité des virements** : Un transfert d'argent exige que le débit du compte A et le crédit du compte B réussissent ensemble. Si l'une des étapes échoue, un mécanisme de *rollback* automatique annule l'intégralité de l'opération en base de données.
* **`@Version` contre les écritures perdues (Optimistic Locking)** : Pour empêcher deux requêtes simultanées de modifier un même solde en se basant sur des données obsolètes, un mécanisme de verrouillage optimiste via un champ de versioning a été mis en place.
* **Encapsulation métier dans l'entité `Account`** : Conformément aux principes de la programmation orientée objet (POO), les règles de gestion (comme l'interdiction d'un solde négatif) sont directement protégées au sein de l'entité (encapsulation).
* **Découplage par DTO et Bean Validation** : L'usage de Data Transfer Objects (DTO) isole le modèle de données de la couche d'exposition API, tout en interceptant et rejetant les requêtes malformées dès l'entrée du contrôleur.
* **Gestion centralisée des exceptions** : Utilisation d'un `@ControllerAdvice` global pour intercepter les exceptions. Cela évite de saturer l'application de blocs `try/catch` répétitifs et garantit un format d'erreur unifié (`ProblemDetail`) pour simplifier l'intégration avec le Frontend.

---

## Couverture des Tests

La logique métier et les scénarios aux limites sont sécurisés par une suite de tests rigoureuse :

| Méthode de Test | Cible de la Vérification |
| :--- | :--- |
| `deposit_shouldIncreaseBalance()` | Confirme que l'opération de dépôt incrémente correctement le solde. |
| `withdraw_shouldThrow_whenBalanceTooLow()` | Valide le blocage et la levée d'une exception en cas de fonds insuffisants lors d'un retrait. |
| `transfer_shouldMoveMoney_whenFundsAreSufficient()` | Vérifie le succès d'un transfert nominal entre deux comptes. |
| `transfer_shouldFail_whenInsufficientFunds()` | S'assure de l'échec d'un virement si le compte source est à sec, avec garantie qu'aucun fonds n'a bougé. |
| `transfer_shouldFail_whenSourceIsDestination()` | Bloque les tentatives de virement d'un compte vers lui-même. |
| `transfer_shouldFail_whenAccountNotFound()` | Valide la levée d'une exception si l'un des comptes cibles n'existe pas. |
| `transactionalTransfert()` | Test d'intégration qui prouve que la base reste intacte après un virement échoué |

---

## Feuille de Route & Améliorations (Roadmap)

Le projet a été conçu selon un découpage modulaire strict, facilitant une transition future vers une architecture microservices. Principaux axes d'amélioration identifiés :

- **Gestion des schémas de BDD** : Remplacer `ddl-auto=update` par un outil de migration de base de données comme **Flyway** ou **Liquibase**.
- **Sécurité des secrets** : Sortir les mots de passe en clair du fichier `docker-compose.yml` pour utiliser des variables d'environnement ou un orchestrateur de secrets (**HashiCorp Vault**).
- **Authentification & Autorisation** : Sécuriser les endpoints de l'API à l'aide de **Spring Security** et d'un fournisseur d'identité OAuth2/OIDC (**Keycloak**).
- **Résilience du réseau (Idempotence)** : Intégrer un système de clés d'idempotence pour éviter qu'une requête de virement rejouée par erreur en raison d'une instabilité réseau ne soit exécutée deux fois.
- **Architecture Orientée Événements (EDA)** : Publier des événements métier dans un broker **Kafka** (ex: `TransferCompletedEvent`) pour notifier d'autres services indépendants (Notifications SMS/Email, Service Anti-fraude).
- **Performance & Observabilité** : Mettre en place la pagination sur l'historique des transactions et ajouter un pipeline CI/CD (GitHub Actions).
