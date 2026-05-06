# PrawnHub V2 — Revised Admin Dashboard and Settings Prompt

## Project

PrawnHub V2

Android package:
`com.example.prawnhub_v2`

Tech stack:
- Android Studio
- Java
- Firebase Authentication
- Firebase Realtime Database
- ESP32
- Material Design

UI Theme:
- modern aquatic UI
- teal and white palette
- soft shadows
- rounded cards
- floating UI
- maximize screen spacing
- user-friendly layout
- responsive design
- clean modern IoT dashboard appearance

---

# IMPORTANT UI REQUIREMENTS

Apply these globally:

- maximize usage of available screen space
- reduce empty areas
- use modern card layouts
- improve spacing consistency
- improve alignment consistency
- use large readable labels
- improve dashboard hierarchy
- avoid cramped UI
- avoid excessive text
- use icons where appropriate
- maintain a clean monitoring dashboard style
- improve visual balance
- use floating rounded cards
- use soft shadows and subtle gradients

Back Button Requirements:
- use icon-only back button
- do NOT use text like "Back"
- use Material arrow icon
- every page with navigation should support returning to previous page
- apply consistent back button design to all admin pages

---

# REVISED ADMIN DASHBOARD

## Remove These Sections Completely

REMOVE:
- Firebase sync status text
- ESP32 connection status text

Reason:
- wastes dashboard space
- creates visual clutter
- low UI value on main dashboard

Instead:

Add a modern compact status section.

Example concept:
- small colored status indicators
- compact horizontal status cards
- minimal labels
- clean IoT appearance

Suggested Status Layout:

```text
[ ● Online ]   [ ● Synced ]   [ ● Sensors OK ]
```

Use:
- green for healthy
- amber for warning
- red for offline/error

Place it below the top bar.

---

# REVISED ADMIN DASHBOARD LAYOUT

Top Bar:
- admin profile image/avatar on left
- app logo on right
- larger spacing
- modern floating top section

Main Dashboard Cards:

1. Manage User Accounts
2. Generate Farm Reports
3. System Settings
4. Logout

Optional useful cards:
- Recent Alerts Summary
- Latest Sensor Warnings
- Quick Report Export

Dashboard Style:
- larger cards
- modern icon cards
- maximize screen width
- modern admin panel appearance
- responsive card grid
- floating sections
- minimal clutter

---

# REMOVE PAGE

REMOVE ENTIRE PAGE:

```text
System Diagnostics
```

Delete:
- DiagnosticsActivity
- diagnostics navigation
- diagnostics dashboard cards
- diagnostics references

Reason:
- simplify admin workflow
- reduce unnecessary complexity
- keep admin interface cleaner

---

# REVISED FARM REPORTS PAGE

Activity:
`ReportsActivity`

Replace:

```text
Today / Week / Month / Custom Range
```

WITH:

```text
Start Date
End Date
```

Requirements:
- date picker UI
- admin selects exact report range manually
- modern calendar picker design
- cleaner reporting workflow

Example Layout:

```text
Start Date
[ 05 / 01 / 2026 ]

End Date
[ 05 / 07 / 2026 ]
```

Report Data:
- temperature
- salinity
- turbidity
- oxygen
- water level
- pH
- alerts
- growth analytics

Buttons:
- Export PDF
- Export Excel

Requirements:
- modern export cards
- printable report preview
- Firebase integration
- downloadable report files
- clean export section
- maximize content width

Suggested UI:
- large export buttons
- icon-based export cards
- soft shadows
- responsive report layout

---

# REMOVE PAGE

REMOVE ENTIRE PAGE:

```text
Sensor Threshold Settings
```

Reason:
- duplicate functionality
- threshold settings should exist only inside System Settings

Delete:
- separate threshold settings navigation
- duplicate threshold activities/cards

Threshold management must now exist ONLY inside:

```text
SettingsActivity
```

---

# REVISED SYSTEM SETTINGS PAGE

Activity:
`SettingsActivity`

Firebase Source:
`/settings`

Top Navigation:
- icon-only back button on top-left
- app logo on top-right
- modern floating header

IMPORTANT:
DO NOT show:
- Dashboard navigation bar
- History navigation bar
- Growth navigation bar
- Settings bottom navigation

Reason:
- this is an admin settings page
- separate from farmer dashboard flow

---

# REMOVE THESE SETTINGS

REMOVE:
- pond size
- shrimp type

Reason:
- system only supports prawns
- unnecessary UI clutter

---

# POND SETTINGS SECTION

Create a cleaner editable threshold section.

Format Requirements:

Example:

```text
Max Temperature
[ Max Temperature               ]

Min Temperature
[ Min Temperature               ]
```

Apply same structure for:
- max temperature
- min temperature
- max salinity
- min salinity
- max turbidity
- min oxygen
- max pH
- min pH
- overflow limit

Requirements:
- large rounded input boxes
- modern spacing
- clear labels
- maximize width
- mobile-friendly form design
- numeric input support

---

# FEEDING AUTOMATION SECTION

Rename fields.

REMOVE:

```text
First Feed Time
```

REPLACE WITH:

```text
Feed Time
```

New Layout:

```text
Feed Time
[ Feed Time                     ]

Feeding Interval
[ Feeding Interval              ]
```

Requirements:
- large rounded input fields
- time picker support
- interval selection support
- cleaner alignment
- responsive spacing

Features:
- automatic feeding schedule
- repeat daily
- Firebase synchronization
- ESP32 reads feeding schedule
- midnight auto-feed mode
- feeding interval default every 4 hours

---

