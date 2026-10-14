Update my existing PrawnHub Android application to support the ESP32 sensor changes for both turbidity and salinity.

**Salinity changes**
1. Replace the user-facing label "TDS (ppm)" with "Salinity (ppt)" throughout the Farmer Dashboard, History, charts, alerts, settings, and PDF/Excel reports.
2. Read the salinity estimate from the existing Firebase path `/ShrimpHub/tds` to maintain compatibility.
3. Make sure live readings and history records use the same salinity value in ppt.
4. Update the minimum and maximum salinity settings and alert logic so their values are interpreted in ppt, not ppm.
5. Do not artificially clamp readings to 17.9–18.3 ppt or generate fake values.

**Turbidity changes**
1. Read the filtered turbidity value from `/ShrimpHub/turbidity`.
2. Use consistent values in the dashboard, History, charts, alerts, and reports.
3. Preserve the configured threshold settings and prevent alerts from being triggered by brief fluctuations.
4. Do not label the value as confirmed NTU until the sensor conversion is calibrated and validated.

**General requirements**
- Inspect the existing project before editing.
- Preserve the current Firebase database structure, authentication, navigation, and unrelated features.
- Update labels, units, calculations, validation, and report formatting consistently.
- Identify all files changed and provide testing steps for both sensors.
- Do not invent or modify sensor readings to make them appear stable.