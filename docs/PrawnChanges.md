# PrawnChanges

Use this file as the prompt for changing the PrawnHub UI. Fill in the sections marked `TODO` before starting implementation.

## Prompt

```text
You are working on the Android project PrawnHub_V2.

Goal:
Change the Farmer and Admin UI based on the instructions in this file. Remove the UI parts listed under "Parts To Remove" and update the remaining screens to match the "New UI Direction".

Important:
- Keep the existing Firebase data connections working.
- Keep the existing role flow working.
- Keep Farmer and Admin behavior separated.
- Do not remove code or Firebase fields unless the removed UI no longer needs them.
- Do not change package names or activity names unless required.
- After changes, run a debug build and fix build errors.
```

## Current UI Reference

Use this file as the current UI baseline:

- `docs/current_ui_text_diagram.md`

## Parts To Remove

Fill this section with the UI parts you want removed.

```text
Farmer Dashboard:
- TODO: remove this part:
- TODO: remove this part:

Farmer History:
- TODO: remove this part:
- TODO: remove this part:

Farmer Growth:
- TODO: remove this part:
- TODO: remove this part:

Farmer Settings:
- TODO: remove this part:
- TODO: remove this part:

Admin Login:
- TODO: remove this part:
- TODO: remove this part:

Admin Dashboard:
- TODO: remove this part:
- TODO: remove this part:

Admin Reports:
- TODO: remove this part:
- TODO: remove this part:

Admin System Settings:
- TODO: remove this part:
- TODO: remove this part:
```

## Parts To Keep

```text
Shared:
- Welcome screen
- Role selection screen
- Farmer role opens Farmer Dashboard
- Admin role opens Admin Login
- Admin login verifies Firebase admin role

Farmer:
- Bottom navigation unless changed below
- DashboardActivity
- HistoryActivity
- GrowthActivity
- SettingsActivity in farmer read-only mode

Admin:
- AdminDashboardActivity
- ReportsActivity
- SettingsActivity in admin editable mode
- Admin-only SessionStore access checks
```

## New UI Direction

Describe the new UI you want here.

```text
Overall style:
- TODO: describe the new design style.

Colors:
- TODO: describe colors to use or avoid.

Typography:
- TODO: describe title/body/button text style.

Cards and sections:
- TODO: describe if cards should be bigger, smaller, flatter, etc.

Navigation:
- TODO: describe bottom navigation changes, if any.
```

## Farmer UI Change Prompt

```text
Update the Farmer UI.

Farmer Dashboard changes:
- Remove:
  - TODO
- Add or change:
  - TODO
- Keep:
  - Firebase sensor values from /aquarium and /ShrimpHub
  - Control writes to /control for pump, filter, aerator, and feed if those controls remain

Farmer History changes:
- Remove:
  - TODO
- Add or change:
  - TODO
- Keep:
  - Firebase /history listener
  - Range and sensor filtering unless replaced by a new filter UI

Farmer Growth changes:
- Remove:
  - TODO
- Add or change:
  - TODO
- Keep:
  - Firebase /growth, /growth_trend, /settings, and sensor health behavior unless removed from UI

Farmer Settings changes:
- Remove:
  - TODO
- Add or change:
  - TODO
- Keep:
  - Farmer read-only mode unless specifically changed
  - Back to Roles action
```

## Admin UI Change Prompt

```text
Update the Admin UI.

Admin Login changes:
- Remove:
  - TODO
- Add or change:
  - TODO
- Keep:
  - FirebaseAuth login
  - Admin role verification from /users
  - Forgot Password unless listed for removal

Admin Dashboard changes:
- Remove:
  - TODO
- Add or change:
  - TODO
- Keep:
  - Admin-only access check
  - Reports navigation
  - System Settings navigation
  - Logout action
  - Firebase sensor and feeding summaries unless listed for removal

Admin Reports changes:
- Remove:
  - TODO
- Add or change:
  - TODO
- Keep:
  - Date range selection unless replaced
  - Export buttons unless listed for removal

Admin System Settings changes:
- Remove:
  - TODO
- Add or change:
  - TODO
- Keep:
  - Save to /settings
  - Save to /thresholds
  - Admin editable mode
```

## Files To Edit

```text
Farmer Dashboard:
- app/src/main/res/layout/activity_dashboard.xml
- app/src/main/java/com/example/prawnhub_v2/DashboardActivity.java

Farmer History:
- app/src/main/res/layout/activity_history.xml
- app/src/main/res/layout/item_history.xml
- app/src/main/java/com/example/prawnhub_v2/HistoryActivity.java

Farmer Growth:
- app/src/main/res/layout/activity_growth.xml
- app/src/main/java/com/example/prawnhub_v2/GrowthActivity.java

Farmer/Admin Settings:
- app/src/main/res/layout/activity_settings.xml
- app/src/main/java/com/example/prawnhub_v2/SettingsActivity.java

Admin Login:
- app/src/main/res/layout/activity_login.xml
- app/src/main/java/com/example/prawnhub_v2/LoginActivity.java

Admin Dashboard:
- app/src/main/res/layout/activity_admin_dashboard.xml
- app/src/main/java/com/example/prawnhub_v2/AdminDashboardActivity.java

Admin Reports:
- app/src/main/res/layout/activity_reports.xml
- app/src/main/java/com/example/prawnhub_v2/ReportsActivity.java

Shared Navigation:
- app/src/main/res/layout/bottom_nav.xml
- app/src/main/java/com/example/prawnhub_v2/BaseNavActivity.java
```

## Firebase Fields To Preserve Unless UI Is Removed

```text
/aquarium
    temp_val
    tds_val
    salinity_val
    turb_val
    water_lvl
    oxygen_val
    do_val
    ph_val

/ShrimpHub
    temperature
    tds
    turbidity
    waterLevel

/settings
    max_temp
    min_temp
    max_sal
    min_sal
    max_turb
    feed_time
    feed_interval_hours
    overflow_limit
    min_oxygen
    min_ph
    max_ph
    sound_alert
    vibrate
    push_notifications
    midnight_feeding
    target_harvest_days

/thresholds
    temperature/min
    temperature/max
    salinity/min
    salinity/max
    turbidity/max
    oxygen/min
    ph/min
    ph/max
    water_level/max

/control
    pump_stat
    filter_stat
    aerator_stat
    motor_trig

/alerts
    ammonia_risk
    overflow_risk
    temp_alert
    sal_alert
    oxygen_alert
    turbidity_alert

/growth
    start_date
    days_in_optimal_temp
    days_of_culture
    performance_score
    water_quality_score

/history
    timestamp
    param_type
    rec_val
    status
```

## Build And Test Prompt

```text
After editing the UI:

1. Run:
   .\gradlew.bat assembleDebug

2. Fix all build errors.

3. Manually verify these flows:
   - Welcome -> Role Selection
   - Role Selection -> Farmer Dashboard
   - Farmer Dashboard -> History
   - Farmer Dashboard -> Growth
   - Farmer Dashboard -> Settings
   - Role Selection -> Admin Login
   - Admin Login -> Admin Dashboard
   - Admin Dashboard -> Reports
   - Admin Dashboard -> System Settings
   - Logout returns to Role Selection
```

## Final Notes For The Implementer

```text
Before changing code:
- Read this file fully.
- Read docs/current_ui_text_diagram.md.
- Read the affected XML layout and Java activity files.

While changing code:
- Update XML first.
- Update Java IDs only when needed.
- Keep changes focused on UI and related screen behavior.
- Do not delete unrelated Firebase logic.

Before finishing:
- Run assembleDebug.
- Report changed files.
- Report any UI parts that could not be removed safely.
```
