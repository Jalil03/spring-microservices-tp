# 🧩 Spring Cloud Microservices – TP Complet

## 🏗️ Architecture globale

Ce projet met en œuvre une architecture microservices complète avec **Spring Cloud** :
- **Discovery Server (Eureka)** pour la découverte des services
- **API Gateway** pour le routage centralisé
- **Config Server** pour la centralisation des configurations
- **Product-Service**, **Review-Service**, **Recommendation-Service** (services métiers)
- **Product-Composite-Service** (orchestrateur/agrégateur)
- **Authorization-Service** (authentification simple)
- **Rest-Client-App** (client Java pour tester les appels)


---

## ⚙️ Découverte des services (Eureka)

Tous les microservices s’enregistrent automatiquement auprès du **Discovery Server**.  
Tableau des instances enregistrées :  
![Eureka dashboard](images/1.jpg)

> Chaque service affiche son **nom**, son **port** et son **statut** `UP`.

---

## 🚀 Tests API via Postman (via API Gateway)

### 1) ➕ Création d’un produit (POST)
![POST - création produit](images/3.jpg)
Réponse attendue : `✅ Produit créé avec succès !`

### 2) ✏️ Mise à jour d’un produit (PUT) – cas nominal
![PUT - mise à jour OK](images/4.jpg)
Réponse : `✅ Produit mis à jour avec succès !`

### 3) ❌ Mise à jour d’un produit (PUT) – validation KO (>100)
![PUT - erreur de validation](images/5.jpg)
Message : `Le poids du produit ne doit pas dépasser 100 !`

### 4) 🔍 Lecture d’un produit (GET) – agrégation
![GET - récupération agrégée](images/2.jpg)
Le **Product-Composite-Service** agrège les réponses de :
- `Product-Service`
- `Review-Service`
- `Recommendation-Service`

---

## 📊 Monitoring & Metrics (Actuator)

La couche d’observabilité est exposée via **Spring Boot Actuator**.

- Liste des métriques disponibles :  
  ![Actuator metrics](images/6.jpg)

- Nombre de requêtes **GET** traitées par le composite :  
  ![Metrics GET count](images/7.jpg)

- Nombre de requêtes **POST/PUT** traitées par le composite :  
  ![Metrics POST/PUT count](images/8.jpg)

---

## 🔎 Traces distribuées (Zipkin)

Exemple de traces pour `authorization-service` :  
![Zipkin traces](images/9.jpg)

> Chaque requête affiche sa **durée** et ses **spans**, ce qui facilite le diagnostic bout‑en‑bout.

---

## 🧠 Extraits de logs

### Agrégation complète
```
🎯 Requête reçue sur ProductCompositeController (port=9084) pour productId=4
➡ Début de l’agrégation pour productId=4
➡ Appel Product-Service via LoadBalancer pour productId=4
✅ Product récupéré : Capteur de température (poids = 84)
➡ Appel Review-Service via LoadBalancer pour productId=4
📝 Nombre d’avis récupérés : 2
➡ Appel Recommendation-Service via LoadBalancer pour productId=4
💡 Nombre de recommandations récupérées : 0
✅ Agrégat final généré avec succès pour productId=4
✅ Réponse envoyée avec les infos des instances pour productId=4
```

### Cas d’erreurs côté composite
```
✏ Requête PUT reçue pour mise à jour du produit id=4 avec poids=-10
❌ Erreur lors de la mise à jour du produit composite : [400] during [PUT] to [http://PRODUCT-SERVICE/product/4]
✏ Requête PUT reçue pour mise à jour du produit id=4 avec poids=120
❌ Poids invalide (120) - doit être <= 100
```

### Logs du client REST
```
➡ Envoi d'une requête GET vers http://localhost:8060/product-composite/1
✅ Réponse GET : {...}
➡ Envoi d'une requête POST vers http://localhost:8060/product-composite
✅ Réponse POST : ✅ Produit créé avec succès !
➡ Envoi d'une requête PUT vers http://localhost:8060/product-composite/99
✅ Produit mis à jour avec succès !
```

---

🧠 Rest-Client-App – Test automatique des appels REST

