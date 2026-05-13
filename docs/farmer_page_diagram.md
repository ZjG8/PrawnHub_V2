# Farmer Page Text-Based Diagram

This diagram shows the farmer flow and each farmer slide/page in the current Android app.

```text
Welcome / Role Selection
    |
    | Tap FARMER
    | Session role = "farmer"
    v
+--------------------------------------------------+
| FARMER DASHBOARD AREA                            |
|                                                  |
| Bottom navigation slides:                        |
| [1] Dashboard                                    |
| [2] History                                      |
| [3] Growth                                       |
| [4] Settings                                     |
+--------------------------------------------------+
```

## Farmer Slide Navigation

```text
+-------------+     +----------+     +--------+     +----------+
| Dashboard   | <-> | History  | <-> | Growth | <-> | Settings |
+-------------+     +----------+     +--------+     +----------+
       ^                  ^              ^              ^
       |                  |              |              |
       +------------------+--------------+--------------+
                  Bottom navigation bar
```

## Slide 1: Dashboard

```text
+--------------------------------------------------+
| U                    Dashboard              Logo |
+--------------------------------------------------+
| Pond Status                                      |
| Healthy / Warning / Danger                       |
+--------------------------------------------------+
| Alert Banner                                     |
| Hidden when normal, shown when Firebase alerts   |
| are active                                       |
+--------------------------------------------------+
| Water Temperature                                |
|                                                  |
|                    -- C                          |
|                    Stable                        |
+--------------------------------------------------+
| Salinity                  | Turbidity            |
| -- ppm                    | -- NTU               |
+---------------------------+----------------------+
| Water Level               | Oxygen               |
| -- cm                     | -- mg/L              |
+---------------------------+----------------------+
| pH Level                                         |
| --                                               |
+--------------------------------------------------+
| Growth Prediction                                |
| Estimated prawn health and recommendation text   |
+--------------------------------------------------+
| Recent Important Alerts                          |
| No important alerts / active alert summary       |
+--------------------------------------------------+
| Controls                                         |
| [Switch] Pump                                    |
| [Switch] Filter                                  |
| [Switch] Aerator                                 |
| [Button] Feed Now                                |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

Data used by this slide:

```text
Firebase /aquarium
    -> temp_val
    -> tds_val
    -> turb_val
    -> water_lvl
    -> oxygen_val or do_val
    -> ph_val

Firebase /settings
    -> min_temp, max_temp
    -> min_sal, max_sal
    -> max_turb
    -> overflow_limit
    -> min_oxygen
    -> min_ph, max_ph

Firebase /alerts
    -> ammonia_risk
    -> overflow_risk
    -> temp_alert
    -> sal_alert
    -> oxygen_alert
    -> turbidity_alert

Firebase /control
    -> pump_stat
    -> filter_stat
    -> aerator_stat
    -> motor_trig when Feed Now is tapped
```

## Slide 2: History

```text
+--------------------------------------------------+
| [Back]                                           |
| History                                          |
+--------------------------------------------------+
| [Range Spinner]          [Sensor Spinner]        |
| Today / Week / Month     Temperature / Salinity  |
|                           Turbidity / Water      |
|                           Oxygen / pH            |
+--------------------------------------------------+
| Important Events                                 |
+--------------------------------------------------+
| Line Chart                                       |
| Shows filtered Firebase history values           |
+--------------------------------------------------+
| Event List                                       |
| +----------------------------------------------+ |
| | Parameter                                    | |
| | Timestamp                                    | |
| | Value                                        | |
| | Status / Important alert                     | |
| +----------------------------------------------+ |
| | More events...                               | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

Data used by this slide:

```text
Firebase /history
    -> last 50 records ordered by timestamp
    -> timestamp
    -> param_type
    -> rec_val
    -> status

Only important events are displayed:
    -> non-normal status
    -> low oxygen
    -> salinity outside target range
    -> high water level
    -> high turbidity
    -> temperature outside target range
    -> pH outside target range
```

## Slide 3: Growth

