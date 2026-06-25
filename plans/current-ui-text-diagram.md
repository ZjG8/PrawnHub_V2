# Current UI Text Diagram

Generated from the current Android XML layouts and Java activity wiring.

## App Entry Flow

```text
+--------------------------------------------------+
| WelcomeActivity                                  |
| Layout: activity_welcome.xml                     |
|                                                  |
| PrawnHub                                         |
| Smart Aquaculture Monitoring                     |
| Monitor - Analyze - Grow                         |
|                                                  |
| [Get Started]                                    |
+--------------------------------------------------+
        |
        v
+--------------------------------------------------+
| RoleSelectionActivity                            |
| Layout: activity_role_selection.xml              |
|                                                  |
| [Back]                                           |
| Choose Role                                      |
| Select how you want to continue                  |
|                                                  |
| +----------------------------------------------+ |
| | A  ADMIN                                    | |
| |    Manage system and monitoring             | |
| +----------------------------------------------+ |
|                                                  |
| +----------------------------------------------+ |
| | F  FARMER                                   | |
| |    View pond and shrimp conditions          | |
| +----------------------------------------------+ |
+--------------------------------------------------+
        |
        +-- ADMIN  -> LoginActivity -> AdminDashboardActivity
        |
        +-- FARMER -> SessionStore role = farmer
                     -> DashboardActivity
```

## Shared Navigation Model

```text
Farmer role
    |
    +-- DashboardActivity
    +-- HistoryActivity
    +-- GrowthActivity
    +-- SettingsActivity in Monitoring Settings mode
    |
    +-- Bottom nav:
        [Dashboard] [History] [Growth] [Settings]

Admin role
    |
    +-- AdminDashboardActivity
    +-- ReportsActivity
    +-- ManageUsersActivity
    +-- SettingsActivity in System Settings mode
    |
    +-- Admin screens use back buttons/cards instead of bottom nav.
```

## Farmer Dashboard

Activity: `DashboardActivity`  
Layout: `activity_dashboard.xml`

```text
+--------------------------------------------------+
| PrawnHub V2                                      |
| [Role: Farmer/Admin] [Firebase Waiting/Online]   |
| [ESP32 Waiting/Online]                           |
+--------------------------------------------------+
| Pond Status                                      |
| Healthy / Warning / Danger                       |
+--------------------------------------------------+
| Alert Banner                                     |
| Hidden when no active sensor alert               |
| Active alerts: temperature / TDS / turbidity /   |
| water level                                      |
+--------------------------------------------------+
| Sensor Cards                                     |
| +----------------------+ +---------------------+ |
| | Temperature          | | TDS                 | |
| | -- C                 | | -- ppm              | |
| | Stable/Normal/etc.   | | Normal/Warning      | |
| +----------------------+ +---------------------+ |
| +----------------------+ +---------------------+ |
| | Turbidity            | | Water Level         | |
| | -- NTU               | | -- cm               | |
| | Normal/Critical      | | Normal/Warning      | |
| +----------------------+ +---------------------+ |
+--------------------------------------------------+
| System Controls                                  |
| [Switch] Pump 1                                  |
| [Switch] Pump 2                                  |
+--------------------------------------------------+
| Automatic Feeder                                 |
| Automatic every 6 hours                          |
| Stepper cycle: 512 forward steps + 16 reverse    |
| correction                                       |
| Handled locally by ESP32; no Firebase manual     |
| feed command is used by this firmware.           |
+--------------------------------------------------+
| Alerts                                           |
| No water quality alerts                          |
| Feeder: automatic every 6 hours                  |
+--------------------------------------------------+
| Growth Status                                    |
| Estimated prawn health and recommendation text   |
+--------------------------------------------------+
| History Preview                                  |
| Latest 5 ShrimpHub history rows                  |
+--------------------------------------------------+
| Admin Tools                                      |
| Visible only when SessionStore role = admin      |
| User summary loading / N registered users        |
| Settings threshold preview                       |
| [Manage Users] [Export Report]                   |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

Primary Firebase paths:

```text
/ShrimpHub
    temperature
    tds
    turbidity
    waterLevel

/ShrimpHub/control
    pump1
    pump2

/ShrimpHub/history
    timestamp
    temperature
    tds
    turbidity
    waterLevel

/settings
    min_temp
    max_temp
    min_sal
    max_sal
    max_turb
    overflow_limit

/users
    counted for the admin dashboard section
