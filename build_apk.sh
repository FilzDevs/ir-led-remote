#!/usr/bin/env bash
set -euo pipefail

SDK="${ANDROID_HOME:-/opt/android-sdk-linux}"
BUILD_TOOLS="${BUILD_TOOLS:-$SDK/build-tools/34.0.0}"
PLATFORM="${PLATFORM:-$SDK/platforms/android-34/android.jar}"

if [ ! -f "$PLATFORM" ]; then
    echo "Fehler: Android SDK Platform nicht gefunden unter: $PLATFORM"
    echo "Setze ANDROID_HOME oder öffne den Ordner ir-led-remote in Android Studio."
    exit 1
fi

rm -rf build
mkdir -p build/gen build/obj build/dex

echo "[1/6] Ressourcen kompilieren (aapt2)..."
"$BUILD_TOOLS/aapt2" compile --dir app/src/main/res -o build/res.zip

echo "[2/6] Ressourcen & Manifest verlinken..."
"$BUILD_TOOLS/aapt2" link \
    -o build/unaligned.apk \
    -I "$PLATFORM" \
    --manifest app/src/main/AndroidManifest.xml \
    --min-sdk-version 24 \
    --target-sdk-version 34 \
    --java build/gen \
    --auto-add-overlay \
    build/res.zip

echo "[3/6] Java-Quellcode kompilieren..."
find app/src/main/java build/gen -name "*.java" > build/sources.txt
javac -source 11 -target 11 \
    -Xlint:-options \
    -encoding UTF-8 \
    -cp "$PLATFORM" \
    -d build/obj \
    @build/sources.txt

echo "[4/6] Bytecode zu DEX konvertieren (d8)..."
find build/obj -name "*.class" > build/classes.txt
"$BUILD_TOOLS/d8" \
    --min-api 24 \
    --lib "$PLATFORM" \
    --output build/dex \
    @build/classes.txt

echo "[5/6] classes.dex packen & zipalign..."
cp build/dex/classes.dex .
zip -q -j build/unaligned.apk classes.dex
rm -f classes.dex
"$BUILD_TOOLS/zipalign" -f -p 4 build/unaligned.apk build/aligned.apk

echo "[6/6] APK signieren..."
if [ ! -f build/keystore.jks ]; then
    keytool -genkeypair \
        -keystore build/keystore.jks \
        -alias irledremote \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -storepass irled123 \
        -keypass irled123 \
        -dname "CN=IrLedRemote, OU=Mobile, O=Filzdevs, L=DE, ST=DE, C=DE"
fi

"$BUILD_TOOLS/apksigner" sign \
    --ks build/keystore.jks \
    --ks-key-alias irledremote \
    --ks-pass pass:irled123 \
    --key-pass pass:irled123 \
    --out IrLedRemote.apk \
    build/aligned.apk

"$BUILD_TOOLS/apksigner" verify --verbose IrLedRemote.apk
echo "Build erfolgreich: IrLedRemote.apk"