```text
+--------------------------------------------------+
| Growth Analytics                                 |
+--------------------------------------------------+
| Estimated Growth                                 |
+--------------------------------------------------+
| Health Progress                                  |
|                                                  |
|              [Horizontal progress]               |
|              0% health                           |
|              0 days                              |
+--------------------------------------------------+
| Weekly Growth Trend                              |
+--------------------------------------------------+
| Harvest readiness message                        |
+--------------------------------------------------+
| Line Chart                                       |
| Weekly growth health                             |
+--------------------------------------------------+
| Status Badge                                     |
| Needs Attention / Good / On Track                |
+--------------------------------------------------+
| Recommendations                                  |
| Water quality recommendation text                |
+--------------------------------------------------+
| [Button] Set Start Date                          |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

Data used by this slide:

```text
Firebase /settings
    -> target_harvest_days

Firebase /growth
    -> start_date
    -> days_in_optimal_temp
    -> days_of_culture
    -> performance_score
    -> water_quality_score

Firebase /aquarium
    -> temp_val
    -> tds_val
    -> turb_val
    -> oxygen_val or do_val
    -> ph_val

Firebase /growth_trend
    -> last 7 score records
```

## Slide 4: Settings

```text
+--------------------------------------------------+
| Monitoring Settings                         Logo |
+--------------------------------------------------+
| Account                                          |
| user@email.com                                   |
+--------------------------------------------------+
| Pond Settings                                    |
| Max Temperature     [read-only value]            |
| Min Temperature     [read-only value]            |
| Max Salinity        [read-only value]            |
| Min Salinity        [read-only value]            |
| Max Turbidity       [read-only value]            |
| Min Oxygen          [read-only value]            |
| Max pH              [read-only value]            |
| Min pH              [read-only value]            |
| Overflow Limit      [read-only value]            |
+--------------------------------------------------+
| Feeding Automation                               |
| Feed Time           [read-only value]            |
| Feeding Interval    [read-only value]            |
| [Disabled switch] Auto-feed during sleep mode    |
+--------------------------------------------------+
| Notifications                                    |
| [Disabled switch] Sound alert                    |
| [Disabled switch] Vibrate                        |
| [Disabled switch] Push notifications             |
+--------------------------------------------------+
| [Button] About PrawnHub                          |
| [Button] Back to Roles                           |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

Farmer-mode behavior:

```text
Session role = "farmer"
    |
    v
SettingsActivity
    |
    +-- Title becomes "Monitoring Settings"
    +-- Back button hidden
    +-- Bottom navigation visible
    +-- Save Settings button hidden
    +-- Input fields disabled / read-only
    +-- Notification and feeding switches disabled
    +-- Logout button text becomes "Back to Roles"
```

Data used by this slide:

```text
Firebase /settings
    -> max_temp, min_temp
    -> max_sal, min_sal
    -> max_turb
    -> feed_time
    -> feed_interval_hours
    -> overflow_limit
    -> min_oxygen
    -> min_ph, max_ph
    -> sound_alert
    -> vibrate
    -> push_notifications
    -> midnight_feeding

Firebase /thresholds
    -> temperature/min, temperature/max
    -> salinity/min, salinity/max
    -> turbidity/max
    -> oxygen/min
    -> ph/min, ph/max
    -> water_level/max
```

## Farmer Activity Mapping

| Farmer slide | Android activity | Layout file |
|---|---|---|
| Dashboard | `DashboardActivity` | `activity_dashboard.xml` |
| History | `HistoryActivity` | `activity_history.xml` |
| Growth | `GrowthActivity` | `activity_growth.xml` |
| Settings | `SettingsActivity` | `activity_settings.xml` |

## Farmer User Flow

```text
Choose Role
    |
    +-- FARMER
        |
        v
    DashboardActivity
        |
        +-- View pond status and sensor readings
        +-- See active alerts
        +-- Toggle pump, filter, aerator
        +-- Send Feed Now command
        |
        +-- Bottom nav: History
        |       |
        |       +-- Filter important sensor events by range and sensor
        |
        +-- Bottom nav: Growth
        |       |
        |       +-- View health score, days of culture, trend, and recommendations
        |       +-- Set culture start date
        |
        +-- Bottom nav: Settings
                |
                +-- View monitoring settings in read-only farmer mode
                +-- Open About PrawnHub
                +-- Back to Roles
```