L’application Rest-Client-App automatise les tests d’intégration du TP à l’aide d’un client Spring Boot configuré avec RestTemplate :

(images/10.jpg)

Elle envoie successivement des requêtes GET, POST, et PUT vers Product-Composite-Service,
puis affiche dans la console les réponses agrégées provenant des différents microservices.



## 🧩 Centralisation des configurations avec Spring Cloud Config


L’objectif est de mettre en place un **serveur de configuration centralisé** afin que chaque microservice récupère automatiquement ses paramètres (`application.yml`, `ports`, `Eureka URL`, etc.) depuis un **dépôt Git distant** au lieu de les définir localement.

---

### ⚙️ Mise en œuvre – Config Server

Le **Config Server** est configuré dans le service `config-server02`.
Il se connecte directement au dépôt GitHub suivant :  
🔗 [https://github.com/Jalil03/tp-note](https://github.com/Jalil03/tp-note)

Ce dépôt contient tous les fichiers YAML nécessaires à la configuration des microservices :

| Fichier | Rôle |
|----------|------|
| `api-gateway.yml` | Configuration du routage vers les microservices |
| `authorization-service.yml` | Gestion de l’authentification simple |
| `product-service.yml` | Paramètres du service produit |
| `review-service.yml` | Paramètres du service d’avis |
| `recommendation-service.yml` | Paramètres du service de recommandations |
| `product-composite-service.yml` | Service d’agrégation, avec `instance-id`, ports, etc. |

Chaque microservice est donc configuré de manière **centralisée** et **versionnée** sur GitHub.

---

### 📘 Exemple de configuration – `application.yml` du Config Server
![Config Server – application.yml](images/11.jpg)

Dans cet exemple :
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/Jalil03/tp-note
          default-label: main
          clone-on-start: true
          skip-ssl-validation: true
```

➡️ Cela indique au serveur de config de charger tous les fichiers YAML depuis la branche `main` du dépôt GitHub **tp-note**.
Ainsi, dès le démarrage, le Config Server rend les fichiers disponibles via :

```
http://localhost:8888/{application-name}/{profile}
```

Exemples :
- `http://localhost:8888/product-service/default`
- `http://localhost:8888/review-service/default`

---

### 🧠 Avantages obtenus

✅ **Centralisation totale** des paramètres — plus besoin de modifier chaque service manuellement.  
✅ **Versioning Git** — toute modification du YAML est historisée sur GitHub.  
✅ **Flexibilité** — les changements sont propagés automatiquement aux microservices.  
✅ **Cohérence** — les environnements (dev, test, prod) partagent la même base de configuration.

---

### 💡 Démonstration visuelle

Et voici le **fichier `application.yml`** du **Config Server**, connecté à ce dépôt :  
![Config Server – configuration Spring Cloud](images/11.jpg)



## 🧰 Stack & Outils

| Composant | Rôle |
|---|---|
| **Spring Boot** | Framework d’application |
| **Spring Cloud** | Eureka, Config Server, Gateway, OpenFeign |
| **Micrometer & Actuator** | Observabilité / métriques |
| **Zipkin** | Traçabilité distribuée |
| **H2** | Base en mémoire |
| **Maven** | Build & dépendances |
| **Postman** | Tests API |
| **IntelliJ IDEA** | IDE |

---

## 🔗 Références & sources

- Code de ce TP : **ce dépôt** (branche `main`).  
- Nous nous sommes également appuyés sur des éléments provenant/issus de ce repo d’entraînement :  
  👉 https://github.com/Jalil03/tp-note

> Les captures d’écran utilisées dans ce README se trouvent dans le dossier `images/` de ce dépôt.

---

## ✅ Conclusion

Ce TP démontre :
- Une architecture **microservices** complète et **observables** (metrics + traces)
- L’**agrégation** au niveau du composite via **OpenFeign** + **LoadBalancer**
- La **validation** et la **gestion d’erreurs**
- Des **tests automatisés** côté client (Rest‑Client‑App)

---

✳️ *Auteur : Abdeljalil BOUZINE*  
📅 *Dernière mise à jour : 04/11/2025*
