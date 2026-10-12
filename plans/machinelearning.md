# PrawnHub Android App – Required UI & ML Changes

Modify the existing PrawnHub Android app based on the current UI/navigation structure.

## 1. Machine Learning Growth Prediction
Replace the current basic Growth Analytics logic with a **Machine Learning-based growth prediction system**.

- Growth prediction must use sensor history/data:
  - Temperature
  - Salinity/TDS
  - Turbidity
- The ML model should evaluate how consistently the sensors stay within their normal/target ranges.
- Stable conditions should contribute to **faster predicted growth**.
- Frequent or long periods outside the normal ranges should reduce the predicted growth rate.
- Display:
  - Predicted growth
  - Growth progress
  - Health/condition score
  - Culture days
  - Estimated harvest/readiness
  - Simple explanation of how sensor conditions affect the prediction
- Use historical Firebase sensor data as the model input where possible.
- Do not claim the prediction is scientifically exact; present it as an estimate.

## 2. Remove "Smart Control"
Remove the wording and UI concept of **Smart Control**.

There should still be a **System Control** section for manual control.

Keep controls such as:
- Filter
- Salt Water
- Fresh Water

These remain manually controllable by the user.

## 3. Farmer Dashboard – Remove Sections
Remove these sections completely from the Farmer Dashboard:

- Feed Control section
- Alerts section
- Growth Status section
- History Preview section

Remove:

`Feed Control: status, last feed, [FEED NOW] [STOP FEEDING]`

and:

`Alerts | Growth Status | History Preview`

Do not leave empty spaces or unused containers.

## 4. Farmer Dashboard – Maximize Space
After removing the sections above:

- Expand the remaining dashboard components to use the available screen space.
- Make sensor cards larger and easier to read.
- Improve spacing and alignment.
- Keep the layout clean and responsive.
- Prioritize:
  1. Pond Status
  2. Temperature
  3. Salinity/TDS
  4. Turbidity
  5. Water Level
  6. System Controls
- Avoid unnecessary scrolling where possible.

## 5. Dark/Night Mode Bug
Fix the Farmer Dashboard dark/night mode.

Currently, when Night Mode is enabled, the Feed Control area remains in Light Mode.

Since the Feed Control section is being removed, ensure **all remaining Farmer Dashboard components** correctly follow the selected theme.

Check:
- Backgrounds
- Cards
- Text
- Buttons
- Sensor status labels
- System Control section
- Dialogs
- Charts
- ML Growth screens

No Farmer screen component should remain stuck in Light Mode.

## 6. Growth Screen
Update Growth Analytics to match the ML system.

Suggested structure:

```text
Growth Analytics
-------------------------
Predicted Growth
[ ML prediction ]

Growth Progress
[ progress ]

Sensor Condition Score
Temperature:  Good
Salinity:     Good
Turbidity:    Good

Culture Days
Estimated Harvest

ML Recommendation
"Stable water conditions are supporting
the predicted growth rate."

The UI should clearly show that the prediction is based on sensor conditions.

7. Firebase Compatibility

Keep the existing Firebase Realtime Database structure unless a change is required.

Continue using the existing sensor/history data:

/ShrimpHub/temperature
/ShrimpHub/tds
/ShrimpHub/turbidity
/ShrimpHub/waterLevel
/ShrimpHub/history

Do not break the existing ESP32-to-Firebase communication.

8. Important
Preserve the existing login, role selection, Farmer/Admin navigation, History, Settings, Reports, and Admin functionality unless specifically changed above.
Do not remove existing sensor monitoring.
Do not redesign the entire application unnecessarily.
Make the changes directly in the existing Android project.
Keep the UI consistent with the current PrawnHub design.
Use simple, understandable labels.
Ensure the project still compiles after the changes.


This prompt keeps the requested changes focused while making the **sensor-based ML growth prediction** the main new feature.