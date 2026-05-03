# Firebase Setup Notes

## Android App

1. Open Firebase Console.
2. Open the ShrimpHub project.
3. Click Project settings.
4. In Your apps, add an Android app.
5. Use package name `com.example.prawnhub_v2`.
6. Download `google-services.json`.
7. Put `google-services.json` in the `app/` folder of this Android project.
8. Sync Gradle or rebuild the project. The Google Services plugin will turn on automatically when the file exists.

Without `google-services.json`, the Android app can compile but Firebase will not connect at runtime.

## Cloud Messaging Test

1. In Firebase Console, open Build, then Messaging.
2. Create a new notification campaign.
3. Enter title `ShrimpHub Alert`.
4. Enter body `ShrimpHub test notification`.
5. Choose the Android app target.
6. Send the test message to your device.

## Alert Data Payloads

For targeted tests, send a data payload with one of these keys:

| Key | Value |
|---|---|
| `alert_type` | `ammonia_risk` |
| `alert_type` | `overflow_risk` |
| `alert_type` | `temp_alert` |
