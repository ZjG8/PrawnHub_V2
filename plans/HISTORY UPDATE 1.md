# PrawnHub V2 - History Page UI Redesign (Sensor Log System)

## Objective
Refactor the Farmer `HistoryActivity` UI from a graph-based system into a real-time **sensor log stream interface** similar to IoT monitoring dashboards.

The goal is to make Firebase data appear as **live system logs / notifications**, not charts.

---

## Target Activity
- Activity: `HistoryActivity`
- Layout: `activity_history.xml`

---

## ❌ REMOVE (Current UI Elements)
- Line Chart section
- Graph visualization completely
- “Important Events” section title
- Any static grouped summary blocks

---

## ✅ NEW DESIGN: SENSOR LOG STREAM

Replace the entire middle section with a **real-time Firebase log feed**.

### Layout Structure

```text
+--------------------------------------------------+
| [Back]                                           |
| History - Sensor Logs                            |
+--------------------------------------------------+
| [Date Range Picker]     [Sensor Filter Dropdown] |
| Start Date - End Date   Temperature / Salinity   |
|                         Turbidity / Water Level  |
+--------------------------------------------------+
| LIVE SENSOR LOG STREAM (Firebase Realtime)      |
+--------------------------------------------------+
| 🔔 12:45:10 PM                                   |
| Temperature recorded: 28°C                       |
| Status: Normal                                   |
+--------------------------------------------------+
| 🔔 12:44:55 PM                                   |
| Salinity detected: 32 ppm                        |
| Status: Stable                                   |
+--------------------------------------------------+
| ⚠ 12:44:20 PM                                    |
| Turbidity spike detected: 65 NTU                 |
| Status: Warning                                  |
+--------------------------------------------------+
| 🔔 12:43:10 PM                                   |
| Water Level: 18 cm                               |
| Status: Normal                                   |
+--------------------------------------------------+
| ⚠ 12:42:30 PM                                    |
| Temperature below threshold                      |
| Status: Alert                                    |
+--------------------------------------------------+
| (Auto-scroll newest Firebase updates)           |
+--------------------------------------------------+
| Compact List View (Optional)                     |
| Parameter | Time | Value | Status               |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+