# NOTIFICATION SETTINGS

Features:
- sound alerts
- vibrate alerts
- push notifications

UI Requirements:
- modern switches/toggles
- large touch-friendly controls
- grouped notification card section

---

# SYSTEM SETTINGS ADDITIONAL REQUIREMENTS

Add:
- Save Settings button
- Logout button

Button Style:
- large rounded buttons
- floating appearance
- full-width modern buttons
- aquatic teal style

---

# GLOBAL DESIGN IMPROVEMENTS

Apply to all admin pages:

- maximize UI spacing
- modern admin panel appearance
- floating cards
- rounded corners
- cleaner hierarchy
- responsive layouts
- larger touch targets
- modern icons
- minimal clutter
- visually balanced sections
- smoother spacing between cards
- cleaner forms
- more premium UI appearance

Avoid:
- tiny cards
- wasted empty areas
- excessive text blocks
- cramped forms
- outdated Android layouts

---

# LOGO USAGE

Use logo from:

```text
C:\Users\sijey\AndroidStudioProjects\PrawnHub_V2\plans\logo
```

Apply logo to:
- admin dashboard
- system settings
- reports page
- login page
- welcome page

---

# ADMIN DASHBOARD TEXT-BASED DIAGRAM

This diagram shows the requested admin dashboard structure and the target navigation flow.

```text
Admin Login
    |
    | Firebase Authentication success
    | Session role = "admin"
    v
+--------------------------------------------------+
| ADMIN DASHBOARD                                  |
|                                                  |
| Header                                           |
| - Admin avatar/profile                           |
| - PrawnHub logo                                  |
|                                                  |
| Compact Status Row                               |
| [Online]  [Synced]  [Sensors OK]                 |
|                                                  |
| Main Admin Actions                               |
| [1] Manage User Accounts                         |
| [2] Farm Reports                                 |
| [3] System Settings                              |
| [4] Logout                                       |
+--------------------------------------------------+
    |
    +--> [1] Manage User Accounts
    |        |
    |        v
    |    +------------------------------------------+
    |    | MANAGE USER ACCOUNTS                    |
    |    |                                          |
    |    | Firebase path: /users                    |
    |    |                                          |
    |    | Admin can:                               |
    |    | - Search users                           |
    |    | - View user email                        |
    |    | - View user role                         |
    |    | - View user status                       |
    |    | - Add user record                        |
    |    | - Assign role: admin or farmer           |
    |    | - Toggle status: active or disabled      |
    |    | - Delete user record                     |
    |    | - Return to Admin Dashboard              |
    |    +------------------------------------------+
    |
    +--> [2] Farm Reports
    |        |
    |        v
    |    +------------------------------------------+
    |    | FARM REPORTS                             |
    |    |                                          |
    |    | Report range:                            |
    |    | - Start Date                             |
    |    | - End Date                               |
    |    |                                          |
    |    | Report data:                             |
    |    | - Temperature                            |
    |    | - Salinity                               |
    |    | - Turbidity                              |
    |    | - Oxygen                                 |
    |    | - Water level                            |
    |    | - pH                                     |
    |    | - Alerts                                 |
    |    | - Growth analytics                       |
    |    |                                          |
    |    | Export options:                          |
    |    | - Export PDF                             |
    |    | - Export Excel                           |
    |    | - Return to Admin Dashboard              |
    |    +------------------------------------------+
    |
    +--> [3] System Settings
    |        |
    |        v
    |    +------------------------------------------+
    |    | SYSTEM SETTINGS                          |
    |    |                                          |
    |    | Firebase paths:                          |
    |    | - /settings                              |
    |    | - /thresholds                            |
    |    |                                          |
    |    | Sensor threshold settings:                |
    |    | - Max temperature                        |
    |    | - Min temperature                        |
    |    | - Max salinity                           |
    |    | - Min salinity                           |
    |    | - Max turbidity                          |
    |    | - Min oxygen                             |
    |    | - Max pH                                  |
    |    | - Min pH                                  |
    |    | - Overflow limit                         |
    |    |                                          |
    |    | Feeding automation:                      |
    |    | - Feed time                              |
    |    | - Feeding interval                       |
    |    | - Midnight auto-feed mode                |
    |    |                                          |
    |    | Notifications:                           |
    |    | - Sound alerts                           |
    |    | - Vibrate alerts                         |
    |    | - Push notifications                     |
    |    |                                          |
    |    | Actions:                                 |
    |    | - Save Settings                          |
    |    | - Logout                                 |
    |    | - Return to Admin Dashboard              |
    |    +------------------------------------------+
    |
    +--> [4] Logout
             |
             v
         Clear SessionStore role
         FirebaseAuth.signOut()
         Firebase /auth/session = "logged_out"
         Return to Role Selection
```

## Removed Admin Pages

```text
System Diagnostics
    |
    +-- Remove DiagnosticsActivity
    +-- Remove diagnostics dashboard card
    +-- Remove diagnostics navigation references

Sensor Threshold Settings
    |
    +-- Remove separate dashboard card
    +-- Keep threshold fields inside System Settings only
```

## Target Activity Mapping

| Admin dashboard button | Target activity |
|---|---|
| Manage User Accounts | `ManageUsersActivity` |
| Farm Reports | `ReportsActivity` |
| System Settings | `SettingsActivity` |
| Logout | `RoleSelectionActivity` |

## Admin Access Rule

```text
Open admin page
    |
    v
Check SessionStore role
    |
    +-- role == "admin" --> Allow access
    |
    +-- role != "admin" --> Close page / return to login flow
```
