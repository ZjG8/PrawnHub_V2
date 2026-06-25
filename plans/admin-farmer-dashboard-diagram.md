# Admin and Farmer Dashboard Text Diagrams

This file maps the main PrawnHub V2 dashboard layouts and navigation flow for the two app roles.

## Role Entry Flow

```text
+----------------------+
| Login / Role Select |
+----------------------+
           |
           v
  +------------------+
  | Check user role  |
  | SessionStore     |
  +------------------+
       |          |
       | admin    | farmer
       v          v
+---------------+  +------------------+
| Admin         |  | Farmer           |
| Dashboard     |  | Dashboard        |
+---------------+  +------------------+
```

## Admin Dashboard

```text
+--------------------------------------------------+
| Admin                                    [ Logo ] |
+--------------------------------------------------+
|                                                  |
|                 ADMIN DASHBOARD                  |
|                                                  |
+--------------------------------------------------+
| System Status                                    |
| [ Online ]        [ Synced ]       [ ESP32 OK ]  |
+--------------------------------------------------+
| Quick Actions                                    |
|                                                  |
| +----------------------------------------------+ |
| | Manage User Accounts                         | |
| | Search, create, edit roles, disable users    | |
| +----------------------------------------------+ |
|                                                  |
| +----------------------------------------------+ |
| | Farm Reports                                 | |
| | Date range, preview, export PDF / Excel      | |
| +----------------------------------------------+ |
|                                                  |
| +----------------------------------------------+ |
| | System Settings                              | |
| | Thresholds, feeding, notifications           | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| Live Sensor Summary                              |
| +----------------------+ +---------------------+ |
| | Temperature          | | Salinity            | |
| | -- C                 | | -- ppt              | |
| +----------------------+ +---------------------+ |
| | Water Level          | | Turbidity           | |
| | Normal / Alert       | | Clear / Alert       | |
| +----------------------+ +---------------------+ |
+--------------------------------------------------+
| Feeding Automation                               |
| Feed Time: --:--                                 |
| Interval: Every -- hours                         |
| Midnight Auto Feed: Enabled / Disabled           |
+--------------------------------------------------+
| [ Logout ]                                       |
+--------------------------------------------------+
```

## Admin Navigation Flow

```text
Admin Dashboard
    |
    +--> Manage User Accounts
    |       |
    |       +--> Firebase: /users
    |       +--> Search users
    |       +--> Add user record
    |       +--> Assign role: admin / farmer
    |       +--> Toggle active / disabled
    |       +--> Delete user record
    |
    +--> Farm Reports
    |       |
    |       +--> Select start date
    |       +--> Select end date
    |       +--> Preview sensor, alert, and growth data
    |       +--> Export PDF
    |       +--> Export Excel
    |
    +--> System Settings
    |       |
    |       +--> Firebase: /settings
    |       +--> Firebase: /thresholds
    |       +--> Edit temperature thresholds
    |       +--> Edit salinity thresholds
    |       +--> Edit turbidity threshold
    |       +--> Edit overflow limit
    |       +--> Edit feeding automation
    |       +--> Edit notification settings
    |
    +--> Logout
            |
            +--> Clear SessionStore role
            +--> FirebaseAuth.signOut()
            +--> Return to role selection
```

## Farmer Dashboard

```text
+--------------------------------------------------+
| [ Farmer Logo ]              Dashboard   [ Logo ] |
+--------------------------------------------------+
| Pond Status                                      |
| [ Healthy ]      [ Warning ]       [ Danger ]    |
+--------------------------------------------------+
| Alert Banner                                     |
| Firebase real-time alert summary                 |
+--------------------------------------------------+
| Water Temperature                                |
|                                                  |
|                       -- C                       |
|                       Stable / Warning           |
+--------------------------------------------------+
| Sensor Summary                                   |
| +----------------------+ +---------------------+ |
| | Salinity             | | Turbidity           | |
| | -- ppt               | | -- NTU              | |
| +----------------------+ +---------------------+ |
|                                                  |
| +----------------------------------------------+ |
| | Water Level                                  | |
| | -- cm                                        | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| Growth Prediction                                |
| Health status, projected growth, recommendation  |
+--------------------------------------------------+
| Recent Alerts                                    |
| Firebase-only important alerts                   |
+--------------------------------------------------+
| Controls                                         |
| [ Pump Switch ]                                  |
| [ Filter Switch ]                                |
| [ Feed Now ]                                     |
+--------------------------------------------------+
| Dashboard       History       Growth     Settings|
+--------------------------------------------------+
```

## Farmer Navigation Flow

```text
Farmer Dashboard
    |
    +--> Dashboard
    |       |
    |       +--> Real-time pond status
    |       +--> Real-time sensor readings
    |       +--> Alert banner
    |       +--> Pump / filter / feed controls
    |
    +--> History
    |       |
    |       +--> Select start date and time
    |       +--> Select end date and time
    |       +--> Filter by sensor type
    |       +--> View line chart
    |       +--> View important event list
    |
    +--> Growth
    |       |
    |       +--> Health score
    |       +--> Weekly growth trend
    |       +--> Status badge
    |       +--> Recommendations
    |
    +--> Settings
            |
            +--> View account role: Farmer
            +--> View read-only pond thresholds
            +--> View read-only feeding automation
            +--> View read-only notification settings
            +--> About PrawnHub
            +--> Back to roles
```

## Shared Data Areas

```text
Firebase Realtime Database
    |
    +--> /sensors
    |       +--> temperature
    |       +--> salinity
    |       +--> turbidity
    |       +--> waterLevel
    |
    +--> /alerts
    |       +--> real-time dashboard alerts
    |       +--> recent alert list
    |       +--> important history events
    |
    +--> /settings
    |       +--> feeding automation
    |       +--> notification settings
    |
    +--> /thresholds
    |       +--> admin editable limits
    |       +--> farmer read-only limits
    |
    +--> /users
            +--> user role
            +--> account status
```

## Role Permission Summary

| Area | Admin | Farmer |
|---|---|---|
| Live sensor dashboard | View | View |
| Pump / filter / feed controls | Optional override | Use controls |
| User account management | Edit | No access |
| Farm reports | Generate and export | No admin export access |
| System thresholds | Edit | Read-only |
| Feeding automation settings | Edit | Read-only |
| Notification settings | Edit | Read-only |
| History logs | View through reports | View through History tab |
| Growth analytics | View through reports | View through Growth tab |
