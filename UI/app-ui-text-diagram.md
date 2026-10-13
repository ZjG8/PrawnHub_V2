# PrawnHub App - Text UI Diagram

## App navigation

```text
[Welcome]
    |
    `-> [Get Started]
            |
            `-> [Choose Role]
                    |
                    +-> [Farmer] -> [Farmer Dashboard]
                    |
                    `-> [Admin] -> [Login]
                                      |
                                      +-> [Continue as Farmer] -> [Farmer Dashboard]
                                      `-> [Login] -> [Admin Dashboard]
```

## Farmer flow

```text
+--------------------------------------------------+
| PrawnHub                                         |
| Pond Status: Healthy / Monitor / Action needed   |
|                                                  |
| Temperature | TDS | Turbidity | Water Level      |
| [System Controls: Filter / Salt / Fresh / Drain] |
| [Feed Control: status, last feed, amount, count] |
| [Feed Now]                                       |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
                 |          |          |
                 v          v          v
```

```text
[History]
    |
    +-> Date/time range + [Apply Filter] + [Show All History]
    +-> Sensor selector
    +-> Scrollable history rows: timestamp, sensor, value, status
    +-> Temperature / TDS / Turbidity / Water Level charts
    `-> Firebase verification of the latest valid reading

[Growth]
    |
    +-> Circular culture-duration progress + "Growth Progress: N%"
    +-> Culture start, target harvest date, elapsed and remaining days
    +-> Live sensor conditions: temperature, TDS, turbidity, water level
    +-> Growth analysis + condition-score trend
    +-> [Set Start Date] / [Harvest] / [Reset Culture]
    `-> Shows ML prediction unavailable until a validated model is supplied

[Settings]
    |
    +-> Water-quality thresholds and overflow limit
    +-> Target harvest duration (administrator editable)
    +-> Feeding time / interval / auto-feed
    +-> Notification and appearance preferences
    `-> [Save Settings] / [About PrawnHub] / [Logout]
```

## Administrator flow

```text
+--------------------------------------------------+
| PrawnHub - Admin Dashboard                       |
|                                                  |
| QUICK ACTIONS                                    |
| [Generate Farm Reports] [System Settings]        |
| [History] [Growth] [Manage User Accounts]        |
|                                                  |
| LIVE SENSOR SUMMARY                              |
| Temperature | Salinity | Water Level | Turbidity |
| Feeding automation summary                       |
| [Logout]                                         |
+--------------------------------------------------+
        |              |              |
        v              v              v
   [Reports]       [Settings]   [Manage Users]
```

```text
[Reports]
    |
    +-> Select start/end date
    +-> Preview actual sensor records
    `-> [Export PDF] / [Export Excel]

[Manage Users]
    |
    +-> List user accounts and roles
    `-> [Add User] and account-management actions

[About]
    `-> App version, project description, team, and technologies
```

## Shared data sources

```text
Firebase Realtime Database
    |
    +-> /ShrimpHub        -> live sensors, controls, feeder
    +-> /ShrimpHub/history-> History rows, charts, growth trend, reports
    +-> /settings         -> thresholds and target_harvest_days
    +-> /growth           -> active culture, harvest, reset records
    `-> /users            -> account management
```
