# PrawnHub History Screen - Text UI Diagram

Screen: `HistoryActivity` / `activity_history.xml`

```text
+--------------------------------------------------+
| [Back]                                           |
+--------------------------------------------------+
| History                                          |
+--------------------------------------------------+
| DATE & TIME RANGE                                |
| +----------------------------------------------+ |
| | Start Date & Time                             | |
| | [ yyyy-MM-dd HH:mm ]                          | |
| | End Date & Time                               | |
| | [ yyyy-MM-dd HH:mm ]                          | |
| | [ Apply Filter ]                              | |
| +----------------------------------------------+ |
| [ All Sensors v ]                                |
+--------------------------------------------------+
| GRAPH ANALYTICS                                  |
| Temperature Trend                                |
| +----------------------------------------------+ |
| | Line chart / “No sensor data available.”     | |
| +----------------------------------------------+ |
| Salinity Trend                                   |
| +----------------------------------------------+ |
| | Line chart / empty state                      | |
| +----------------------------------------------+ |
| Turbidity Trend                                  |
| +----------------------------------------------+ |
| | Line chart / empty state                      | |
| +----------------------------------------------+ |
| Water Level Trend                                |
| +----------------------------------------------+ |
| | Line chart / empty state                      | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| HISTORY LIST                                     |
| +----------------------------------------------+ |
| | Timestamp | sensor | value | status          | |
| | ...                                          | |
| +----------------------------------------------+ |
| [No records found for selected range.]           |
+--------------------------------------------------+
| FIREBASE VERIFICATION                            |
| +----------------------------------------------+ |
| | Latest timestamp: date/time or --             | |
| | Temperature: value C or --                    | |
| | TDS: value ppt or --                          | |
| | Turbidity: value NTU or --                    | |
| | Water Level: value ft or --                   | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

## Filtering and rendering flow

```text
Open History
    |
    +-> Default date range: previous 7 days through now
    +-> Default sensor filter: All Sensors
    `-> Listen to /ShrimpHub/history ordered by timestamp

History records arrive
    |
    +-> Read grouped records: timestamp, temperature, tds, turbidity, waterLevel
    +-> Also accept legacy rows: timestamp, param_type, rec_val, status
    +-> Sort list newest first; sort charts oldest first
    +-> Update Firebase Verification from newest usable record
    `-> Apply selected date range and sensor filter

Filter selection
    |
    +-> All Sensors: show all four chart groups and list entries
    `-> One sensor: show its chart and matching list entries only

No matching valid records
    +-> Show “No records found for selected range.”
    `-> Each visible chart shows “No sensor data available.”
```

## Data inputs

| Firebase path | Use on screen |
|---|---|
| `/ShrimpHub/history` | Primary source for the list, trends, and verification panel |
| `history/{id}/timestamp` | Orders and filters records by date/time |
| `history/{id}/temperature` | Temperature trend and list item |
| `history/{id}/tds` | Salinity/TDS trend and list item |
| `history/{id}/turbidity` | Turbidity trend and list item |
| `history/{id}/waterLevel` | Water-level trend and list item |
| Legacy `param_type`, `rec_val`, `status` | Backward-compatible history-row display |
