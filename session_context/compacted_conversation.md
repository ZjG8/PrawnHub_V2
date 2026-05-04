# PrawnHub V2 Session Context

Use this file as the first read in a new session. It captures the compacted conversation and the current project state.

## Project

- Workspace: `C:\Users\sijey\AndroidStudioProjects\PrawnHub_V2`
- Android package / applicationId / namespace: `com.example.prawnhub_v2`
- Firebase app nickname used: `ShrimpHub Android`
- Main launcher activity: `com.example.prawnhub_v2/.LoginActivity`
- `app/google-services.json` has been added and Gradle processes it successfully.
- The project is not currently a Git repository.

## Plan Source

The work started from the Markdown file inside `/plans`. The implementation follows the shrimp/prawn monitoring app plan:

- Android app with Firebase Authentication and Realtime Database.
- ESP32 firmware for sensor uploads.
- Dashboard, history, growth estimate, settings, notifications.
- Firebase setup docs, database schema, and database rules.

## Firebase And Android Setup

Important files:

- `app/google-services.json`
- `app/build.gradle.kts`
- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle/libs.versions.toml`
- `firebase/database_schema.json`
- `firebase/database.rules.json`
- `docs/firebase_setup.md`

Firebase plugin setup:

- Google services plugin is declared in Gradle.
- `app/build.gradle.kts` conditionally applies `com.google.gms.google-services` only when `google-services.json` exists.
- Firebase dependencies include Authentication, Realtime Database, and Messaging.

## Android Files Added Or Updated

Java classes:

- `app/src/main/java/com/example/prawnhub_v2/BaseNavActivity.java`
- `app/src/main/java/com/example/prawnhub_v2/NotificationHelper.java`
- `app/src/main/java/com/example/prawnhub_v2/HistoryItem.java`
- `app/src/main/java/com/example/prawnhub_v2/HistoryAdapter.java`
- `app/src/main/java/com/example/prawnhub_v2/MyFirebaseMessagingService.java`
- `app/src/main/java/com/example/prawnhub_v2/LoginActivity.java`
- `app/src/main/java/com/example/prawnhub_v2/DashboardActivity.java`
- `app/src/main/java/com/example/prawnhub_v2/HistoryActivity.java`
- `app/src/main/java/com/example/prawnhub_v2/GrowthActivity.java`
- `app/src/main/java/com/example/prawnhub_v2/SettingsActivity.java`

Layouts and resources:

- `app/src/main/res/layout/activity_login.xml`
- `app/src/main/res/layout/activity_dashboard.xml`
- `app/src/main/res/layout/activity_history.xml`
- `app/src/main/res/layout/activity_growth.xml`
- `app/src/main/res/layout/activity_settings.xml`
- `app/src/main/res/layout/bottom_nav.xml`
- `app/src/main/res/layout/item_history.xml`
- `app/src/main/res/drawable/card_background.xml`
- `app/src/main/res/drawable/card_alert.xml`
- `app/src/main/res/drawable/banner_alert.xml`
- `app/src/main/res/drawable/badge_green.xml`
- `app/src/main/res/drawable/badge_yellow.xml`
- `app/src/main/res/drawable/badge_red.xml`
- `app/src/main/res/drawable/input_background.xml`
- `app/src/main/res/drawable/nav_background.xml`
- `app/src/main/res/drawable/section_background.xml`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/styles.xml`
- `app/src/main/res/values/themes.xml`

## Recent Design Changes

The `$mobile-android-design` skill was used for Android design work.

The UI was updated to be more user-friendly and lower contrast:

- Softer green primary palette.
- Lighter surface/card backgrounds.
- Less harsh alert colors.
- Material Components no-action-bar theme.
- Reusable button and input styles.
- Softer cards, sections, nav bar, badges, and alert banners.
- Headers added across dashboard, history, growth, and settings screens.

Key colors currently used:

- `brand_primary`: `#4F8F88`
- `brand_primary_dark`: `#2F6762`
- `brand_primary_soft`: `#DDEDEA`
- `brand_accent`: `#D9A441`
- `surface_soft`: `#F7FAF8`
- `surface_card`: `#FEFFFD`
- `text_primary`: `#20302E`
- `text_secondary`: `#657570`

## Logout Button

A logout button was added to `activity_settings.xml` after the Save Settings button.

`SettingsActivity.java` now:

- Imports `Intent`.
- Imports `FirebaseAuth`.
- Finds `R.id.logoutButton`.
- Calls `FirebaseAuth.getInstance().signOut()`.
- Opens `LoginActivity`.
- Clears the task stack using `Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK`.

This prevents users from returning to protected screens with the back button after logout.

## Firmware

ESP32 firmware exists at:

- `firmware/shrimphub_esp32/shrimphub_esp32.ino`

Placeholder values still need to be replaced before flashing:

- `REPLACE_THIS_YOUR_HOTSPOT_NAME`
- `REPLACE_THIS_YOUR_HOTSPOT_PASSWORD`
- `REPLACE_THIS_YOUR_FIREBASE_API_KEY`
- `REPLACE_THIS_YOUR_FIREBASE_DATABASE_URL`
- `REPLACE_THIS_YOUR_FIREBASE_PASSWORD`

Supporting docs:

- `docs/wiring_guide.md`
- `docs/testing_checklist.md`
- `docs/firebase_setup.md`

## Commands That Worked

Use Android Studio's bundled JBR:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'; .\gradlew.bat assembleDebug
```

Install to emulator/device:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'; .\gradlew.bat installDebug
```

Launch the app:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.prawnhub_v2/.LoginActivity
```

Check connected devices:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
```

## Verification Already Done

After the design changes and logout button:

- `assembleDebug` completed successfully.
- `installDebug` completed successfully.
- App installed on Pixel 5 AVD / emulator.
- `LoginActivity` launched successfully.

## Device Notes

ADB previously saw:

- `emulator-5554 device`
- Pixel 5 AVD, Android 13

No physical Android phone was visible at that time. If installing to a real phone:

- Enable Developer Options.
- Enable USB Debugging.
- Accept the debugging prompt on the phone.
- Run `adb devices` again.

## Important Behavior

- `LoginActivity` skips login if `FirebaseAuth.getCurrentUser()` is not null.
- Logout clears Firebase auth state and returns to login.
- Firebase initialization should work because `google-services.json` exists and the Google services Gradle plugin runs.

## Next Useful Checks

- Open the app and confirm the logout button appears in Settings.
- Test login, navigation, logout, then back-button behavior.
- Replace ESP32 firmware placeholders before testing hardware.
- Confirm Firebase Realtime Database rules are deployed.
- Confirm app package in Firebase remains `com.example.prawnhub_v2`.
