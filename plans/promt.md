**Codex Prompt — Update PrawnHub Growth Prediction**

Modify `GrowthActivity` and `activity_growth.xml` to update the Growth Prediction section.

1. **UI changes**
   - Keep the circular progress indicator.
   - Display `Growth Progress: N%` below the circle.
   - Replace the message `Target harvest duration not configured` with `Growth Progress: Unavailable` when no valid prediction can be calculated.
   - Remove the user-editable target harvest duration from the Growth screen and Settings UI. The user must not manually set the target harvest days.

2. **Automatic harvest prediction**
   - Make the app estimate the target harvest duration and target harvest date automatically using available Firebase sensor history, current sensor conditions, culture start date, and recorded culture/harvest data.
   - Use relevant data from `/ShrimpHub`, `/ShrimpHub/history`, and `/growth`.
   - Check whether a genuine trained and validated ML model exists. If it does, integrate it properly. If not, do not pretend that rule-based calculations are ML predictions.
   - If available data is insufficient for a reliable estimate, show `Growth Progress: Unavailable` and explain that more culture or harvest data is needed. Do not invent predictions or use arbitrary hardcoded target days.

3. **Progress calculation**
   - Calculate elapsed culture days from `/growth/start_date`.
   - When a valid predicted culture duration is available, calculate progress as `(elapsed culture days / predicted culture duration) × 100`, capped at 100%.
   - Calculate the predicted harvest date from the culture start date and predicted duration.
   - Display elapsed days, estimated remaining days, and predicted harvest date only when valid data is available.

4. **Firebase and reliability**
   - Preserve existing culture start, harvest, and reset functions.
   - Store prediction results in an appropriate `/growth` child only if needed, without overwriting existing sensor or culture records.
   - Handle missing data, invalid timestamps, Firebase read failures, and unavailable predictions clearly.
   - Ensure the circular indicator and text update when Firebase data changes.

5. **Testing**
   - Inspect the existing code and database structure before editing.
   - Do not break History, Settings, Reports, or other screens.
   - Build the project and report the files changed, prediction method used, required data, and test results.