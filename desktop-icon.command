#!/bin/bash

DESKTOP_DIR="$HOME/Desktop"
SHORTCUT_NAME="Nordnet dev authenticator"
ICON_PATH="./src/main/resources/img.png"
TARGET_SCRIPT="./gradleBootRun.sh"

cat <<EOF > "$DESKTOP_DIR/$SHORTCUT_NAME.desktop"
[Desktop Entry]
Version=1.0
Type=Application
