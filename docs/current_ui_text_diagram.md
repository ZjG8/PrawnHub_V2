# Current Farmer and Admin UI Text Diagram

This document maps the current Android UI from the app layouts and activity code.

## Entry Flow

```text
+--------------------------------------------------+
| WelcomeActivity                                  |
| App launch screen                                |
+--------------------------------------------------+
        |
        v
+--------------------------------------------------+
| RoleSelectionActivity                            |
|                                                  |
| [Back]                                           |
| Choose Role                                      |
| Select how you want to continue                  |
|                                                  |
| +----------------------------------------------+ |
| | ADMIN                                        | |
| | Manage system and monitoring                 | |
| +----------------------------------------------+ |
|                                                  |
| +----------------------------------------------+ |
| | FARMER                                       | |
| | View pond and shrimp conditions              | |
| +----------------------------------------------+ |
+--------------------------------------------------+
        |
        +-- Tap FARMER
        |       |
        |       +-- SessionStore role = "farmer"
        |       v
        |   DashboardActivity
        |
        +-- Tap ADMIN
                |
                v
            LoginActivity
```

## Farmer UI

### Farmer Navigation

```text
Session role: farmer
        |
        v
+--------------------------------------------------+
| Farmer Area                                      |
|                                                  |
| Bottom navigation tabs:                          |
| [Dashboard] [History] [Growth] [Settings]        |
+--------------------------------------------------+

+-------------+     +----------+     +--------+     +----------+
| Dashboard   | <-> | History  | <-> | Growth | <-> | Settings |
+-------------+     +----------+     +--------+     +----------+
       ^                  ^              ^              ^
       |                  |              |              |
       +------------------+--------------+--------------+
                         Bottom navigation
```

### Farmer Dashboard

Activity: `DashboardActivity`  
Layout: `activity_dashboard.xml`

```text
+--------------------------------------------------+
| U                    Dashboard              Logo |
+--------------------------------------------------+
| Pond Status                                      |
| Healthy / Warning / Danger                       |
+--------------------------------------------------+
| Alert Banner                                     |
| Hidden when no active alert                      |
| Shows active Firebase alerts when present        |
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

Data and actions:

```text
Firebase /aquarium
    -> temp_val
    -> tds_val
    -> turb_val
    -> water_lvl
    -> oxygen_val or do_val
    -> ph_val

Firebase /ShrimpHub fallback
    -> temperature
    -> tds
    -> turbidity
    -> waterLevel

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
    -> motor_trig = true when Feed Now is tapped
```

### Farmer History

Activity: `HistoryActivity`  
Layout: `activity_history.xml`

```text
+--------------------------------------------------+
| [Back]                                           |
| History                                          |
+--------------------------------------------------+
| [Range Spinner]          [Sensor Spinner]        |
| Today / Week / Month     Temperature             |
|                           Salinity                |
|                           Turbidity               |
|                           Water Level             |
|                           Oxygen                  |
|                           pH                      |
+--------------------------------------------------+
| Important Events                                 |
+--------------------------------------------------+
| Line Chart                                       |
| Last 50 filtered Firebase history entries        |
+--------------------------------------------------+
| Event List                                       |
| +----------------------------------------------+ |
| | Parameter                                    | |
| | Timestamp                                    | |
| | Value                                        | |
| | Status / Important alert                     | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

Data:

```text
Firebase /history
    -> last 50 records ordered by timestamp
    -> timestamp
    -> param_type
    -> rec_val
    -> status

Displayed records are important events only:
    -> non-normal status
    -> oxygen < 5
    -> salinity < 15 or > 25
    -> water_level > 5
    -> turbidity > 45
    -> temp < 28 or > 32
    -> ph < 6.5 or > 8.5
```

### Farmer Growth

Activity: `GrowthActivity`  
Layout: `activity_growth.xml`

```text
+--------------------------------------------------+
| Growth Analytics                                 |
+--------------------------------------------------+
| Estimated Growth                                 |
+--------------------------------------------------+
| Health Progress                                  |
|                                                  |
|              [Horizontal progress]               |
|              Performance score / health          |
|              Days of culture                     |
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

Data and actions:

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

Firebase /ShrimpHub fallback
    -> temperature
    -> tds
    -> turbidity

Firebase /growth_trend
    -> last 7 score records

Set Start Date
    -> opens DatePickerDialog
    -> writes yyyy-MM-dd to /growth/start_date
```