```

## Farmer History

Activity: `HistoryActivity`  
Layout: `activity_history.xml`

```text
+--------------------------------------------------+
| [Back]                                           |
| History                                          |
+--------------------------------------------------+
| Sensor Logs                                      |
+--------------------------------------------------+
| Date Range                                       |
| [Start: MM/dd/yyyy hh:mm AM/PM]                  |
| [End:   MM/dd/yyyy hh:mm AM/PM]                  |
+--------------------------------------------------+
| [Sensor Filter Spinner]                          |
| All Sensors                                      |
| Temperature                                      |
| Salinity                                         |
| Turbidity                                        |
| Water Level                                      |
+--------------------------------------------------+
| LIVE SENSOR LOG STREAM                           |
+--------------------------------------------------+
| RecyclerView                                     |
| +----------------------------------------------+ |
| | Parameter                                    | |
| | Timestamp                                    | |
| | Value                                        | |
| | Status                                       | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

History reads the last 100 rows from `/ShrimpHub/history`, expands each row into separate temperature, salinity/TDS, turbidity, and water level items, then filters by date/time range and selected sensor.

## Farmer Growth

Activity: `GrowthActivity`  
Layout: `activity_growth.xml`

```text
+--------------------------------------------------+
| Growth Analytics                                 |
+--------------------------------------------------+
| Estimated Growth                                 |
+--------------------------------------------------+
| Health Progress                                  |
| [Horizontal progress bar]                        |
| Performance score: 0%                            |
| 0 days                                           |
+--------------------------------------------------+
| Weekly Growth Trend                              |
| Harvest readiness will be calculated from        |
| Firebase data.                                   |
| [Line chart]                                     |
+--------------------------------------------------+
| Status Badge                                     |
| Needs Attention - Check parameters now.          |
+--------------------------------------------------+
| Recommendations                                  |
| Maintain stable salinity, clear water, and       |
| target temperature.                              |
+--------------------------------------------------+
| [Set Start Date]                                 |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

## Settings Screen

Activity: `SettingsActivity`  
Layout: `activity_settings.xml`

This screen changes behavior based on `SessionStore.getRole()`.

```text
+--------------------------------------------------+
| [< only in admin mode] Settings title       Logo |
| Title = System Settings for admin                |
| Title = Monitoring Settings for farmer           |
+--------------------------------------------------+
| Role                                             |
| admin email or Farmer                            |
+--------------------------------------------------+
| Pond Settings                                    |
| Temperature                                      |
| [RangeSlider 0..50, step 0.5]                    |
| Min: 28.0 C        Max: 32.0 C                   |
|                                                  |
| Salinity                                         |
| [RangeSlider 0..50, step 1]                      |
| Min: 15 ppt        Max: 25 ppt                   |
|                                                  |
| Turbidity Limit                                  |
| Current: 45 NTU                                  |
| [Slider 0..100]                                  |
|                                                  |
| Overflow Limit                                   |
| Current: 5 cm                                    |
| [Slider 0..100]                                  |
+--------------------------------------------------+
| Feeding Automation                               |
| Feed Time [time picker field]                    |
| Feeding Interval [number field]                  |
| [Switch] Auto-feed during sleep mode             |
+--------------------------------------------------+
| Notifications                                    |
| [Switch] Sound alert                             |
| [Switch] Vibrate                                 |
| [Switch] Push notifications                      |
+--------------------------------------------------+
| [About PrawnHub]                                 |
| [Save Settings] visible only in admin mode       |
| [Logout / Back to Roles]                         |
+--------------------------------------------------+
| Farmer mode only: Dashboard | History | Growth | |
| Settings                                         |
+--------------------------------------------------+
```

Mode differences:

```text
Farmer mode
    title = Monitoring Settings
    back button hidden
    bottom nav visible
    save button hidden
    sliders, inputs, and switches disabled
    logout button text = Back to Roles

Admin mode
    title = System Settings
    back button visible
    bottom nav hidden
    save button visible
    sliders, inputs, and switches enabled
    logout button text = Logout
