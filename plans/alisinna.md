In my existing PrawnHub Android application, completely remove the text **"(uncalibrated)"** from the Turbidity section.

Requirements:
1. On the **Farmer Dashboard**, remove "(uncalibrated)" from the Turbidity card, subtitle, description, or any text displayed near the turbidity reading.
2. In **Settings**, remove "(uncalibrated)" from the turbidity configuration, threshold labels, descriptions, helper text, and related sections.
3. Search the entire project for the exact text "uncalibrated" and remove all user-visible occurrences related to turbidity, including variations in capitalization.
4. Keep the displayed sensor name as **Turbidity**.
5. Preserve the existing turbidity readings, Firebase connection, database paths, threshold values, alert logic, and sensor functionality.
6. Do not change the actual sensor calculations or modify any readings as part of this task.
7. Check Kotlin/Java files, XML layouts, string resources, Compose UI components, and any other files that may generate the text dynamically.
8. If the text is assembled from multiple strings or generated dynamically, remove its source so it does not reappear when the app refreshes or restarts.
9. Do not redesign the UI or modify unrelated features.
10. After making the changes, search the entire project again for "uncalibrated" to verify that no user-visible occurrences remain in the Turbidity Dashboard or Settings.
11. Build the application and report which files were changed and whether the build succeeded.

Make the changes directly in the existing project rather than just suggesting code changes.