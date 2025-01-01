#!/bin/bash

SHORTCUT_NAME="Nordnet-dev-authenticator"
SHORTCUT_PATH="$HOME/Desktop/$SHORTCUT_NAME.command"
PROJECT_PATH="$(pwd)"

echo "Creating desktop shortcut from $PROJECT_PATH."

cat <<EOF > "$SHORTCUT_PATH"
#!/bin/bash
pwd
cd "$PROJECT_PATH"
pwd
./gradlew bootRun
EOF

chmod a+x "$SHORTCUT_PATH"

echo "Shortcut created in $SHORTCUT_PATH."
