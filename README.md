# IR LED Remote (Android)

Eine schlanke, werbefreie Open-Source-Android-App ohne externe Abhängigkeiten zur Steuerung von **RGB-LED-Streifen** über den integrierten **Infrarot-Blaster (IR)** des Smartphones (`ConsumerIrManager`, 38 kHz NEC-Protokoll).

> **Hinweis zur Kompatibilität:** Inoffizielle Open-Source-App, kompatibel mit gängigen 24- und 44-Tasten-IR-LED-Controllern (u. a. Grundig RGB-LED-Streifen und generische NEC-IR-Empfänger). Keine Verbindung zu oder Unterstützung durch die genannten Marken.

## Features
- **3 Fernbedienungs-Profile** (Auswahl wird gespeichert):
  - `24-Tasten (Standard 0xEF00)`
  - `24-Tasten (Alternativ 0xFF00)`
  - `44-Tasten (Standard 0xFF00)` inkl. DIY-Tasten, R/G/B-Feinabstimmung und Jump/Fade-Modi
- **Sofortige IR-Hardware-Erkennung** (`hasIrEmitter()`) und Anzeige des zuletzt gesendeten NEC-Hex-Codes
- **Keine Internet- oder Tracking-Berechtigungen** – benötigt ausschließlich `android.permission.TRANSMIT_IR`

## Voraussetzungen
- Android 7.0 (API 24) oder neuer (getestet bis Android 15/16)
- Smartphone mit eingebautem **IR-Blaster** (z. B. Xiaomi / Redmi / Poco)

## Installation
Die fertige `IrLedRemote.apk` kannst du direkt über die **GitHub Releases** herunterladen und auf deinem Smartphone installieren.

## Selbst bauen

### Option 1: Android Studio
1. Ordner `ir-led-remote` in Android Studio öffnen.
2. **Build → Build Bundle(s) / APK(s) → Build APK(s)** ausführen.

### Option 2: Kommandozeile (`build_apk.sh`)
Mit installiertem JDK und Android SDK (API 34 + Build-Tools 34.0.0):
```bash
ANDROID_HOME=/pfad/zum/android-sdk ./build_apk.sh
```
Erzeugt die signierte `IrLedRemote.apk`.

## Lizenz
[MIT](LICENSE)
