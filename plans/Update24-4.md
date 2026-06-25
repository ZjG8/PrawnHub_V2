# PRAWNHUB HISTORY FIX

## Problem

History data exists in Firebase but does not appear in HistoryActivity.

ESP32 writes logs to:

/ShrimpHub/history

while some app components may still read:

/history

There are currently two history nodes:

/ShrimpHub/history
/history

This causes the app to read the wrong location.

---

## Required Fix

### 1. Use ONLY ONE history source

Remove all references to:

/history

Use ONLY:

/ShrimpHub/history

for charts, filters, analytics, and history records.

---

### 2. Verify Android Firebase Query

All history screens must read:

DatabaseReference historyRef =
FirebaseDatabase.getInstance()
.getReference("ShrimpHub")
.child("history");

Do not read from root-level /history.

---

### 3. Support Current ESP32 Structure

Current history records contain:

temperature
tds
turbidity
waterLevel
timestamp

Example:

{
"temperature": 28.4,
"tds": 22.1,
"turbidity": 11.3,
"waterLevel": 1.5,
"timestamp": 1778686057
}

HistoryActivity must parse this structure directly.

Do not require:

param_type
value
rec_val
status

because the ESP32 does not generate those fields.

---

### 4. History List Builder

Expand each history row into four records:

Temperature
Salinity (TDS)
Turbidity
Water Level

Example:

Timestamp: 2026-06-24 15:27:00
Sensor: Temperature
Value: 28.4 °C

Timestamp: 2026-06-24 15:27:00
Sensor: Salinity
Value: 22.1 ppt

Timestamp: 2026-06-24 15:27:00
Sensor: Turbidity
Value: 11.3 NTU

Timestamp: 2026-06-24 15:27:00
Sensor: Water Level
Value: 1.5 ft

---

### 5. Date + Time Filter

Replace date-only filtering.

Use:

Start DateTime Picker
End DateTime Picker

Format:

yyyy-MM-dd HH:mm

Filter using Unix timestamp.

Convert picker values into Unix seconds and compare against:

timestamp

inside each history record.

---

### 6. Charts

Charts must use:

/ShrimpHub/history

Temperature Chart:
x = timestamp
y = temperature

Salinity Chart:
x = timestamp
y = tds

Turbidity Chart:
x = timestamp
y = turbidity

Water Level Chart:
x = timestamp
y = waterLevel

Default chart range:

Last 7 Days

---

### 7. Debug Logging

Add Logcat debugging:

History count loaded
History path used
Timestamp parsed
Chart entries created

Display message:

"No records found for selected date/time range"

when result count = 0.

---

### 8. Firebase Verification Screen

Temporarily print:

Latest timestamp
Temperature
TDS
Turbidity
Water Level

from the newest history record.

This confirms the app is successfully reading Firebase.
