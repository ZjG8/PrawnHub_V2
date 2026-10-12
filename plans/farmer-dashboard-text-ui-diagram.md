# Farmer Dashboard - Text-Based UI Diagram

This diagram reflects the current farmer view of `DashboardActivity` (`activity_dashboard.xml`). Values shown with `--` are populated from Firebase when live data is available.

```text
+----------------------------------------------------------+
| PrawnHub                                           [Logo] |
+----------------------------------------------------------+
| POND STATUS                                              |
| Healthy | Monitor conditions | Action needed              |
+----------------------------------------------------------+
| +--------------------------+ +--------------------------+ |
| | Temperature              | | TDS                      | |
| | --.- C                   | | --.- ppm                 | |
| | Stable / Warning          | | Normal / Warning         | |
| +--------------------------+ +--------------------------+ |
| +--------------------------+ +--------------------------+ |
| | Turbidity                | | Water Level              | |
| | --.- NTU                 | | --.- ft                  | |
| | Normal / Critical         | | Normal / Warning         | |
| +--------------------------+ +--------------------------+ |
+----------------------------------------------------------+
| SYSTEM CONTROLS                                          |
| Filter                                      [ ON / OFF ] |
| Salt Water                                  [ ON / OFF ] |
| Fresh Water                                 [ ON / OFF ] |
+----------------------------------------------------------+
| FEED CONTROL                                             |
| Status: Ready / Feeding                                  |
| Last feed: --                                            |
| Feed amount: -- g                                        |
| Feed count: --                                           |
|                                                          |
|                    [          FEED NOW          ]        |
+----------------------------------------------------------+
| [ Dashboard ]   [ History ]   [ Growth ]   [ Settings ]   |
+----------------------------------------------------------+
```

## Interaction and data map

| UI element | Farmer action / behavior | Source or destination |
| --- | --- | --- |
| Pond status | Read-only overall health indicator | Calculated from sensor values and configured limits |
| Temperature, TDS, turbidity, water level cards | Read-only live readings with condition labels | `/ShrimpHub` and `/settings` |
| Filter switch | Turns filter pump on or off | `/ShrimpHub/control/pump1` |
| Salt Water switch | Turns salt-water pump on or off | `/ShrimpHub/control/pump2` |
| Fresh Water switch | Turns fresh-water pump on or off | `/ShrimpHub/control/pump3` |
| Feed Now | Starts a manual feed request; disabled while feeding | `/ShrimpHub/feeder/feedNow` |
| Feed details | Read-only status, time, amount, and count | `/ShrimpHub/feeder` |
| Bottom navigation | Opens the selected farmer screen | Dashboard, History, Growth, or Settings |

The admin-only tools section is hidden when the signed-in role is `farmer`.
