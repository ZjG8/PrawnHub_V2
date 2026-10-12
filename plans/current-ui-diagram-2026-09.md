# Current UI Diagram

This is a text-based map of the Android UI as implemented in the current XML layouts and Java activity navigation.

## Entry and role flow

```text
+-----------------------------+
| Welcome                     |
| PrawnHub                    |
| Smart Aquaculture Monitoring|
| [ Get Started ]             |
+-----------------------------+
              |
              v
+-----------------------------+
| Choose Role                 |
| [ ADMIN ]     [ FARMER ]    |
+-----------------------------+
       |                 |
       v                 v
+----------------+   +-------------------+
| Admin Login    |   | Farmer Dashboard  |
| email/password |   | (bottom navigation)|
+----------------+   +-------------------+
       |
       v
+-------------------+
| Admin Dashboard   |
+-------------------+
```

## Farmer area

```text
Bottom navigation: [ Dashboard ] [ History ] [ Growth ] [ Settings ]

+------------------------------------------------------------------+
| Dashboard                                                        |
| PrawnHub                                                         |
| Pond Status: Healthy / Warning / Danger                          |
| [active alert banner - shown only when an alert exists]          |
|                                                                  |
| + Temperature +  + TDS         +                                 |
| | value/status |  | value/status|                                 |
| +-------------+  +-------------+                                 |
| + Turbidity   +  + Water Level +                                 |
| | value/status |  | value/status|                                 |
| +-------------+  +-------------+                                 |
| System Controls: [Filter] [Salt Water] [Fresh Water]             |
| Feed Control: status, last feed, [FEED NOW] [STOP FEEDING]       |
| Alerts | Growth Status | History Preview                          |
| Admin Tools (visible only when the saved role is admin)          |
+------------------------------------------------------------------+

+------------------------------------------------------------------+
| History                                                          |
| [Back]                                                           |
| Date & Time Range: [start] [end] [Apply Filter]                  |
| [Sensor filter]                                                  |
| Graph Analytics: Temperature / Salinity / Turbidity / Water Level|
| History List                                                      |
| Firebase Verification (latest timestamp and sensor values)       |
+------------------------------------------------------------------+

+------------------------------------------------------------------+
| Growth Analytics                                                 |
| Estimated Growth: progress, health score, culture days           |
| Weekly Growth Trend chart | harvest readiness                     |
| Status badge | recommendations | [Set Start Date]                |
+------------------------------------------------------------------+

+------------------------------------------------------------------+
| Monitoring Settings (farmer mode)                                |
| Role/account | read-only pond thresholds                         |
| Read-only feeding and notification preferences                   |
| Appearance: ( ) Light  ( ) Dark  ( ) Follow System               |
| [About PrawnHub]  [Back to Roles]                                |
+------------------------------------------------------------------+
```

## Admin area

```text
+------------------------------------------------------------------+
| Admin Dashboard                                                  |
| Quick Actions: [Generate Farm Reports] [System Settings]         |
|                [Recent Alerts]                                   |
| Live Sensor Summary: Temperature | Salinity | Water Level |      |
|                      Turbidity                                   |
| Feeding Automation: feed time, interval, auto-feed state         |
| [Logout]                                                         |
+------------------------------------------------------------------+
       | reports                         | settings
       v                                 v
+---------------------------+  +----------------------------------+
| Farm Reports              |  | System Settings                  |
| [Back]                    |  | [Back]                           |
| start/end date pickers    |  | Editable pond thresholds         |
| report preview            |  | Feeding automation               |
| [Export PDF] [Export Excel]| | Notifications                    |
+---------------------------+  | Appearance (Light/Dark/System)   |
                               | [About] [Save Settings] [Logout] |
                               +----------------------------------+
```

## Shared secondary screens

```text
+---------------------------------------------------------------+
| Manage User Accounts (admin-only entry from Dashboard tools) |
| [Back] [Search users] [Add User]                             |
| User cards: email, role/status, [Edit] [Toggle] [Delete]     |
+---------------------------------------------------------------+

+---------------------------------------------------------------+
| About PrawnHub                                                |
| [Back] | version | product summary | team/school | technology |
| [Contact Team]                                                |
+---------------------------------------------------------------+
```

## Navigation notes

```text
Farmer: Dashboard <-> History <-> Growth <-> Settings via shared bottom nav.
Admin:  Admin Dashboard -> Reports / Settings; Dashboard admin tools -> Manage Users / Reports.
Settings: the same activity switches between read-only farmer mode and editable admin mode.
```
