# 🚨 Quick SOS – Application Android d’Urgence

## 📱 Description
**Quick SOS** est une application Android dédiée à la **sécurité personnelle**.  
Elle permet à l’utilisateur d’envoyer rapidement une alerte d’urgence à un contact prédéfini en **un seul clic**, incluant :
- un **SMS avec la localisation GPS**,
- un **appel téléphonique automatique**,
- des alertes visuelles et sonores (flash, vibration, son),
- des **notifications de rappel périodiques**.

L’application est conçue pour être simple, rapide et efficace en situation de danger.

---

## 🎯 Objectifs du projet
- Envoyer rapidement une alerte SOS
- Transmettre automatiquement la localisation GPS
- Lancer un appel téléphonique vers un contact d’urgence
- Offrir des options de personnalisation
- Mettre en pratique les notions du module **Développement Android – M205**

---

## 🧩 Fonctionnalités

### 🔹 Écran de configuration
- Saisie du numéro d’urgence
- Sauvegarde via **SharedPreferences**

### 🔹 Écran principal
- Bouton SOS central
- Envoi automatique :
  - SMS avec lien Google Maps
  - Appel téléphonique
- Flash, vibration et son activables
- Accès aux paramètres

### 🔹 Écran Paramètres
- Activation / désactivation :
  - Flash
  - Vibration
  - Son
  - Notifications de rappel (toutes les 15 minutes)

### 🔹 Notifications
Message de rappel :
> لا تنسى إذا كنت في خطر اضغط على زر سلامتك أولويتنا

---

## 🏗️ Architecture

### Activities
- `SetupActivity`
- `MainActivity`
- `SettingsActivity`

### Workers (WorkManager)
- `SOSWorker` : SMS + appel + localisation
- `ReminderWorker` : notifications périodiques

### Stockage
- **SharedPreferences** :
  - numéro d’urgence
  - préférences utilisateur

---

## 🛠️ Technologies utilisées
- Kotlin
- Android Studio
- Android SDK
- Google Play Services Location
- WorkManager
- Material Design
- Espresso (UI Tests)
- JUnit (Unit Tests)

---

## 🧪 Tests

### Tests UI
- Test de saisie du numéro
- Test du bouton SOS
- Test des Switch (flash, vibration, son, notification)

### Tests unitaires
- Vérification du stockage des préférences
- Validation du comportement logique

---

## 🔐 Permissions
- ACCESS_FINE_LOCATION
- SEND_SMS
- CALL_PHONE
- VIBRATE
- CAMERA
- POST_NOTIFICATIONS

---

## ⚠️ Difficultés rencontrées
- Gestion des permissions Android
- Localisation GPS fiable
- Exécution en arrière-plan
- Compatibilité Android

### Solutions
- Utilisation de **WorkManager**
- Bonne gestion du cycle de vie Android

---

## 📂 Structure du projet
