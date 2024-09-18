#!/bin/bash
set -Eeuo pipefail

SECRETS_DIR="../secrets"
PROJECT_DIR=".."

for env in "debug" "qa" "release"
do
	git clone -b main "[git@git.rumble.work:49096]:rumble/mobile/android/android-battles-app-$env-keys.git" "$SECRETS_DIR/$env" || true

	mkdir -p "$PROJECT_DIR/app/keystore/$env"
	mkdir -p "$PROJECT_DIR/tv/keystore/$env"

	cp -f "$SECRETS_DIR/$env/app/keystore/keystore.properties" "$PROJECT_DIR/app/keystore/$env/keystore.properties" || true
	cp -f "$SECRETS_DIR/$env/app/keystore/android.keystore" "$PROJECT_DIR/app/keystore/$env/android.keystore" || true
	cp -f "$SECRETS_DIR/$env/app/keys.properties" "$PROJECT_DIR/app/keystore/$env/keys.properties" || true

	cp -f "$SECRETS_DIR/$env/tv/keystore/keystore.properties" "$PROJECT_DIR/tv/keystore/$env/keystore.properties" || true
	cp -f "$SECRETS_DIR/$env/tv/keystore/android_tv.keystore" "$PROJECT_DIR/tv/keystore/$env/android_tv.keystore" || true
	cp -f "$SECRETS_DIR/$env/tv/keystore/fire_tv.keystore" "$PROJECT_DIR/tv/keystore/$env/fire_tv.keystore" || true

	mkdir -p "$PROJECT_DIR/app/src/$env"
	mkdir -p "$PROJECT_DIR/ftv/src/$env"
	mkdir -p "$PROJECT_DIR/atv/src/$env"

	cp -f "$SECRETS_DIR/$env/app/google-services.json" "$PROJECT_DIR/app/src/$env/google-services.json" || true
	cp -f "$SECRETS_DIR/$env/tv/google-services.json" "$PROJECT_DIR/atv/src/$env/google-services.json" || true
	cp -f "$SECRETS_DIR/$env/tv/google-services.json" "$PROJECT_DIR/ftv/src/$env/google-services.json" || true
done
