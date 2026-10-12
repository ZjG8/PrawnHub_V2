# Growth and harvest workflow fields

The existing paths remain unchanged. These additive fields support the Growth screen workflow.

| Path | Type | Purpose |
|---|---|---|
| `/ShrimpHub/sensors_active` | boolean | Set by the ESP32 after it publishes readings; distinguishes an inactive sensor from a valid zero reading. |
| `/ShrimpHub/sensors_last_update` | string Unix seconds | Last successful ESP32 sensor upload; useful for diagnosing stale data. |
| `/ShrimpHub/history/{unixSeconds}` | object | Grouped temperature, TDS, and turbidity sample used only for the labelled sensor-condition trend. |
| `/growth/status` | `active` or `harvested` | Controls whether the current culture is tracked as active. |
| `/growth/harvested_at` | Unix milliseconds | Recorded only when the farmer confirms harvesting. |
| `/growth/last_harvest_readiness_notification_id` | string | Prevents repeat readiness notifications for one culture/target-duration event. |
| `/growth/completed_cultures/{cultureId}` | object | Immutable-style harvest record preserving start date, harvest timestamp, and configured target duration. |
| `/growth/reset_cultures/{cultureId}` | object | Preserves an active culture's start date, reset timestamp, and target duration when the farmer resets tracking. |

`/settings/target_harvest_days` is the existing configured-duration fallback. It is not a machine-learning output or a measurement of prawn biomass. It drives both the displayed Harvest Date and the configured harvest-readiness rule when no validated biological model is available.
