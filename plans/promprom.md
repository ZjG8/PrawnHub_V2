Fix the missing target harvest configuration in PrawnHub Growth Prediction.

- Inspect `GrowthActivity`, `activity_growth.xml`, and the existing Firebase settings structure.
- Add `/settings/target_harvest_days` as a configurable positive number of culture days. Integrate it with the existing admin settings UI if appropriate.
- Do not overwrite existing settings or silently hardcode a target duration.
- Read `/growth/start_date` (`2026-10-13`) and `/growth/status` (`active`) from Firebase.
- Calculate the target harvest date by adding `target_harvest_days` to the culture start date.
- Calculate growth progress using elapsed culture days divided by target culture days, multiplied by 100, capped at 100%.
- Keep the circular progress indicator and visible `Growth Progress: N%` text.
- If the target duration is not configured, display `Target harvest duration not configured` instead of a misleading percentage or date.
- Ensure culture start, reset, and Firebase updates refresh the displayed values correctly.
- Preserve all existing settings and functionality. Build and test the app.