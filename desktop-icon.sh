#!/bin/bash

echo "Creating desktop shortcut from $(pwd)."

SHORTCUT_NAME="Nordnet-dev-authenticator"
SHORTCUT_PATH="$HOME/Desktop/$SHORTCUT_NAME.command"
SHORTCUT_PATH="$(pwd)/$SHORTCUT_NAME.command"
ICON_PATH="$(pwd)/src/main/resources/img.png"
RUN_SCRIPT="sh $(pwd)/gradlew bootRun"


cat <<EOF > "$SHORTCUT_PATH"
#!/bin/bash
$RUN_SCRIPT
EOF

chmod a+x "$SHORTCUT_PATH"
chmod 755 "$SHORTCUT_PATH"

echo "Shortcut created in $SHORTCUT_PATH."
