# E-Bank Microservices

Projet de banque électronique construit autour de services Spring Boot indépendants. Il regroupe la gestion des clients et des comptes, la découverte des services, une passerelle API, un chatbot connecté aux données bancaires et une interface web Angular.

> Le projet est en cours de développement. Ce README décrit les fonctionnalités et la configuration présentes dans le dépôt ; certaines intégrations restent à finaliser.

## Fonctionnalités

- Gestion des clients : création, consultation d'un client et liste des clients.
- Gestion des comptes bancaires : création d'un compte pour un client existant, consultation d'un compte et liste des comptes.
- Communication entre les services de comptes et de clients via OpenFeign, avec un circuit breaker Resilience4j et une réponse de secours.
- Découverte des services avec Netflix Eureka.
- Serveur MCP dans les services clients et comptes, afin d'exposer leurs opérations au chatbot.
- Assistant conversationnel basé sur Google Gemini, accessible par HTTP et via des bots Telegram et Discord.
- Interface Angular contenant une page de consultation des comptes.
- Bases H2 en mémoire pour les données clients et comptes.

## Architecture

```text
frontend (Angular :4200)
    └── API Gateway (:9999) ── routes à configurer

discovery-service (Eureka :8761)
    ├── customer-service (:8056)
    ├── ebank-service (:8057) ── Feign ──> customer-service
    ├── gateway-service (:9999)
    └── ebank-bot (:8058)
          ├── Google Gemini
          ├── MCP client ──> customer-service et ebank-service
          ├── Telegram
          └── Discord
```

| Répertoire | Rôle |
| --- | --- |
| `custumer-service/` | API REST des clients, serveur MCP et stockage H2. Le nom du répertoire est orthographié ainsi dans le dépôt. |
| `ebank-service/` | API REST des comptes, serveur MCP, stockage H2 et appel au service clients. |
| `discovery-service/` | Registre Eureka sur le port `8761`. |
| `gateway-service/` | Passerelle Spring Cloud Gateway sur le port `9999`, avec configuration CORS et découverte activée. |
| `ebank-bot/` | API de chat, agent Gemini et intégrations Telegram/Discord. |
| `frontend/` | Application Angular avec une vue des comptes. |
| `pom.xml`, `src/` | Projet Spring Boot racine séparé ; le POM racine n'est pas un agrégateur Maven des services. |

Les cinq services backend sont des projets Maven autonomes, chacun avec son propre wrapper `mvnw` / `mvnw.cmd`.

## Technologies

- Java 21
- Spring Boot 4.1.1
- Spring Cloud 2025.1.3 : Eureka, Gateway, OpenFeign et circuit breaker Resilience4j
- Spring AI 2.0.1 et Google Gemini
- Spring MVC, Spring Data JPA, H2 et serveurs MCP
- Angular 22, TypeScript 6, RxJS et Bootstrap 5
- Bots Telegram et Discord
- Maven Wrapper et npm 10.9.9

## Prérequis

- JDK 21
- Node.js et npm (version npm déclarée dans `frontend/package.json` : `10.9.9`)
- Git

Maven n'a pas besoin d'être installé séparément : chaque projet backend fournit son wrapper.

## Démarrage local

### Services backend

Lancer chaque commande dans un terminal distinct, depuis la racine du dépôt. Démarrer Eureka avant les services qui s'y enregistrent :

```bash
cd discovery-service
./mvnw spring-boot:run
```

```bash
cd custumer-service
./mvnw spring-boot:run
```

```bash
cd ebank-service
./mvnw spring-boot:run
```

```bash
cd gateway-service
./mvnw spring-boot:run
```

```bash
cd ebank-bot
./mvnw spring-boot:run
```

Sous Windows, remplacer `./mvnw` par `.\mvnw.cmd`.

| Service | URL locale | Démarrage |
| --- | --- | --- |
| Eureka | `http://localhost:8761` | Requis pour les services utilisant la découverte |
| Service clients | `http://localhost:8056` | Après Eureka |
| Service comptes | `http://localhost:8057` | Après Eureka et le service clients |
| Gateway | `http://localhost:9999` | Après Eureka |
| Chatbot | `http://localhost:8058` | Requiert les clés de configuration ci-dessous |