### Farmer Settings

Activity: `SettingsActivity`  
Layout: `activity_settings.xml`

```text
+--------------------------------------------------+
| Monitoring Settings                         Logo |
+--------------------------------------------------+
| Account                                          |
| user@email.com                                   |
+--------------------------------------------------+
| Pond Settings                                    |
| Max Temperature         [read-only value]         |
| Min Temperature         [read-only value]         |
| Max Salinity            [read-only value]         |
| Min Salinity            [read-only value]         |
| Max Turbidity           [read-only value]         |
| Min Oxygen              [read-only value]         |
| Max pH                  [read-only value]         |
| Min pH                  [read-only value]         |
| Overflow Limit          [read-only value]         |
+--------------------------------------------------+
| Feeding Automation                               |
| Feed Time               [read-only value]         |
| Feeding Interval        [read-only value]         |
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

Farmer mode behavior:

```text
Session role != "admin"
    |
    v
SettingsActivity
    |
    +-- Title = "Monitoring Settings"
    +-- Back button hidden
    +-- Bottom navigation visible
    +-- Save Settings button hidden
    +-- Input fields disabled
    +-- Notification and feeding switches disabled
    +-- Logout button text = "Back to Roles"
```

## Admin UI

### Admin Login

Activity: `LoginActivity`  
Layout: `activity_login.xml`

```text
+--------------------------------------------------+
| [Back]                                           |
|                         Logo                     |
| Login                                            |
| Admin Login                                      |
+--------------------------------------------------+
| Email                                            |
| [Enter your email]                               |
+--------------------------------------------------+
| Password                                         |
| [Enter your password]                            |
+--------------------------------------------------+
| [Show Password / Hide Password]                  |
| [Login]                                          |
| Error message area                               |
| [Forgot Password?]                               |
+--------------------------------------------------+
```

Admin verification:

```text
FirebaseAuth sign in
    |
    v
Check Firebase /users/{uid}/role
    |
    +-- role == "admin"
    |       |
    |       +-- SessionStore role = "admin"
    |       +-- open AdminDashboardActivity
    |
    +-- otherwise check /users by email
            |
            +-- matching role == "admin"
            |       |
            |       +-- write user email and role to /users/{uid}
            |       +-- SessionStore role = "admin"
            |       +-- open AdminDashboardActivity
            |
            +-- no admin role
                    |
                    +-- sign out
                    +-- show "This account is not registered as an admin."
```

### Admin Dashboard

Activity: `AdminDashboardActivity`  
Layout: `activity_admin_dashboard.xml`

```text
+--------------------------------------------------+
| A                  Admin                    Logo |
+--------------------------------------------------+
| ADMIN DASHBOARD                                  |
+--------------------------------------------------+
| ONLINE                    | SYNCHRONIZED         |
+--------------------------------------------------+
| Quick Actions                                    |
+--------------------------------------------------+
| Generate Farm Reports                            |
| Export PDF and Excel reports                     |
+--------------------------------------------------+
| System Settings                                  |
| Thresholds, feeding, and notifications           |
+--------------------------------------------------+
| Recent Alerts                                    |
| Review latest sensor warnings                    |
+--------------------------------------------------+
| Live Sensor Summary                              |
+--------------------------------------------------+
| Temperature              | Salinity              |
| -- C                     | -- ppt                |
+--------------------------+-----------------------+
| Water Lv.                | Turbidity             |
| -- cm                    | -- NTU                |
+--------------------------------------------------+
| Feeding Automation                               |
| Feed Time: --                                    |
| Feeding Interval: --                             |
| Midnight Auto Feed: --                           |
+--------------------------------------------------+
| Logout                                           |
+--------------------------------------------------+
```

Dashboard actions:

```text
Generate Farm Reports
    -> ReportsActivity

System Settings
    -> SettingsActivity in admin mode

Recent Alerts
    -> Toast: "Recent alerts summary can be wired to Firebase alerts next."

Logout
    -> clear SessionStore
    -> FirebaseAuth.signOut()
    -> write /auth/session = "logged_out"
    -> RoleSelectionActivity
