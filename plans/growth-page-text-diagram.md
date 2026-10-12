# Growth Analytics — Text-Based Diagram

```text
┌──────────────────────────────────────────────────────────────┐
│ Growth Analytics                                               │
│                                                              │
│ Predicted Growth                                              │
│ ┌──────────────────────────────────────────────────────────┐ │
│ │                 [ Growth progress bar ]                  │ │
│ │                                                          │ │
│ │ Predicted growth rate: 1.00x                             │ │
│ │ Culture days: 30  •  Growth progress: 40%               │ │
│ └──────────────────────────────────────────────────────────┘ │
│                                                              │
│ Growth Progress                                              │
│ ┌──────────────────────────────────────────────────────────┐ │
│ │ Estimated harvest/readiness: around day 75                │ │
│ └──────────────────────────────────────────────────────────┘ │
│ ┌──────────────────────────────────────────────────────────┐ │
│ │ Growth stage: Growing  •  Harvest readiness: 40%          │ │
│ └──────────────────────────────────────────────────────────┘ │
│                                                              │
│ ┌──────────────────────────────────────────────────────────┐ │
│ │              Sensor condition trend chart                │ │
│ │     100 ┤      ╭──╮                                       │ │
│ │      50 ┤  ╭───╯  ╰──╮                                   │ │
│ │       0 ┴────────────────── history samples ──────────── │ │
│ └──────────────────────────────────────────────────────────┘ │
│                                                              │
│ [ Sensor condition score: 82% ]                              │
│                                                              │
│ Sensor Condition Score                                       │
│ ┌──────────────────────────────────────────────────────────┐ │
│ │ Temperature: Good (90% of readings in target)            │ │
│ └──────────────────────────────────────────────────────────┘ │
│ ┌──────────────────────────────────────────────────────────┐ │
│ │ Salinity/TDS: Good (85% of readings in target)           │ │
│ └──────────────────────────────────────────────────────────┘ │
│ ┌──────────────────────────────────────────────────────────┐ │
│ │ Turbidity: Monitor (70% of readings in target)           │ │
│ └──────────────────────────────────────────────────────────┘ │
│                                                              │
│ Growth Analysis                                              │
│ ┌──────────────────────────────────────────────────────────┐ │
│ │ Stable water conditions are supporting a faster predicted │ │
│ │ growth rate. This is an estimate based on sensor history. │ │
│ └──────────────────────────────────────────────────────────┘ │
│                                                              │
│ [ Set Start Date ]                                           │
├──────────────────────────────────────────────────────────────┤
│                    Bottom navigation                         │
└──────────────────────────────────────────────────────────────┘
```

## Data flow

```text
/ShrimpHub/history (latest 100 valid records)
  ├── temperature
  ├── tds
  └── turbidity
          │
          ▼
SensorGrowthPredictor
  ├── percentage of readings within configured target ranges
  ├── overall sensor condition score
  ├── estimated growth rate and harvest day
  └── readiness/progress percentage
          │
          ▼
Growth Analytics cards and condition trend chart

/growth/start_date ──► culture days
/settings ───────────► target ranges and target harvest days
```

The displayed growth figures are estimates from available sensor history, not scientific biomass measurements.