Les services clients et comptes désactivent Spring Cloud Config et utilisent H2 en mémoire. Aucun serveur de configuration ou serveur de base de données externe n'est nécessaire pour ce démarrage. Au lancement, le service clients crée trois clients de démonstration ; le service comptes crée ensuite quatre comptes pour chacun des clients d'identifiants 1 à 3. Les types et soldes de ces comptes sont générés au démarrage. Ces données en mémoire ne sont pas persistées et sont recréées à chaque redémarrage.

### Chatbot et variables de configuration

Le service `ebank-bot` nécessite les propriétés suivantes, configurées via l'environnement ou la configuration Spring :

- `GEMINI_API_KEY` : clé d'accès Google Gemini.
- `DISCORD-TOKEN` : jeton du bot Discord.
- `TELEGRAM-TOKEN` : jeton du bot Telegram.

Ne pas placer de véritables clés ou jetons dans le dépôt. Le chatbot se connecte aux serveurs MCP des services clients (`http://localhost:8056/mcp`) et comptes (`http://localhost:8057/mcp`). L'agent conserve le contexte des conversations selon un identifiant de conversation ; les bots utilisent respectivement l'identifiant du chat Telegram et celui du canal Discord.

### Frontend Angular

```bash
cd frontend
npm install
npm start
```

Ouvrir `http://localhost:4200`. La route Angular `/accounts` affiche la liste des comptes et demande les données à `http://localhost:9999/EBANK-SERVICE/accounts`.

**À noter :** le fichier de configuration du gateway ne définit actuellement aucune route active : les exemples de routes sont commentés. Le routage nécessaire au frontend doit donc être configuré dans `gateway-service` avant que la page puisse récupérer les comptes via cette URL.

## API REST

### Clients

Base URL : `http://localhost:8056`

| Méthode | Chemin | Description |
| --- | --- | --- |
| `GET` | `/customers` | Retourne la liste des clients. |
| `GET` | `/customers/{id}` | Retourne un client par son identifiant. |
| `POST` | `/customers` | Crée un client à partir de son nom et de son adresse e-mail. |

Exemple :

```bash
curl -X POST http://localhost:8056/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Samir Benali","email":"samir@example.com"}'
```

### Comptes bancaires

Base URL : `http://localhost:8057`

| Méthode | Chemin | Description |
| --- | --- | --- |
| `GET` | `/accounts` | Retourne la liste des comptes. |
| `GET` | `/accounts/{id}` | Retourne un compte par son identifiant, avec les données du client associées. |
| `POST` | `/accounts` | Crée un compte pour un client existant. |

Exemple :

```bash
curl -X POST http://localhost:8057/accounts \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"balance":1500.00,"accType":"CURRENT_ACCOUNT"}'
```

L'identifiant du compte et sa date de création sont générés par le service. La création vérifie le client via `customer-service`. Si cet appel échoue, le circuit breaker peut fournir un client de secours ; la création ne constitue donc pas une preuve que le client existe réellement.

### Chat

Base URL : `http://localhost:8058`

| Méthode | Chemin | Description |
| --- | --- | --- |
| `GET` | `/chat?query=...&conversationId=...` | Envoie une question à l'agent et retourne une réponse texte. `query` vaut `Bonjour` par défaut et `conversationId` vaut `user-1` par défaut. |

L'agent est destiné aux questions sur les clients et les comptes à l'aide des outils MCP disponibles. Les bots Telegram et Discord relaient également les messages à cet agent ; le bot Telegram accepte aussi les photos accompagnées d'une légende ou d'une question.

## Compilation et tests

Depuis le répertoire du service concerné :

```bash
./mvnw clean verify
```

Sous Windows :

```powershell
.\mvnw.cmd clean verify
```

Pour le frontend :

```bash
cd frontend
npm run build
npm test
```

## Limites actuelles

- Les données des services clients et comptes sont conservées en mémoire seulement.
- La passerelle n'a pas encore de définition de routes active ; configurez-la avant de l'utiliser comme point d'entrée API ou avec le frontend.
- Le frontend présente actuellement une page de consultation des comptes ; il ne propose pas encore de parcours complet de gestion bancaire.
- Les API n'implémentent pas encore les opérations de dépôt, retrait ou virement.
- L'authentification et l'autorisation ne sont pas implémentées.
- Le circuit breaker du service comptes utilise un objet client de secours en cas d'échec du service clients.
