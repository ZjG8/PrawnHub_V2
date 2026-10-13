Fix the Growth Prediction card in `GrowthActivity` / `activity_growth.xml`.

- Keep the existing **GROWTH PREDICTION** card layout.
- Display only a centered **circular progress indicator** and the text **“Growth Progress: N%”** below it.
- Remove “Culture Duration Progress” and “Configured target” text.
- Fix the text visibility issue: ensure the progress text uses a readable text color, sufficient contrast, proper size, and correct constraints/margins so it is not hidden, clipped, or overlapped.
- Calculate the percentage using the existing culture progress and target harvest days from Firebase. Show a suitable fallback such as “Growth Progress: Unavailable” when data is missing.
- Preserve existing Firebase integration and prediction logic. Do not redesign the whole screen.
- Build and test the screen to confirm the circular indicator and text are both visible and update correctly.