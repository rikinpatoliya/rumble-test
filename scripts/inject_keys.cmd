@echo off
setlocal enabledelayedexpansion

set "SECRETS_DIR=..\secrets"
set "PROJECT_DIR=.."

for %%E in (debug qa release) do (
    git clone -b main "[git@git.rumble.work:49096]:rumble/mobile/android/android-battles-app-%%E-keys.git" "%SECRETS_DIR%\%%E" 2>nul

    if not exist "%PROJECT_DIR%\app\keystore\%%E" mkdir "%PROJECT_DIR%\app\keystore\%%E"
    if not exist "%PROJECT_DIR%\tv\keystore\%%E" mkdir "%PROJECT_DIR%\tv\keystore\%%E"

    copy /Y "%SECRETS_DIR%\%%E\app\keystore\keystore.properties" "%PROJECT_DIR%\app\keystore\%%E\keystore.properties" 2>nul
    copy /Y "%SECRETS_DIR%\%%E\app\keystore\android.keystore" "%PROJECT_DIR%\app\keystore\%%E\android.keystore" 2>nul
    copy /Y "%SECRETS_DIR%\%%E\app\keys.properties" "%PROJECT_DIR%\app\keystore\%%E\keys.properties" 2>nul

    copy /Y "%SECRETS_DIR%\%%E\tv\keystore\keystore.properties" "%PROJECT_DIR%\tv\keystore\%%E\keystore.properties" 2>nul
    copy /Y "%SECRETS_DIR%\%%E\tv\keystore\android_tv.keystore" "%PROJECT_DIR%\tv\keystore\%%E\android_tv.keystore" 2>nul
    copy /Y "%SECRETS_DIR%\%%E\tv\keystore\fire_tv.keystore" "%PROJECT_DIR%\tv\keystore\%%E\fire_tv.keystore" 2>nul

    if not exist "%PROJECT_DIR%\app\src\%%E" mkdir "%PROJECT_DIR%\app\src\%%E"
    if not exist "%PROJECT_DIR%\ftv\src\%%E" mkdir "%PROJECT_DIR%\ftv\src\%%E"
    if not exist "%PROJECT_DIR%\atv\src\%%E" mkdir "%PROJECT_DIR%\atv\src\%%E"

    copy /Y "%SECRETS_DIR%\%%E\app\google-services.json" "%PROJECT_DIR%\app\src\%%E\google-services.json" 2>nul
    copy /Y "%SECRETS_DIR%\%%E\tv\google-services.json" "%PROJECT_DIR%\atv\src\%%E\google-services.json" 2>nul
    copy /Y "%SECRETS_DIR%\%%E\tv\google-services.json" "%PROJECT_DIR%\ftv\src\%%E\google-services.json" 2>nul
)
