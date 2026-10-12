# PrawnHub Growth Prediction UI Update

Update the existing **Growth Analytics** page into the following **Growth Prediction** design and behavior.

## 1. Header
- Remove the `Growth Prediction` title beside `← Back`.
- Keep only the Back button/icon in the top header.

## 2. Growth Prediction
Replace the current horizontal progress bar with a **circular progress bar**.

The circle must clearly show:
- **Predicted Growth: XX%**
- **Condition: GOOD / MONITOR / CRITICAL**
- **Health Score: XX / 100**

These values must be calculated from the **actual Firebase sensor/history records**, not hardcoded sample values.

If the culture **start date has not been set**, the prediction must not start. Show an appropriate “not started” state instead.

## 3. Culture Information
Show:
- Culture Days
- Growth Stage
- Estimated Harvest
- Harvest Readiness %

Important:
- Do not use a fixed target such as **75 days**.
- Do not display a target harvest date.
- Do not depend on a predefined target harvest day.
- The system must create its **own estimated harvest prediction** based on the available sensor records and culture progress.
- Culture Days must only count after the user sets the actual start date.

## 4. Sensor Conditions
Display the latest live:
- Temperature
- Salinity/TDS
- Turbidity

Use Firebase data from:

`/ShrimpHub/temperature`  
`/ShrimpHub/tds`  
`/ShrimpHub/turbidity`  
`/ShrimpHub/history`

Sensor values and their **Good / Monitor / Critical** status must be calculated from the actual detected readings and existing configured ranges.

Do not hardcode example values.

## 5. Growth Analysis
Make the Growth Analysis **live and sensor-based**.

The explanation should change according to the actual sensor history.

Examples:
- Stable sensor conditions → better predicted growth.
- Frequent or long deviations → lower predicted growth.
- A sensor repeatedly outside its acceptable range → explain which condition is affecting growth.

Do not always display the same hardcoded sentence.

## 6. Growth Trend
Keep the Growth Trend chart, but make it **fully live**.

The chart must use actual records from:

`/ShrimpHub/history`

The trend should reflect changes in:
- Temperature
- Salinity/TDS
- Turbidity

Requirements:
- No hardcoded chart points.
- Update when new Firebase sensor records are detected.
- Use valid historical records.
- Handle missing or invalid records safely.
- The displayed trend must actually change based on the sensor data.

## 7. Prediction Logic
The prediction should consider:
- Sensor readings within acceptable ranges
- Sensor stability over time
- Frequency of abnormal readings
- Duration of abnormal conditions
- Current sensor conditions
- Overall sensor/health score
- Culture days
- Growth progress
- Estimated harvest/readiness

All displayed growth values must be derived from the available records.

The prediction is an **estimate based on recorded pond conditions**, not a scientific biomass measurement.

## 8. Farmer Dashboard Dark Mode
Fix the **Feed Control** section on the Farmer Dashboard.

When Dark/Night Mode is enabled, Feed Control currently remains light.

Make the entire Feed Control section follow the dark theme, including:
- Background
- Cards
- Text
- Labels
- Buttons
- Status indicators
- Borders/dividers

## General Requirements
- Preserve existing Firebase and ESP32 communication.
- Preserve existing navigation and other screens.
- Keep the existing PrawnHub design style.
- Support both Light and Dark/Night Mode.
- Do not use the term **ML** anywhere on the Growth Prediction page.
- Do not create duplicate screens or unnecessary components.
- Do not remove unrelated existing functionality.
- Ensure the final implementation builds successfully and has no errors.