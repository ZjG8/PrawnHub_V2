# PrawnHub Dashboard - Text UI Diagram

Screen: `DashboardActivity` / `activity_dashboard.xml`

```text
+--------------------------------------------------+
| PrawnHub                                 [Logo] |
+--------------------------------------------------+
| POND STATUS                                      |
| +----------------------------------------------+ |
| | Healthy / Monitor conditions / Action needed | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| LIVE SENSOR READINGS                             |
| +----------------------+ +---------------------+ |
| | Temperature          | | TDS                 | |
| | NN.N C               | | NN.N ppm            | |
| | Stable / Warning     | | Normal / Warning    | |
| +----------------------+ +---------------------+ |
| +----------------------+ +---------------------+ |
| | Turbidity            | | Water Level         | |
| | NN.N NTU             | | NN.N ft             | |
| | Normal / Critical    | | Normal / Warning    | |
| +----------------------+ +---------------------+ |
+--------------------------------------------------+
| SYSTEM CONTROLS                                  |
| +----------------------------------------------+ |
| | Filter                              [on/off] | |
| | Salt Water                          [on/off] | |
| | Fresh Water                         [on/off] | |
| | Drain Pump                          [on/off] | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| FEED CONTROL                                     |
| +----------------------------------------------+ |
| | Status: Ready / Feeding                       | |
| | Last feed: timestamp / --                     | |
| | Feed amount: N g                              | |
| | Feed count: N                                 | |
| |                 [ FEED NOW ]                  | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| ADMIN TOOLS (admin role only)                    |
| +----------------------------------------------+ |
| | N registered users                            | |
| | Temp range | TDS range | Turbidity maximum   | |
| | [ Manage Users ]                              | |
| | [ Export Report ]                             | |
| +----------------------------------------------+ |
+--------------------------------------------------+
| Dashboard | History | Growth | Settings          |
+--------------------------------------------------+
```

## Live State Rules

```text
/ShrimpHub sensor values arrive
    |
    +-> Temperature outside configured range -> Warning
    +-> TDS outside configured range         -> Warning
    +-> Turbidity above configured maximum   -> Critical
    +-> Water level above overflow limit      -> Warning
    |
    `-> Pond status
        +-> 0 issues: Healthy
        +-> 1-2 issues: Monitor conditions
        `-> 3-4 issues: Action needed

/ShrimpHub/control changes
    `-> Reflect Filter, Salt Water, Fresh Water, and Drain Pump switch state

User changes a control switch
    `-> Save the matching control value to /ShrimpHub/control

User selects FEED NOW
    +-> If not already feeding, set /ShrimpHub/feeder/feedNow = true
    `-> Disable the button while feeder state is Feeding
```

## Data Inputs

| Firebase path | Use on screen |
|---|---|
| `/ShrimpHub/temperature` | Temperature reading and status |
| `/ShrimpHub/tds` | TDS reading and status |
| `/ShrimpHub/turbidity` | Turbidity reading and status |
| `/ShrimpHub/waterLevel` | Water level, displayed in feet |
| `/ShrimpHub/control` | Four manual system-control switch states |
| `/ShrimpHub/feeder` | Feeding state, last feed, amount, and count |
| `/settings` | Sensor safe ranges and overflow threshold |
| `/users` | Registered-user count for administrators |
