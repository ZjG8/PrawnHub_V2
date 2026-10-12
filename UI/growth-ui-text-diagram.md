# PrawnHub Growth Screen - Text UI Diagram

Screen: `GrowthActivity` / `activity_growth.xml`

```text
+--------------------------------------------------+
| [<- Back]                                        |
+--------------------------------------------------+
| GROWTH PREDICTION                                |
| +----------------------------------------------+ |
| |             [ circular progress ]            | |
| |                                                | |
| | Predicted growth rate: -- / 1.00x             | |
| | Culture days: N  /  Growth progress: N%       | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| CULTURE INFORMATION                              |
| +----------------------------------------------+ |
| | Estimated harvest: unavailable / culture day | |
| +----------------------------------------------+ |
| +----------------------------------------------+ |
| | Growth stage: Not started / Growing / ...    | |
| +----------------------------------------------+ |
| [ Condition: Waiting / Good / Monitor / Critical]|
+--------------------------------------------------+
| SENSOR CONDITIONS                                |
| +----------------------------------------------+ |
| | Temperature: live value + condition          | |
| +----------------------------------------------+ |
| | Salinity/TDS: live value + condition         | |
| +----------------------------------------------+ |
| | Turbidity: live value + condition            | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| GROWTH ANALYSIS                                  |
| +----------------------------------------------+ |
| | Recommendation or data-availability message  | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| GROWTH TREND                                     |
| +----------------------------------------------+ |
| | Line chart of valid sensor-condition history | |
| | or an empty-state message                     | |
| +----------------------------------------------+ |
| [Set Start Date]                                 |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

## State Rules

```text
No saved culture start date
    |
    +-> Prediction: not started
    +-> Culture information: not started / unavailable
    +-> Analysis: prompt to save the actual start date
    +-> Trend: empty; no fabricated chart points
    `-> [Set Start Date] opens a date picker and saves /growth/start_date

Saved valid culture start date
    |
    +-> Culture days = current date - saved start date
    +-> Read live values from /ShrimpHub
    +-> Read history from /ShrimpHub/history
    +-> Ignore history records before the saved start date
    |
    +-> No complete post-start records
    |   `-> Explain that more data is required; do not create an estimate
    |
    `-> Complete post-start records
        +-> Calculate condition score from configured ranges
        +-> Render progress, stage, analysis, and chart
        `-> Label output as an estimate, not measured biomass
```

## Data Inputs

| Firebase path | Use on screen |
|---|---|
| `/growth/start_date` | Enables culture-day counting and growth analysis |
| `/ShrimpHub/temperature` | Live temperature condition |
| `/ShrimpHub/tds` | Live TDS/salinity condition |
| `/ShrimpHub/turbidity` | Live turbidity condition |
| `/ShrimpHub/history` | Trend and post-start condition analysis |
| `/settings` | Temperature, TDS/salinity, and turbidity target ranges |
