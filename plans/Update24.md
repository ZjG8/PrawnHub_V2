# PRAWNHUB UI UPDATE REQUIREMENTS

## GLOBAL

* Implement dark mode (night mode) across the entire application.
* Use dark backgrounds, aqua/blue accents, white text, elevated cards.
* Keep all existing Firebase functionality intact.
* Do not remove existing role-based authentication.

---

# FARMER DASHBOARD

## Header Changes

REMOVE:

* "PrawnHub V2"
* Role indicator
* Firebase Waiting/Online
* ESP32 Waiting/Online

REPLACE WITH:

PrawnHub

---

## System Controls

Rename controls:

* Pump 1 → Filter
* Pump 2 → Salt Water
* Pump 3 → Fresh Water

Display:

[Switch] Filter
[Switch] Salt Water
[Switch] Fresh Water

Firebase paths:

/ShrimpHub/control/pump1
/ShrimpHub/control/pump2
/ShrimpHub/control/pump3

---

## Feed Control

REMOVE:

* Feed Amount selector (256 / 512 / 1024)
* Auto Feed controls from dashboard

ADD:

Feed Control Card

Status:

* Ready to Feed
* Feeding...
* Last Feed Time

Buttons:

[Feed Now]
[Stop Feeding]

Firebase:

/ShrimpHub/feeder/feedNow
/ShrimpHub/feeder/feeding

Feed Now:

* set feedNow = true

Stop Feeding:

* set feedNow = false

Auto Feed remains ONLY in Settings.

---

# HISTORY SCREEN

REMOVE:

* "Sensor Logs" title
* Date Range section
* Start Date field
* End Date field

Make UI simple and modern.

---

## Graph Analytics

Replace Live Sensor Log Stream list view with charts.

Add line charts:

* Temperature Trend
* Salinity Trend
* Turbidity Trend
* Water Level Trend

Use MPAndroidChart.

Read data from:

/ShrimpHub/history

Show latest records automatically.

Graph updates in realtime.

---

## History List

Below charts display:

Timestamp
Sensor
Value
Status

Newest first.

Fix Firebase loading issues.

Verify:

/ShrimpHub/history

is properly read and displayed.

---

# SETTINGS SCREEN

## Farmer Permissions

Farmer CAN edit:

### Feeding Automation

Feed Time
Feeding Interval
Auto Feed

### Notifications

Sound Alert
Vibrate
Push Notifications

---

Farmer CANNOT edit:

### Pond Settings

Temperature
Salinity
Turbidity
Overflow Limit

These remain read-only.

---

## Feeding Automation

Rename:

Auto-feed during sleep mode

TO:

Auto Feed

---

## Turbidity

Replace single slider with RangeSlider.

Min Turbidity
Max Turbidity

Store values:

/settings/min_turb
/settings/max_turb

---

## Overflow

Change unit:

cm → ft

Firebase:

/settings/overflow_limit

Display in feet.

---

# ADMIN DASHBOARD

Header:

Current:

A Admin Logo

Replace with:

PrawnHub Logo

---

REMOVE:

ADMIN DASHBOARD

ONLINE | SYNCHRONIZED

---

Keep:

Quick Actions
Live Sensor Summary
Feeding Automation
Logout

---

# ADMIN PAGES

Replace every back icon:

[<]

with

Back

Text button only.

Applies to:

* Manage Users
* Reports
* Settings
* About
* Any admin-only page

---

# FIREBASE STRUCTURE

Ensure UI supports:

/ShrimpHub/control/pump1
/ShrimpHub/control/pump2
/ShrimpHub/control/pump3

/ShrimpHub/feeder/feedNow
/ShrimpHub/feeder/feeding

/ShrimpHub/history

/settings
/thresholds

No breaking changes to existing database schema.

---

# FINAL GOAL

Create a cleaner, farmer-friendly, mobile-first interface with:

* Dark mode
* Pump 3 support
* Simple feeding controls
* Real-time charts
* Better usability
* Role-based permissions
* Full Firebase integration
* Consistent PrawnHub branding
