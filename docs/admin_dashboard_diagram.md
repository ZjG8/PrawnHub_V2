# Admin Dashboard Text-Based Diagram

This diagram shows the admin area navigation and the main responsibilities of each admin screen.

```text
Admin Login
    |
    | Firebase Authentication success
    | Session role = "admin"
    v
+--------------------------------------------------+
| ADMIN DASHBOARD                                  |
|                                                  |
| Status: Firebase Sync / ESP32 Connected-Standby  |
|                                                  |
| [1] Manage User Accounts                         |
| [2] System Diagnostics                           |
| [3] Farm Reports                                 |
| [4] Sensor Threshold Settings                    |
| [5] System Settings                              |
| [6] Logout                                       |
+--------------------------------------------------+
    |
    +--> [1] Manage User Accounts
    |        |
    |        v
    |    +------------------------------------------+
    |    | MANAGE USER ACCOUNTS                    |
    |    |                                          |
    |    | Data source: Firebase /users             |
    |    |                                          |
    |    | - Search users                           |
    |    | - View email, role, status, date         |
    |    | - Add user record                        |
    |    | - Assign role: admin or farmer           |
    |    | - Toggle status: active or disabled      |
    |    | - Delete user record                     |
    |    | - Back to Admin Dashboard                |
    |    +------------------------------------------+
    |
    +--> [2] System Diagnostics
    |        |
    |        v
    |    +------------------------------------------+
    |    | SYSTEM DIAGNOSTICS                       |
    |    |                                          |
    |    | Data sources: Firebase /control,         |
    |    |               Firebase /aquarium         |
    |    |                                          |
    |    | - ESP32 online/offline status            |
    |    | - Firebase connection status             |
    |    | - Sensor readings summary                |
    |    | - Database sync status                   |
    |    | - Pump, filter, aerator state            |
    |    | - Back to Admin Dashboard                |
    |    +------------------------------------------+
    |
    +--> [3] Farm Reports
    |        |
    |        v
    |    +------------------------------------------+
    |    | FARM REPORTS                             |
    |    |                                          |
    |    | - Select report range                    |
    |    |     Today / Week / Month / Custom Range  |
    |    | - Select export format                   |
    |    |     PDF / Excel (.xlsx)                  |
    |    | - Export PDF                             |
    |    | - Export Excel                           |
    |    | - Back to Admin Dashboard                |
    |    +------------------------------------------+
    |
    +--> [4] Sensor Threshold Settings
    |        |
    |        v
    |    +------------------------------------------+
    |    | SENSOR THRESHOLD SETTINGS                |
    |    |                                          |
    |    | Screen used: System Settings screen       |
    |    | Data sources: Firebase /settings,        |
    |    |               Firebase /thresholds       |
    |    |                                          |
    |    | Editable threshold fields:                |
    |    | - Min / max temperature                  |
    |    | - Min / max salinity                     |
    |    | - Max turbidity                          |
    |    | - Min oxygen                             |
    |    | - Min / max pH                           |
    |    | - Overflow limit                         |
    |    | - Save settings                          |
    |    +------------------------------------------+
    |
    +--> [5] System Settings
    |        |
    |        v
    |    +------------------------------------------+
    |    | SYSTEM SETTINGS                          |
    |    |                                          |
    |    | Screen used: SettingsActivity             |
    |    | Data source: Firebase /settings           |
    |    |                                          |
    |    | - Account email display                  |
    |    | - Pond size                              |
    |    | - Shrimp type                            |
    |    | - Feeding time                           |
    |    | - Feeding interval hours                 |
    |    | - Alert settings                         |
    |    |     Sound / Vibrate / Push notifications |
    |    | - Feeding automation                     |
    |    | - About PrawnHub                         |
    |    | - Save settings                          |
    |    | - Logout                                 |
    |    +------------------------------------------+
    |
    +--> [6] Logout
             |
             v
         Clear local session role
         FirebaseAuth.signOut()
         Firebase /auth/session = "logged_out"
         Return to Role Selection
```

## Current Activity Mapping

| Admin dashboard button | Android activity opened |
|---|---|
| Manage User Accounts | `ManageUsersActivity` |
| System Diagnostics | `DiagnosticsActivity` |
| Farm Reports | `ReportsActivity` |
| Sensor Threshold Settings | `SettingsActivity` |
| System Settings | `SettingsActivity` |
| Logout | `RoleSelectionActivity` |

## Admin Access Rule

```text
Open admin screen
    |
    v
Check SessionStore role
    |
    +-- role == "admin" --> Allow screen
    |
    +-- role != "admin" --> Close screen
```

