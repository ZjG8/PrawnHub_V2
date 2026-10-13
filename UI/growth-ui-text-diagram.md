# PrawnHub Growth Screen — Text UI Diagram

Screen: `GrowthActivity` / `activity_growth.xml`

```text
+--------------------------------------------------+
| [← Back]                                         |
+--------------------------------------------------+
| GROWTH PREDICTION                                |
| +----------------------------------------------+ |
| |              [circular progress]             | |
| | Growth Progress: N% / Unavailable / Started  | |
| | Configured target: N culture days             | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| CULTURE INFORMATION                              |
| +----------------------------------------------+ |
| | Culture Start Date: date / Not Set            | |
| | Harvest Date: date / Unavailable              | |
| | Culture Days: N / --                          | |
| | Growth Stage: active / harvested / not started| |
| +----------------------------------------------+ |
| [Condition: In progress / Ready / Unavailable]   |
+--------------------------------------------------+
| SENSOR CONDITIONS                                |
| +----------------------------------------------+ |
| | Temperature: NN.N C — Normal/Warning/...     | |
| | Salinity/TDS: NN.N ppm — Normal/Warning/...  | |
| | Turbidity: NN.N NTU — Normal/Warning/...     | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| GROWTH ANALYSIS                                  |
| +----------------------------------------------+ |
| | Duration-based readiness explanation or      | |
| | configuration/data-availability guidance     | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| GROWTH TREND                                     |
| +----------------------------------------------+ |
| | Condition-score line chart (0–100), or       | |
| | an explanation of why it is unavailable      | |
| +----------------------------------------------+ |
| [ Set Start Date / Start New Culture ]           |
| [ Harvest ] (enabled only when ready)            |
| [ Reset Culture ]                                |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

## State and interaction flow

```text
No saved start date
    |
    +-> Show “Not Started” / unavailable states
    +-> Keep Harvest disabled
    +-> Show an empty Growth Trend explanation
    `-> [Set Start Date] validates a non-future date and saves /growth/start_date

Active culture with configured target_harvest_days
    |
    +-> Culture days = today - saved start date
    +-> Progress = culture days / configured target duration
    +-> Before target: “Culture in progress”; Harvest disabled
    `-> At or after target: “Ready for harvest review”; Harvest enabled

[Harvest]
    |
    +-> Ask for confirmation
    +-> Save harvested status and an immutable completed-culture record
    `-> Show harvested state; [Set Start Date] becomes [Start New Culture]

[Reset Culture]
    |
    +-> Ask for confirmation
    +-> Archive the reset event
    `-> Clear only the active growth cycle; sensor history is retained

Sensor and history updates
    |
    +-> Evaluate live temperature, TDS, turbidity, and water level against settings
    +-> Build chronological condition scores from complete post-start records
    `-> Never represent sensor readings as measured prawn biomass or growth
```

## Data inputs

| Firebase path | Use on screen |
|---|---|
| `/growth/start_date` | Active culture start date |
| `/growth/status` and `/growth/harvested_at` | Harvested/active state and displayed harvest date |
| `/growth/completed_cultures` | Immutable completed-culture history |
| `/growth/reset_cultures` | Reset-event archive |
| `/settings/target_harvest_days` | Configured culture duration and readiness calculation |
| `/settings` sensor thresholds | Sensor-condition evaluation and trend score |
| `/ShrimpHub` | Live temperature, TDS, turbidity, water level, and sensor activity |
| `/ShrimpHub/history` | Complete historical samples used for the condition trend |
