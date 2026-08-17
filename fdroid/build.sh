#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
BUILD="$ROOT/fdroid-build"
OUT="$ROOT/MagicSwipe-v0.4.1-unsigned.apk"
SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"

if [[ -z "$SDK" || ! -d "$SDK" ]]; then
  echo "ANDROID_SDK_ROOT/ANDROID_HOME is not set to a usable Android SDK." >&2
  exit 1
fi

ANDROID_JAR="$SDK/platforms/android-36/android.jar"
if [[ ! -f "$ANDROID_JAR" ]]; then
  echo "Android API 36 platform not found: $ANDROID_JAR" >&2
  exit 1
fi

BUILD_TOOLS="$(find "$SDK/build-tools" -mindepth 1 -maxdepth 1 -type d -name '36*' -print | sort -V | tail -n 1)"
if [[ -z "$BUILD_TOOLS" ]]; then
  echo "Android Build-Tools 36.x not found." >&2
  exit 1
fi

AAPT2="$BUILD_TOOLS/aapt2"
D8="$BUILD_TOOLS/d8"
ZIPALIGN="$BUILD_TOOLS/zipalign"
for tool in "$AAPT2" "$D8" "$ZIPALIGN"; do
  [[ -x "$tool" ]] || { echo "Required tool not executable: $tool" >&2; exit 1; }
done
command -v javac >/dev/null || { echo "javac is required." >&2; exit 1; }
command -v jar >/dev/null || { echo "jar is required." >&2; exit 1; }

rm -rf "$BUILD" "$OUT"
mkdir -p "$BUILD/gen" "$BUILD/classes" "$BUILD/dex"

"$AAPT2" compile --dir "$ROOT/app/src/main/res" -o "$BUILD/res.zip"
"$AAPT2" link \
  -o "$BUILD/resources-unsigned.apk" \
  -I "$ANDROID_JAR" \
  --manifest "$ROOT/app/src/main/AndroidManifest.xml" \
  --java "$BUILD/gen" \
  --min-sdk-version 26 \
  --target-sdk-version 36 \
  --version-code 9 \
  --version-name 0.4.1 \
  "$BUILD/res.zip"

mapfile -t SOURCES < <(find "$ROOT/app/src/main/java" "$BUILD/gen" -type f -name '*.java' -print | sort)
javac -encoding UTF-8 -source 17 -target 17 -classpath "$ANDROID_JAR" -d "$BUILD/classes" "${SOURCES[@]}"
(
  cd "$BUILD/classes"
  jar cf "$BUILD/classes.jar" .
)
"$D8" --lib "$ANDROID_JAR" --min-api 26 --output "$BUILD/dex" "$BUILD/classes.jar"

# jar records the source file's wall-clock timestamp when adding classes.dex.
# Normalize it so repeated Linux builds and the Windows release build use the
# same ZIP entry timestamp instead of the build time.
touch -t 200001010000.00 "$BUILD/dex/classes.dex"

cp "$BUILD/resources-unsigned.apk" "$BUILD/with-dex.apk"
jar uf "$BUILD/with-dex.apk" -C "$BUILD/dex" classes.dex
"$ZIPALIGN" -f -p 4 "$BUILD/with-dex.apk" "$OUT"

echo "Unsigned F-Droid build: $OUT"
