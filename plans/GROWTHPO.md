# PrawnHub Growth Prediction UI Update

Update the existing Growth Analytics screen to become **Growth Prediction** and match the requested layout.

## UI
- Header: `← Back` + `Growth Prediction`
- **GROWTH PREDICTION**
  - Predicted Growth %
  - Progress bar
  - Condition: Good / Monitor / Critical
  - Health Score /100
- **CULTURE INFORMATION**
  - Culture Days
  - Growth Stage
  - Estimated Harvest
  - Harvest Readiness %
- **SENSOR CONDITIONS**
  - Temperature + status
  - Salinity/TDS + status
  - Turbidity + status
- **GROWTH ANALYSIS**
  - Short, simple explanation based on recent sensor conditions
- **GROWTH TREND**
  - Live trend chart based on detected sensor/history data

## Live Growth Trend
Make the Growth Trend update from Firebase sensor/history data, not hardcoded sample points.

Use:
`/ShrimpHub/history`
- temperature
- tds
- turbidity

Also use:
`/growth/start_date` → culture days
`/settings` → configured target ranges and target harvest days

Reuse the existing `SensorGrowthPredictor` logic where appropriate. Calculate the displayed prediction from available sensor history, including:
- percentage of readings within target ranges
- overall sensor condition/health score
- estimated growth rate
- harvest day/readiness
- growth progress

The chart should refresh when new sensor/history records are detected and reflect the actual recorded conditions. Handle missing/invalid records safely.

## Requirements
- Do NOT use the term `ML` anywhere on this page.
- The page must remain separate from the Farmer Dashboard.
- Keep the prediction clearly labeled as an estimate, not scientific biomass measurement.
- Preserve Firebase/ESP32 compatibility and existing navigation.
- Keep the existing PrawnHub visual style and dark/night mode.
- Avoid unnecessary redesigns or duplicate screens.
- Inspect the existing project first, modify the correct existing files, then build/compile and fix errors.