```

Data:

```text
Firebase /aquarium
    -> temp_val
    -> salinity_val or tds_val
    -> water_lvl
    -> turb_val

Firebase /ShrimpHub fallback
    -> temperature
    -> tds
    -> waterLevel
    -> turbidity

Firebase /settings
    -> feed_time
    -> feed_interval_hours
    -> midnight_feeding
```

Admin access rule:

```text
Open AdminDashboardActivity or ReportsActivity
    |
    v
Check SessionStore role
    |
    +-- role == "admin" --> Allow screen
    |
    +-- role != "admin" --> finish screen
```

### Admin Reports

Activity: `ReportsActivity`  
Layout: `activity_reports.xml`

```text
+--------------------------------------------------+
| [<] Farm Reports                            Logo |
+--------------------------------------------------+
| Report Range                                     |
| Start Date              [date picker value]      |
| End Date                [date picker value]      |
+--------------------------------------------------+
| Report Preview                                   |
| Includes temperature, salinity, turbidity,       |
| oxygen, water level, pH, alerts, and growth      |
| analytics.                                       |
+--------------------------------------------------+
| [Export PDF]                                     |
| [Export Excel]                                   |
+--------------------------------------------------+
```

Report behavior:

```text
Open ReportsActivity
    |
    +-- default start date = today - 7 days
    +-- default end date = today
    +-- tapping either date opens DatePickerDialog
    +-- Export PDF shows placeholder toast
    +-- Export Excel shows placeholder toast
```

### Admin System Settings

Activity: `SettingsActivity`  
Layout: `activity_settings.xml`

```text
+--------------------------------------------------+
| [<] System Settings                         Logo |
+--------------------------------------------------+
| Account                                          |
| admin@email.com                                  |
+--------------------------------------------------+
| Pond Settings                                    |
| Max Temperature         [editable]               |
| Min Temperature         [editable]               |
| Max Salinity            [editable]               |
| Min Salinity            [editable]               |
| Max Turbidity           [editable]               |
| Min Oxygen              [editable]               |
| Max pH                  [editable]               |
| Min pH                  [editable]               |
| Overflow Limit          [editable]               |
+--------------------------------------------------+
| Feeding Automation                               |
| Feed Time               [time picker]            |
| Feeding Interval        [editable]               |
| [Switch] Auto-feed during sleep mode             |
+--------------------------------------------------+
| Notifications                                    |
| [Switch] Sound alert                             |
| [Switch] Vibrate                                 |
| [Switch] Push notifications                      |
+--------------------------------------------------+
| [Button] About PrawnHub                          |
| [Button] Save Settings                           |
| [Button] Logout                                  |
+--------------------------------------------------+
```

Admin mode behavior:

```text
Session role == "admin"
    |
    v
SettingsActivity
    |
    +-- Title = "System Settings"
    +-- Back button visible
    +-- Bottom navigation hidden
    +-- Save Settings button visible
    +-- Input fields editable
    +-- Notification and feeding switches enabled
    +-- Logout button text = "Logout"
```

Settings data:

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
    -> esp32_feeding_scheduler

Firebase /thresholds
    -> temperature/min, temperature/max
    -> salinity/min, salinity/max
    -> turbidity/max
    -> oxygen/min
    -> ph/min, ph/max
    -> water_level/max
```

## Activity Map

| Area | Screen | Activity | Layout |
|---|---|---|---|
| Shared | Welcome | `WelcomeActivity` | `activity_welcome.xml` |
| Shared | Role selection | `RoleSelectionActivity` | `activity_role_selection.xml` |
| Admin | Login | `LoginActivity` | `activity_login.xml` |
| Admin | Dashboard | `AdminDashboardActivity` | `activity_admin_dashboard.xml` |
| Admin | Farm reports | `ReportsActivity` | `activity_reports.xml` |
| Admin / Farmer | Settings | `SettingsActivity` | `activity_settings.xml` |
| Farmer | Dashboard | `DashboardActivity` | `activity_dashboard.xml` |
| Farmer | History | `HistoryActivity` | `activity_history.xml` |
| Farmer | Growth | `GrowthActivity` | `activity_growth.xml` |
| Shared | About | `AboutActivity` | `activity_about.xml` |
