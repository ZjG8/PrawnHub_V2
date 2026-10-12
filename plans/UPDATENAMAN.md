# PrawnHub UI Update Prompt

Update the existing PrawnHub Android app to match the two UI designs below.

## Farmer Dashboard
Implement this structure:
- PRAWNHUB header
- POND STATUS with Healthy status and short condition message
- 4 large sensor cards:
  - Temperature
  - Salinity / TDS
  - Turbidity
  - Water Level
- SYSTEM CONTROLS with:
  - Filter
  - Salt Water
  - Fresh Water
- FEED CONTROL with:
  - Status
  - Last Feed
  - Feed Amount
  - Feed Count
  - FEED NOW button

Remove unnecessary/empty sections. Maximize available screen space and keep the layout clean and responsive.

## Growth Prediction
Keep this as a separate screen from the Farmer Dashboard.
Include:
- Predicted Growth percentage + progress bar
- Condition
- Health Score
- Culture Days
- Growth Stage
- Estimated Harvest
- Harvest Readiness
- Sensor Conditions: Temperature, Salinity/TDS, Turbidity
- Short Growth Analysis explaining how stable sensor conditions affect the prediction
- Growth Trend visualization

Use Firebase history/sensor data where available. The prediction must be presented as an estimate, not scientifically exact. Do NOT use the term “ML” anywhere on this page.

## General Requirements
- Use the existing PrawnHub design/style; do not redesign unrelated screens.
- Keep Firebase compatibility with:
  `/ShrimpHub/temperature`
  `/ShrimpHub/tds`
  `/ShrimpHub/turbidity`
  `/ShrimpHub/waterLevel`
  `/ShrimpHub/history`
  `/ShrimpHub/feeder/*`
- Keep existing login, role selection, navigation, History, Settings, Reports, and Admin functionality.
- Use “SYSTEM CONTROLS”, not “Smart Control”.
- Ensure dark/night mode works consistently on all updated screens.
- Inspect the existing project first and modify the appropriate existing files instead of creating unnecessary duplicates.
- Preserve existing functionality and ESP32 communication.
- Build/compile the project after changes and fix any errors.
