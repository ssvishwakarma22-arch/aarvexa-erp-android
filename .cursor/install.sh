#!/usr/bin/env bash
set -euo pipefail

ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"
SDK_MANAGER="${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager"
PACKAGES=(
  "platform-tools"
  "platforms;android-34"
  "build-tools;34.0.0"
)

if [[ ! -x "${SDK_MANAGER}" ]]; then
  echo "Installing Android SDK command-line tools..."
  sudo mkdir -p "${ANDROID_HOME}/cmdline-tools"
  tmp_zip="$(mktemp)"
  wget -q "https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip" -O "${tmp_zip}"
  sudo unzip -q "${tmp_zip}" -d "${ANDROID_HOME}/cmdline-tools"
  rm -f "${tmp_zip}"
  sudo mv "${ANDROID_HOME}/cmdline-tools/cmdline-tools" "${ANDROID_HOME}/cmdline-tools/latest"
  sudo chown -R ubuntu:ubuntu "${ANDROID_HOME}"
fi

echo "Ensuring Android SDK packages are installed..."
set +o pipefail
yes | "${SDK_MANAGER}" --sdk_root="${ANDROID_HOME}" "${PACKAGES[@]}" >/dev/null 2>&1 || true
set -o pipefail

export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk-amd64}"
export ANDROID_SDK_ROOT="${ANDROID_HOME}"
export PATH="/opt/gradle-8.7/bin:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${PATH}"

echo "Toolchain versions:"
java -version
gradle --version | head -n 3
"${SDK_MANAGER}" --sdk_root="${ANDROID_HOME}" --list_installed | grep -E 'platforms;android-34|build-tools;34.0.0|platform-tools' || true

if [[ -d Android_WebView_Source ]]; then
  echo "Verifying Android project dependencies..."
  (
    cd Android_WebView_Source
    gradle --version >/dev/null
  )
fi

echo "Install complete."
