# Asset Tracker – Android Anwendung

**Asset Tracker** ist eine moderne Android-Anwendung zur effizienten Anlagenverwaltung. Die App basiert vollständig auf **Jetpack Compose** für ein deklaratives UI-Design, nutzt **Firebase** als skalierbares Backend und folgt dem standardisierten **MVVM-Architekturmuster** (Model-View-ViewModel) mit einem reaktiven, unidirektionalen Datenfluss (UDF).

---

## 🛠️ Architektur & Technologie-Stack

* **UI / Design:** Jetpack Compose (Modernes, deklaratives UI), Material Design 3
* **Architekturmuster:** MVVM (Model-View-ViewModel) + UDF (Unidirectional Data Flow)
* **Backend / Cloud:** Firebase Authentication (Benutzerverwaltung), Cloud Firestore (Echtzeit-Datenbank)
* **Hintergrundverarbeitung:** WorkManager & Android Notification System
* **Navigation:** Jetpack Navigation Compose (`NavHost`)
* **Lokale Persistenz:** SharedPreferences (für Benutzereinstellungen)

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio:** Ladybug (2024.2.1) oder neuer empfohlen.
* **JDK:** Version 17.
* **Firebase:** Ein aktives Firebase-Projekt mit aktivierter E-Mail/Passwort-Authentifizierung und Cloud Firestore.

### Setup
1. **Repository klonen:**
   ```bash
   git clone https://github.com/LiyuWu1976/assettracker.git
   ```
2. **Firebase Konfiguration:**
   * Laden Sie die `google-services.json` aus Ihrer Firebase-Konsole herunter.
   * Platzieren Sie die Datei im Verzeichnis `app/`.
3. **Gradle Sync:**
   * Öffnen Sie das Projekt in Android Studio.
   * Führen Sie einen "Gradle Sync" durch, um alle Abhängigkeiten zu laden.

### Run
1. Schließen Sie ein physisches Android-Gerät an oder starten Sie einen Emulator (API 24+).
2. Klicken Sie auf **Run 'app'** in Android Studio.

---

## 📂 Project Structure

Die Codebasis ist modular nach Verantwortlichkeiten organisiert:

* **`com.fh.msd.assettracker`**
    * **`model/`**: Datenklassen (z. B. `Asset`, `User`) und Firestore-Mapping.
    * **`viewmodel/`**: MVVM-ViewModels zur Verwaltung des UI-Status und der Firebase-Interaktion.
    * **`composables/`**: Wiederverwendbare UI-Komponenten (z. B. `DropdownField`, `TextFields`).
    * **`screen/` / `ui/`**: Vollständige Bildschirm-Layouts (Screens) für die App.
    * **`worker/`**: Hintergrund-Tasks (WorkManager) zur Garantieüberprüfung.
    * **`utils/`**: Hilfsklassen für Benachrichtigungen (`NotificationHelper`) und Validierung.
    * **`constants/`**: Zentrale Definitionen von Firebase-Sammlungsnamen.

---

## 📦 Kernfunktionsmodule

### A. Benutzerauthentifizierung
* **Funktionen:** Anmeldung (Login), Registrierung, Passwortwiederherstellung.
* **Model:** `FirebaseConstants` (Zentrale Definition von Sammlungs- und Feldnamen).
* **Views (Screens):** `LoginScreen`, `RegisterScreen`, `ForgotPasswordScreen`.
* **ViewModel:** `AuthViewModel` (Kapselt die Interaktionen mit `FirebaseAuth`).

### B. Assetanzeige & -filterung
* **Funktionen:** Echtzeit-Anzeige der Assetliste, Volltextsuche nach Namen, Filterung nach Kategorien (z. B. Software, Computer).
* **Model:** `Asset` (Entitätsklasse mit Eigenschaften wie ID, Name, Preis, Kategorie, Status, Garantiezeitraum etc.).
* **Views (Screens):** `AssetCollectionScreen` (Hauptübersicht).
* **ViewModel:** `AssetViewModel` (Abruf der Listen), `CategoryViewModel` (Verwaltung der Kategorieliste).

