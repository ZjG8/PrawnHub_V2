# ShrimpHub Testing Checklist

## Unit Tests

- [ ] UT01 - Temperature sensor reads correctly within 2% of a real thermometer
- [ ] UT02 - TDS sensor reads a non-zero value in saltwater
- [ ] UT03 - Turbidity reads higher in cloudy water than clear water
- [ ] UT04 - Ultrasonic reads a smaller number when water level is higher
- [ ] UT05 - Stepper motor rotates when `motor_trig` is set to true in Firebase

## Integration Tests

- [ ] IT01 - Serial Monitor shows all 4 readings every 3 seconds
- [ ] IT02 - Firebase dashboard shows updated values within 15 seconds
- [ ] IT03 - Android app dashboard updates within 5 seconds of sensor change
- [ ] IT04 - Toggling pump button in app turns relay on and off on the hardware

## System Tests

- [ ] ST01 - Stir water to increase turbidity, then verify filter relay activates
- [ ] ST02 - Block ultrasonic sensor, then verify water-level alert fires
- [ ] ST03 - Set feed time to 2 minutes from now, then verify motor spins
- [ ] ST04 - Turn off hotspot, then verify ESP32 keeps running offline

## Acceptance Tests

- [ ] AT01 - Run for 24 hours, then verify history has continuous entries
- [ ] AT02 - Growth score calculates correctly for a 30-day culture
- [ ] AT03 - App receives push notification within 10 seconds of alert
- [ ] AT04 - App login and app restart session handling work correctly

## Final Deployment Checklist

- [ ] Firebase Realtime Database schema from `firebase/database_schema.json` is imported
- [ ] Firebase rules from `firebase/database.rules.json` are published
- [ ] Firebase Email/Password Authentication is enabled
- [ ] A Firebase user exists for the farm account
- [ ] Android app has a real `google-services.json` from Firebase Console
- [ ] Firmware placeholders in `firmware/shrimphub_esp32/shrimphub_esp32.ino` are replaced
- [ ] ESP32 connects to the farmer phone hotspot
- [ ] Serial Monitor shows sensor values every 3 seconds
- [ ] Android dashboard updates from Firebase
- [ ] Manual pump, filter, aerator, and feed commands work
- [ ] Alert notifications work while the app is open
- [ ] All power wiring is enclosed and away from water
- [ ] System has passed a 24-hour dry run before real tank deployment
