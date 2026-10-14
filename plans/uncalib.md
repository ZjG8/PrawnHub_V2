Update my existing PrawnHub Android application to use the filtered turbidity readings from the ESP32.

Requirements:
1. Keep the existing Firebase Realtime Database path `/ShrimpHub/turbidity`.
2. Update the Farmer Dashboard to display the latest turbidity reading with the label **Turbidity**.
3. Update History to display and save turbidity readings consistently with the live dashboard.
4. Update turbidity charts, alerts, settings, and PDF/Excel reports to use the same turbidity values.
5. Preserve the existing minimum and maximum turbidity threshold settings and ensure alert logic follows the configured thresholds.
6. Ensure brief fluctuations do not trigger repeated alerts unnecessarily.
7. Do not artificially force readings into the 0–5 range or generate fake sensor values.
8. Preserve the existing Firebase structure, authentication, navigation, UI design, and unrelated features.
9. Inspect the current project first and modify only the files necessary for turbidity.
10. After implementation, list the files changed and provide steps to test the dashboard, History, alerts, and reports.