### C. Asset-Bearbeitung (CRUD)
* **Funktionen:** Neue Anlagen hinzufügen, bestehende Daten bearbeiten, Anlagen löschen, Ablaufdatum der Garantie über einen nativen Datumsauswahldialog (DatePicker) festlegen.
* **Model:** `Asset`.
* **Views (Screens):** `AddAssetScreen`, `EditAssetScreen`.
* **ViewModel:** `AddAssetViewModel`, `EditAssetViewModel`.

### D. Systemeinstellungen & Personalisierung
* **Funktionen:** Dynamische Sprachumschaltung (English / Deutsch), Tag-/Nachtmodus (Dark Mode Toggle).
* **Model:** `SharedPreferences` zur persistenten Speicherung der Benutzerkonfiguration.
* **Views (Screens):** Setup-Menü (TopAppBar) integriert im `AssetCollectionScreen`.
* **ViewModel:** `SettingsViewModel`.

### E. Hintergrundbenachrichtigungen (Garantie-Checker)
* **Funktionen:** Automatische Hintergrundprüfung des Garantie-Ablaufdatums. Sendet eine Systembenachrichtigung exakt einen Tag vor Ablauf der Garantie.
* **Technologie:** `WorkManager` (für periodisch geplante Aufgaben), `NotificationHelper` (Kapselung der Systembenachrichtigungen).

---

## 🔄 Datenflussanalyse (Unidirektionaler Datenfluss / UDF)

Die Anwendung implementiert einen strikten reaktiven Datenfluss in Kombination mit der Firebase-Echtzeitüberwachung:

### 1. Vom Bildschirm (View) zu Firestore (Schreibzugriff)
* **Benutzereingabe:** Der Benutzer füllt ein Formular im `AddAssetScreen` aus und klickt auf „Speichern“.
* **Auslösen des ViewModels:** Der Bildschirm ruft `AddAssetViewModel.saveAsset(...)` auf.
* **Datenzuordnung:** Das ViewModel ordnet die eingegebenen Zeichenketten und Zahlen `Asset`-Objekten zu.
* **Firebase Write:** Das ViewModel ruft die Firestore-API auf (z. B. `db.collection("assets").add(asset)`).
* **Statusrückmeldung:** Das ViewModel aktualisiert einen `saveSuccess`-StateFlow; der Bildschirm erkennt die Statusänderung, benachrichtigt den Benutzer per Toast und kehrt zur vorherigen Seite zurück.

### 2. Von Firestore zum Bildschirm (Lesen)
* **Abonnieren von Listenern:** Bei der Initialisierung richtet `AssetViewModel` einen Firestore-Listener vom Typ `addSnapshotListener` ein.
* **Datentransformation:** Immer wenn sich Daten in der Firestore-Cloud ändern (hinzugefügt, gelöscht oder modifiziert), empfängt der Listener einen `QuerySnapshot` und transformiert ihn in eine `List<Asset>`.
* **Statusaktualisierung:** Die transformierte Liste wird an die `_assets` des ViewModels übertragen (`MutableStateFlow`).
* **UI-Rendering:** `AssetCollectionScreen` überwacht diesen Ablauf mit `collectAsState()`. Sobald die Daten aktualisiert werden, löst Compose die Rekomposition aus, und die Liste auf dem Bildschirm wird automatisch aktualisiert, ohne dass der Benutzer manuell nach unten ziehen muss.

---

## 🏗️ Zusammenfassung der architektonischen Highlights

* **Entkopplung:** Routen werden einheitlich über `NavHost` verwaltet. Bildschirme übergeben keine komplexen Objekte direkt, sondern Parameter über IDs oder teilen ViewModel-Instanzen.
* **Responsive Designs/Sprachen:** Der globale Status wird über das `SettingsViewModel` verwaltet. Beim Sprachwechsel ändert `AppCompatDelegate` dynamisch das Gebietsschema; beim Designwechsel erkennt die Benutzeroberfläche automatisch den Dunkelmodus und wendet das Farbschema neu an.
* **Robustheit:** Alle UI-Texte werden in `strings.xml` (zweisprachig Englisch und Deutsch) extrahiert, und die Farben werden einheitlich über `ui.theme` verwaltet. Dies vermeidet Hardcoding und vereinfacht die spätere Wartung.
