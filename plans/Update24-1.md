# PRAWNHUB UI UPDATE V3

## GLOBAL

* Add full Night Mode support.
* Theme toggle:

  * Light Mode
  * Dark Mode
  * Follow System
* Save preference locally.
* Apply to all activities.

---

# HISTORY PAGE

Keep existing layout and charts.

## ADD DATE RANGE FILTER

Place ABOVE Sensor Filter.

Layout:

Start Date [DatePicker]
End Date [DatePicker]

[Apply Filter]

Behavior:

* Filter chart data.
* Filter history list.
* Read from Firebase history timestamps.
* Default = Last 7 Days.

Firebase:

/ShrimpHub/history

timestamp

---

## SENSOR FILTER

Keep:

* All Sensors
* Temperature
* Salinity
* Turbidity
* Water Level

Filter both:

* Graphs
* History List

---

## CHARTS

Keep MPAndroidChart.

Realtime update.

Charts:

* Temperature Trend
* Salinity Trend
* Turbidity Trend
* Water Level Trend

Data Source:

/ShrimpHub/history

Newest data displayed last.

---

## HISTORY LIST

Keep below charts.

Display:

Timestamp
Sensor
Value
Status

Newest first.

Support:

timestamp
time
param_type
rec_val
value
status

---

# NOTIFICATION SETTINGS

## PUSH NOTIFICATIONS

If OFF:

* Stop local notifications.
* Stop alert notifications.
* No vibration.
* No sound.

If ON:

* Notifications active.
* Alert popups active.
* Background notifications active.

Firebase:

/settings/push_notifications

---

## VIBRATION

If OFF:

* No vibration.

If ON:

* Vibrate on alerts.

Firebase:

/settings/vibrate

---

## SOUND ALERT

If OFF:

* Silent alerts.

If ON:

* Play alert sound.

Firebase:

/settings/sound_alert

---

## IMPLEMENT REAL CONTROL

Notification behavior must follow settings instantly without requiring app restart.

Use SharedPreferences + Firebase sync.

---

# SETTINGS PAGE

Add:

Appearance Section

Theme

( ) Light
( ) Dark
( ) System Default

Save instantly.

Apply across:

Dashboard
History
Growth
Settings
Admin Pages

---

# PLANS DOCUMENT

After implementation create:

docs/FarmerFlowPlan.md

Include updated text diagram.

---

# UPDATED FARMER FLOW

Role Selection
|
+-- Farmer
|
+-- Dashboard
|      |
|      +-- Monitor Sensors
|      +-- Filter Pump
|      +-- Salt Water Pump
|      +-- Fresh Water Pump
|      +-- Feed Now
|
+-- History
|      |
|      +-- Date Range Filter
|      +-- Sensor Filter
|      +-- Realtime Charts
|      +-- History Records
|
+-- Growth
|      |
|      +-- Growth Analytics
|      +-- Health Score
|
+-- Settings
|
+-- Feeding Automation
+-- Notifications
+-- Theme Settings
+-- Read Only Pond Settings

Keep all Firebase paths unchanged.
