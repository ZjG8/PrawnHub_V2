Update the existing PrawnHub Farmer Dashboard UI and its Firebase control logic.

Use the provided Arduino code as the source of truth for the pump control paths and behavior.

### New Pump 4

Add **Pump 4 / Drain Pump** to the existing **SYSTEM CONTROLS** section.

Firebase path:

`/ShrimpHub/control/pump4`

Arduino GPIO:

`GPIO 26`

Relay:

`Relay 4`

Purpose:

`Drain Pump`

The relay is **active LOW**, but the Android UI should only show the normal user-friendly ON/OFF state.

### Updated Farmer Dashboard

Keep the existing dashboard design and do not remove the existing controls.

```text
+----------------------------------------------------------+
| PrawnHub                                           [Logo] |
+----------------------------------------------------------+
| POND STATUS                                              |
| Healthy | Monitor conditions | Action needed             |
+----------------------------------------------------------+
| +--------------------------+ +--------------------------+ |
| | Temperature              | | TDS / Salinity           | |
| | --.- °C                  | | --.- ppt                 | |
| | Stable / Warning         | | Normal / Warning         | |
| +--------------------------+ +--------------------------+ |
| +--------------------------+ +--------------------------+ |
| | Turbidity                | | Water Level              | |
| | --.- NTU                 | | --.- %                   | |
| | Normal / Critical        | | Normal / Warning         | |
| +--------------------------+ +--------------------------+ |
+----------------------------------------------------------+
| SYSTEM CONTROLS                                          |
|                                                          |
| Filter                                      [ ON / OFF ] |
| Salt Water                                  [ ON / OFF ] |
| Fresh Water                                 [ ON / OFF ] |
| Drain Pump                                  [ ON / OFF ] |
+----------------------------------------------------------+
| FEED CONTROL                                             |
| Status: Ready / Feeding                                  |
| Last feed: --                                            |
| Feed amount: -- g                                        |
| Feed count: --                                           |
|                                                          |
|                    [          FEED NOW          ]        |
+----------------------------------------------------------+
| [ Dashboard ] [ History ] [ Growth ] [ Settings ]        |
+----------------------------------------------------------+
```

### Firebase mapping

Keep the existing mappings:

* Filter → `/ShrimpHub/control/pump1`
* Salt Water → `/ShrimpHub/control/pump2`
* Fresh Water → `/ShrimpHub/control/pump3`

Add:

* Drain Pump → `/ShrimpHub/control/pump4`

The Drain Pump switch must read its current Firebase value and write changes back to the same path.

### Important behavior

1. Do not change the existing Pump 1–3 functionality.
2. Add Pump 4 without breaking the existing controls.
3. Pump 4 must persist its ON/OFF state through Firebase.
4. The UI must correctly reflect the current Firebase state when the dashboard opens.
5. Prevent accidental duplicate writes or conflicting states.
6. Follow the existing project's architecture, naming conventions, styling, dark/light theme handling, and Firebase implementation.
7. Make the new Drain Pump responsive and visually consistent with the other system controls.
8. Make sure the Drain Pump also works correctly in **dark mode**.
9. Do not create a separate screen for Pump 4; keep it inside **SYSTEM CONTROLS**.
10. Do not change the existing feeder behavior.
11. Do not modify the Arduino code unless required for compatibility; the Arduino already supports:

```text
Relay 4 / Pump 4 / Drain Pump
GPIO 26
Firebase: /ShrimpHub/control/pump4
Active LOW
```

### Arduino/Firebase reference

The relevant Arduino logic is:

```cpp
#define PUMP4_RELAY 26

bool lastPump4State = false;

Firebase.RTDB.getBool(
    &fbdo,
    "/ShrimpHub/control/pump4"
);

digitalWrite(
    PUMP4_RELAY,
    state ? LOW : HIGH
);
```

Implement the Android side so that it matches this behavior exactly.

### Final requirement

After implementation, verify that:

* Pump 1 works.
* Pump 2 works.
* Pump 3 works.
* Pump 4 / Drain Pump works.
* Each switch controls only its corresponding Firebase path.
* Firebase state survives leaving and reopening the dashboard.
* Dark mode correctly styles all four controls.
* Existing Dashboard, History, Growth, Settings, and Feed Control functionality remains unchanged.
