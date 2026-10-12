# PrawnHub System UI — Text Diagram

This diagram reflects the Android activities, XML layouts, and navigation wiring in the current project.

## Application Flow

```text
+---------------------------+
| Welcome                   |
| PrawnHub logo             |
| Smart Aquaculture         |
| [Get Started]             |
+---------------------------+
             |
             v
+---------------------------+
| Choose Role               |
| [Admin]                   |
| [Farmer]                  |
+---------------------------+
       |               |
       |               +------------------------------+
       v                                              v
+---------------------------+          +---------------------------+
| Admin Login               |          | Farmer Dashboard          |
| Email                     |          | (bottom navigation)       |
| Password                  |          +---------------------------+
| [Show Password]           |
| [Login]                   |
| [Forgot Password?]        |
| [Create Account]          |
+---------------------------+
       |
       v
+---------------------------+
| Admin Dashboard           |
+---------------------------+
```

## Farmer Interface

```text
+--------------------------------------------------+
| PrawnHub                                    [logo]|
+--------------------------------------------------+
| Pond Status: Healthy / Warning / Danger           |
+--------------------------------------------------+
| Live Sensor Cards                                 |
| +-------------------+ +-------------------------+ |
| | Temperature       | | TDS                     | |
| | value in °C       | | value in ppm            | |
| | status            | | status                  | |
| +-------------------+ +-------------------------+ |
| +-------------------+ +-------------------------+ |
| | Turbidity         | | Water Level             | |
| | value in NTU      | | value in cm             | |
| | status            | | status                  | |
| +-------------------+ +-------------------------+ |
+--------------------------------------------------+
| System Controls                                  |
| [switch] Filter       [switch] Salt Water        |
| [switch] Fresh Water  [switch] Drain Pump        |
+--------------------------------------------------+
| Feed Control                                     |
| Status: Ready / Feeding                           |
| Last feed • Feed amount • Feed count             |
| [FEED NOW]                                       |
+--------------------------------------------------+
| Admin-only tools (when an admin opens this view) |
| User summary • Settings preview                  |
| [Manage Users] [Export Report]                   |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings           |
+--------------------------------------------------+
```

```text
History
├─ Back
├─ Date & Time Range
│  ├─ [Start date/time]
│  ├─ [End date/time]
│  └─ [Apply Filter]
├─ Sensor filter: All / Temperature / Salinity / Turbidity / Water Level
└─ Sensor-log list: parameter, timestamp, value, status

Growth
├─ Growth Analytics
├─ Estimated Growth
├─ Health Progress and performance score
├─ Weekly Growth Trend chart
├─ Status badge and recommendations
└─ [Set Start Date]

Monitoring Settings (farmer)
├─ Role: Farmer
├─ Pond thresholds (read-only)
│  ├─ Temperature range
│  ├─ Salinity range
│  ├─ Turbidity limit
│  └─ Overflow limit
├─ Feeding Automation
├─ Notification preferences
├─ [About PrawnHub]
└─ [Back to Roles]
```

## Admin Interface

```text
+--------------------------------------------------+
| Admin                                       [logo]|
+--------------------------------------------------+
| ADMIN DASHBOARD                                  |
| Online status • Sync status                       |
+--------------------------------------------------+
| Quick Actions                                    |
| [Generate Farm Reports]                          |
| [System Settings]                                |
| [Recent Alerts]                                  |
+--------------------------------------------------+
| Live Sensor Summary                              |
| Temperature | Salinity/TDS | Water Level | Turb. |
+--------------------------------------------------+
| Feeding Automation                               |
| Feed time • interval • midnight auto-feed        |
+--------------------------------------------------+
| [Logout]                                         |
+--------------------------------------------------+
```

```text
Admin navigation
├─ Generate Farm Reports → Farm Reports
│  ├─ Start date / End date
│  ├─ Report preview
│  ├─ [Export PDF]
│  └─ [Export Excel]
├─ System Settings → System Settings
│  ├─ Editable pond threshold controls
│  ├─ Feeding automation controls
│  ├─ Notification switches
│  ├─ [About PrawnHub]
│  ├─ [Save Settings]
│  └─ [Logout]
├─ Recent Alerts → latest-warning message
└─ Logout → Choose Role

Admin tools available from Dashboard
└─ Manage User Accounts
   ├─ [Search users]
   ├─ [Add User]
   └─ Per-user card: email, role, status, registration date
      ├─ [Edit]
      ├─ [Toggle active/disabled]
      └─ [Delete]
```

## Shared About Screen

```text
About PrawnHub
├─ Back
├─ PrawnHub logo and Version 1.0
├─ System description
├─ Development team and school
├─ Technologies: ESP32, Firebase, Android Studio
└─ [Contact Team]
```

## Role-Specific Navigation Summary

| Role | Primary route | Navigation model |
|---|---|---|
| Farmer | Dashboard | Persistent bottom navigation: Dashboard, History, Growth, Settings |
| Admin | Admin Dashboard | Action cards plus back buttons; Settings exposes editable controls |
| Shared | Welcome, Role Selection, About | Forward navigation and standard back actions |
