# Farmer Flow Plan

Updated for `Update24-1.md`.

```text
Role Selection
|
+-- Farmer
    |
    +-- Dashboard
    |      |
    |      +-- Monitor Sensors
    |      |      +-- Temperature
    |      |      +-- TDS / Salinity
    |      |      +-- Turbidity
    |      |      +-- Water Level
    |      |
    |      +-- Filter Pump
    |      |      +-- /ShrimpHub/control/pump1
    |      |
    |      +-- Salt Water Pump
    |      |      +-- /ShrimpHub/control/pump2
    |      |
    |      +-- Fresh Water Pump
    |      |      +-- /ShrimpHub/control/pump3
    |      |
    |      +-- Feed Now
    |             +-- /ShrimpHub/feeder/feedNow
    |             +-- /ShrimpHub/feeder/feeding
    |
    +-- History
    |      |
    |      +-- Date Range Filter
    |      |      +-- Default: last 7 days
    |      |      +-- Start Date picker
    |      |      +-- End Date picker
    |      |      +-- Apply Filter
    |      |
    |      +-- Sensor Filter
    |      |      +-- All Sensors
    |      |      +-- Temperature
    |      |      +-- Salinity
    |      |      +-- Turbidity
    |      |      +-- Water Level
    |      |
    |      +-- Realtime Charts
    |      |      +-- Temperature Trend
    |      |      +-- Salinity Trend
    |      |      +-- Turbidity Trend
    |      |      +-- Water Level Trend
    |      |
    |      +-- History Records
    |             +-- Timestamp
    |             +-- Sensor
    |             +-- Value
    |             +-- Status
    |
    +-- Growth
    |      |
    |      +-- Growth Analytics
    |      +-- Health Score
    |      +-- Weekly Growth Trend
    |      +-- Recommendations
    |      +-- Set Start Date
    |
    +-- Settings
           |
           +-- Feeding Automation
           |      +-- Feed Time
           |      +-- Feeding Interval
           |      +-- Auto Feed
           |
           +-- Notifications
           |      +-- Push Notifications
           |      |      +-- OFF stops local alert notifications
           |      |
           |      +-- Vibrate
           |      |      +-- OFF removes vibration from alerts
           |      |
           |      +-- Sound Alert
           |             +-- OFF makes alerts silent
           |
           +-- Theme Settings
           |      +-- Light Mode
           |      +-- Dark Mode
           |      +-- Follow System
           |      +-- Saved locally with SharedPreferences
           |
           +-- Read Only Pond Settings
                  +-- Temperature Range
                  +-- Salinity Range
                  +-- Turbidity Range
                  +-- Overflow Limit
```

## Firebase Paths Kept

```text
/ShrimpHub/control/pump1
/ShrimpHub/control/pump2
/ShrimpHub/control/pump3

/ShrimpHub/feeder/feedNow
/ShrimpHub/feeder/feeding

/ShrimpHub/history
    timestamp
    time
    param_type
    rec_val
    value
    status
    temperature
    tds
    turbidity
    waterLevel

/settings/push_notifications
/settings/vibrate
/settings/sound_alert

/settings
/thresholds
```

## Farmer Permission Summary

```text
Editable:
    Feeding Automation
    Notifications
    Theme Settings

Read-only:
    Pond Settings
    Threshold ranges
```
