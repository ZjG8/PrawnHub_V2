# Farmer Pages Text Diagram

This diagram reflects the current farmer-facing app after `Update24.md`.

```text
------------------+
| Welcome          |
+------------------+
         |
         v
+------------------+
| Role Selection   |
|                  |
| [Admin] [Farmer] |
+------------------+
         |
         | Tap Farmer
         | SessionStore.role = "farmer"
         v
+--------------------------------------------------+
| Farmer Area                                      |
|                                                  |
| Bottom navigation:                               |
| [Dashboard] [History] [Growth] [Settings]        |
+--------------------------------------------------+
```

## Farmer Navigation

```text
-------------+       +----------+       +--------+       +----------+
| Dashboard   | <---> | History  | <---> | Growth | <---> | Settings |
+-------------+       +----------+       +--------+       +----------+
       ^                   ^                 ^                 ^
       |                   |                 |                 |
       +-------------------+-----------------+-----------------+
                         Bottom nav
```

## Dashboard

```text
+--------------------------------------------------+
| PrawnHub                                    Logo  |
+--------------------------------------------------+
| Pond Status                                      |
| Healthy / Warning / Danger                       |
+--------------------------------------------------+
| Alert Banner                                     |
| Hidden normally, visible for active alerts       |
+--------------------------------------------------+
| Temperature              | TDS                   |
| -- C                     | -- ppm                |
| Stable / Critical        | Normal / Warning      |
+--------------------------+-----------------------+
| Turbidity                | Water Level           |
| -- NTU                   | -- ft                 |
| Normal / Critical        | Normal / Warning      |
+--------------------------+-----------------------+
| System Controls                                  |
| [Switch] Filter          -> /ShrimpHub/control/pump1
| [Switch] Salt Water      -> /ShrimpHub/control/pump2
| [Switch] Fresh Water     -> /ShrimpHub/control/pump3
+--------------------------------------------------+
| Feed Control                                     |
| Status: Ready to Feed / Feeding...               |
| Last Feed Time: --                               |
| [Feed Now]       -> feedNow=true, feeding=true   |
| [Stop Feeding]   -> feedNow=false, feeding=false |
+--------------------------------------------------+
| Alerts                                           |
| No water quality alerts / active alert summary   |
+--------------------------------------------------+
| Growth Status                                    |
| Estimated prawn health and recommendation text   |
+--------------------------------------------------+
| History Preview                                  |
| Latest five history rows                         |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

Firebase paths:

```text
/ShrimpHub
    temperature
    tds
    turbidity
    waterLevel

/ShrimpHub/control
    pump1
    pump2
    pump3

/ShrimpHub/feeder
    feedNow
    feeding
    lastFeedTime

/ShrimpHub/history
    latest records for preview

/settings
    min_temp, max_temp
    min_sal, max_sal
    max_turb
    overflow_limit
```

## History

```text
+--------------------------------------------------+
| [Back]                                           |
| History                                          |
+--------------------------------------------------+
| [Sensor Filter Dropdown]                         |
| All Sensors / Temperature / Salinity /           |
| Turbidity / Water Level                          |
+--------------------------------------------------+
| Graph Analytics                                  |
+--------------------------------------------------+
| Temperature Trend                                |
| [Line chart from /ShrimpHub/history]             |
+--------------------------------------------------+
| Salinity Trend                                   |
| [Line chart from /ShrimpHub/history]             |
+--------------------------------------------------+
| Turbidity Trend                                  |
| [Line chart from /ShrimpHub/history]             |
+--------------------------------------------------+
| Water Level Trend                                |
| [Line chart from /ShrimpHub/history]             |
+--------------------------------------------------+
| History List                                     |
| +----------------------------------------------+ |
| | Timestamp: yyyy-MM-dd HH:mm:ss / raw value   | |
| | Sensor: Temperature / Salinity / etc.        | |
| | Value: numeric value + unit                  | |
| | Status: Normal / alert status                | |
| +----------------------------------------------+ |
| | Newest records first                         | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

Firebase paths:

```text
/ShrimpHub/history
    timestamp
    temperature
    tds
    turbidity
    waterLevel

Legacy-compatible row support:
    timestamp or time
    param_type
    rec_val or value
    status
```

## Growth

```text
+--------------------------------------------------+
| Growth Analytics                                 |
+--------------------------------------------------+
| Estimated Growth                                 |
| +----------------------------------------------+ |
| | Health progress bar                          | |
| | 0-100% health                                | |
| | Days of culture                              | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| Weekly Growth Trend                              |
| Harvest readiness message                        |
| [Line chart: last 7 growth health records]       |
+--------------------------------------------------+
| Status Badge                                     |
| On Track / Good / Needs Attention                |
+--------------------------------------------------+
| Recommendations                                  |
| Water quality recommendations based on sensors   |
+--------------------------------------------------+
| [Set Start Date]                                 |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

Firebase paths:

```text
/settings
    target_harvest_days

/growth
    start_date
    days_in_optimal_temp
    days_of_culture
    performance_score
    water_quality_score

/growth_trend
    score

/aquarium and /ShrimpHub
    sensor values used for health scoring
```

## Settings

```text
+--------------------------------------------------+
| Monitoring Settings                         Logo |
+--------------------------------------------------+
| Role                                             |
| Farmer                                           |
+--------------------------------------------------+
| Pond Settings                                    |
| Temperature Range      [read-only for farmer]    |
| Salinity Range         [read-only for farmer]    |
| Turbidity Range        [read-only for farmer]    |
| Overflow Limit (ft)    [read-only for farmer]    |
+--------------------------------------------------+
| Feeding Automation                               |
| Feed Time              [editable]                |
| Feeding Interval       [editable]                |
| [Switch] Auto Feed     [editable]                |
+--------------------------------------------------+
| Notifications                                    |
| [Switch] Sound Alert        [editable]           |
| [Switch] Vibrate            [editable]           |
| [Switch] Push Notifications [editable]           |
+--------------------------------------------------+
| [About PrawnHub]                                 |
| [Save Settings]                                  |
| [Back to Roles]                                  |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

Farmer permission model:

```text
Farmer can edit:
    /settings/feed_time
    /settings/feed_interval_hours
    /settings/midnight_feeding
    /settings/esp32_feeding_scheduler
    /settings/sound_alert
    /settings/vibrate
    /settings/push_notifications

Farmer cannot edit:
    /settings/min_temp
    /settings/max_temp
    /settings/min_sal
    /settings/max_sal
    /settings/min_turb
    /settings/max_turb
    /settings/overflow_limit
    /thresholds/*
```

## Activity And Layout Map

| Farmer page | Activity | Layout |
|---|---|---|
| Dashboard | `DashboardActivity` | `activity_dashboard.xml` |
| History | `HistoryActivity` | `activity_history.xml` |
| Growth | `GrowthActivity` | `activity_growth.xml` |
| Settings | `SettingsActivity` | `activity_settings.xml` |

## Main Farmer Use Flow

```text
Role Selection
    |
    +-- Farmer
        |
        v
Dashboard
    |
    +-- Monitor pond status and sensor cards
    +-- Toggle Filter / Salt Water / Fresh Water
    +-- Feed Now or Stop Feeding
    |
    +-- History
    |       |
    |       +-- View realtime trend charts
    |       +-- Filter newest-first history list by sensor
    |
    +-- Growth
    |       |
    |       +-- View health score and growth trend
    |       +-- Set culture start date
    |
    +-- Settings
            |
            +-- Edit feeding automation
            +-- Edit notification preferences
            +-- View pond thresholds as read-only
            +-- Save allowed farmer settings
```
