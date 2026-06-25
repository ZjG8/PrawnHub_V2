# PRAWNHUB HISTORY UPDATE V4

## HISTORY PAGE

Keep existing History page.

### Date & Time Range Filter

Replace current date-only filter with DateTime filter.

Layout:

Start Date & Time
[MM/DD/YYYY HH:MM AM/PM]

End Date & Time
[MM/DD/YYYY HH:MM AM/PM]

[Apply Filter]

Default:

* End = Current DateTime
* Start = Current DateTime - 7 Days

Behavior:

* Filter charts using selected DateTime range.
* Filter history records using selected DateTime range.
* Support precise filtering down to minutes and seconds.
* User can view all records from any specific time period.

Use:

MaterialDatePicker
+
MaterialTimePicker

Combine into Unix Timestamp.

---

## FIREBASE HISTORY VALIDATION

Verify HistoryActivity reads data from:

/ShrimpHub/history

Supported formats:

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

---

## SENSOR DATA VERIFICATION

Add validation to confirm ESP32 data is reaching Firebase.

Before chart rendering:

Check:

temperature exists
tds exists
turbidity exists
waterLevel exists

If missing:

Show:

"No sensor data available."

instead of empty graph.

---

## HISTORY LOADING FIX

Current issue:

Some records are not displayed.

Implement:

* Order by timestamp.
* Sort newest first.
* Parse both timestamp and legacy time fields.
* Ignore malformed records.
* Load complete dataset before applying filters.

---

## REALTIME CHARTS

Charts:

* Temperature Trend
* Salinity Trend
* Turbidity Trend
* Water Level Trend

Requirements:

* Auto refresh on Firebase changes.
* Respect DateTime range filter.
* Respect Sensor Filter.
* Support large history datasets.

Source:

/ShrimpHub/history

---

## DEBUG LOGGING

Add temporary logs:

HistoryActivity

Log:

Record count loaded
Timestamp range loaded
Filtered record count
Firebase listener status

Tag:

PRAWNHUB_HISTORY

---

## EXPECTED FLOW

History
|
+-- DateTime Filter
|      +-- Start Date
|      +-- Start Time
|      +-- End Date
|      +-- End Time
|      +-- Apply
|
+-- Sensor Filter
|
+-- Firebase Load
|
+-- Validate Data
|
+-- Render Charts
|
+-- Render History List
|
+-- Realtime Updates

Goal:

Display ALL available sensor records accurately from Firebase and allow precise filtering by both date and time.