```

Settings writes mirrored values to both `/settings` and `/thresholds`.

## Admin Login

Activity: `LoginActivity`  
Layout: `activity_login.xml`

```text
+--------------------------------------------------+
| [Back]                                           |
| Login                                            |
| Continue as Farmer                               |
+--------------------------------------------------+
| Email                                            |
| [email input]                                    |
+--------------------------------------------------+
| Password                                         |
| [password input]                                 |
+--------------------------------------------------+
| [Show Password]                                  |
| [Login]                                          |
| Error message area                               |
| [Forgot Password?]                               |
| [Create Account]                                 |
+--------------------------------------------------+
```

Successful login requires Firebase Auth plus an admin role in `/users`.

## Admin Dashboard

Activity: `AdminDashboardActivity`  
Layout: `activity_admin_dashboard.xml`

```text
+--------------------------------------------------+
| A  Admin                                    Logo |
+--------------------------------------------------+
| ADMIN DASHBOARD                                  |
+--------------------------------------------------+
| ONLINE                    | SYNCHRONIZED         |
+--------------------------------------------------+
| Quick Actions                                    |
| +----------------------------------------------+ |
| | Generate Farm Reports                        | |
| | Export PDF and Excel reports                 | |
| +----------------------------------------------+ |
| +----------------------------------------------+ |
| | System Settings                              | |
| | Thresholds, feeding, and notifications       | |
| +----------------------------------------------+ |
| +----------------------------------------------+ |
| | Recent Alerts                                | |
| | Review latest sensor warnings                | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| Live Sensor Summary                              |
| +----------------------+ +---------------------+ |
| | Temperature          | | Salinity            | |
| | -- C                 | | -- ppt              | |
| +----------------------+ +---------------------+ |
| +----------------------+ +---------------------+ |
| | Water Lv.            | | Turbidity           | |
| | -- cm                | | -- NTU              | |
| +----------------------+ +---------------------+ |
+--------------------------------------------------+
| Feeding Automation                               |
| Feed Time: --                                    |
| Feeding Interval: --                             |
| Midnight Auto Feed: --                           |
+--------------------------------------------------+
| Logout                                           |
+--------------------------------------------------+
```

Admin dashboard actions:

```text
Generate Farm Reports -> ReportsActivity
System Settings       -> SettingsActivity in admin mode
Recent Alerts         -> placeholder toast
Logout                -> clear session, sign out, return to RoleSelectionActivity
```

## Manage Users

Activity: `ManageUsersActivity`  
Layout: `activity_manage_users.xml`

```text
+--------------------------------------------------+
| [<] Manage User Accounts                         |
+--------------------------------------------------+
| [Search users input]                             |
| [Add User]                                       |
+--------------------------------------------------+
| Users Container                                  |
| +----------------------------------------------+ |
| | user@email.com                               | |
| | Role: admin/farmer  Status: active/disabled | |
| | Registered: timestamp/date                   | |
| | [Edit] [Toggle] [Delete]                     | |
| +----------------------------------------------+ |
+--------------------------------------------------+
```

Admin-only behavior:

```text
Open screen
    |
    +-- role != admin -> finish()
    |
    +-- role == admin -> listen to /users

Add User
    -> dialog with email, registeredAt, role, status
    -> pushes a new record under /users

Edit
    -> assign role dialog: admin or farmer

Toggle
    -> active <-> disabled

Delete
    -> remove user record from /users/{uid}
```

## Farm Reports

Activity: `ReportsActivity`  
Layout: `activity_reports.xml`

```text
+--------------------------------------------------+
| [<] Farm Reports                            Logo |
+--------------------------------------------------+
| Report Range                                     |
| Start Date [date picker input]                   |
| End Date   [date picker input]                   |
+--------------------------------------------------+
| Report Preview                                   |
| Includes temperature, salinity, turbidity, water |
| level, alerts, and growth analytics.             |
+--------------------------------------------------+
| [Export PDF]                                     |
| [Export Excel]                                   |
+--------------------------------------------------+
```

Exports currently show placeholder toasts.

## About Screen

Activity: `AboutActivity`  
Layout: `activity_about.xml`

```text
+--------------------------------------------------+
| [Back]                                           |
+--------------------------------------------------+
| PrawnHub                                         |
| Version 1.0                                      |
| Smart Monitoring and Control System for Indoor   |
| Prawn Farming                                    |
+--------------------------------------------------+
| Developed By                                     |
| Zj Gilbuena                                      |
| Mikko Garduna                                    |
| Jovin Barretto                                   |
| Franz Velasco                                    |
|                                                  |
| School                                           |
| Polytechnic University of the Philippines Lopez, |
| Quezon Branch                                    |
+--------------------------------------------------+
| Technologies Used                                |
| - ESP32                                          |
| - Firebase                                       |
| - Android Studio                                 |
+--------------------------------------------------+
| [Contact Team]                                   |
+--------------------------------------------------+
```

## Activity And Layout Index

| Area | Screen | Activity | Layout |
|---|---|---|---|
| Shared | Welcome | `WelcomeActivity` | `activity_welcome.xml` |
| Shared | Role selection | `RoleSelectionActivity` | `activity_role_selection.xml` |
| Admin | Login | `LoginActivity` | `activity_login.xml` |
| Admin | Admin dashboard | `AdminDashboardActivity` | `activity_admin_dashboard.xml` |
| Admin | Manage users | `ManageUsersActivity` | `activity_manage_users.xml` |
| Admin | Farm reports | `ReportsActivity` | `activity_reports.xml` |
| Admin/Farmer | Settings | `SettingsActivity` | `activity_settings.xml` |
| Farmer/Admin role-aware | Dashboard | `DashboardActivity` | `activity_dashboard.xml` |
| Farmer | History | `HistoryActivity` | `activity_history.xml` |
| Farmer | Growth | `GrowthActivity` | `activity_growth.xml` |
| Shared | About | `AboutActivity` | `activity_about.xml` |
