# PrawnHub UI Full Redesign Prompt

## Goal
Redesign and refactor the PrawnHub Android UI for Farmer and Admin roles.

## Farmer Dashboard
- Remove Oxygen, pH, and Aerator controls from visible UI.
- Keep Pond Status, Firebase alert banner, Water Temperature, Salinity, Turbidity, Water Level, Growth Prediction, Recent Alerts, Pump, Filter, and Feed Now.
- Keep Firebase sensor reads from `/aquarium` and `/ShrimpHub`.
- Keep control writes to `/control/pump_stat`, `/control/filter_stat`, and `/control/motor_trig`.

## Farmer History
- Remove the Important Events section header.
- Replace Today/Week/Month spinner with Start Date + Time and End Date + Time pickers.
- Keep sensor filtering for Temperature, Salinity, Turbidity, and Water Level.
- Keep Firebase `/history` support and filter by timestamp range plus sensor type.

## Farmer Growth
- Show Growth Analytics, Health Score, Weekly Growth Trend, Status Badge, and Recommendations.
- Keep Firebase `/growth`, `/growth_trend`, `/settings`, and sensor health behavior.

## Farmer Settings
- Remove Account section, Oxygen fields, and pH fields.
- Show Farmer role label in farmer mode.
- Keep read-only Pond Settings, Feeding Automation, Notifications, About PrawnHub, and Back to Roles.

## Admin Dashboard
- Show ADMIN DASHBOARD, Firebase Sync status, ESP32 Status, Quick Actions, Activity Feed, and Logout.
- Quick Actions: Manage Users, Farm Reports, System Settings, Diagnostics.
- Keep admin-only access checks and existing navigation.

## Admin Reports
- Remove Oxygen and pH from report preview.
- Keep date range, Temperature, Salinity, Turbidity, Water Level, Alerts, Growth Analytics, Export PDF, and Export Excel.

## Admin System Settings
- Remove Min Oxygen, Max pH, and Min pH fields.
- Keep editable temperature, salinity, turbidity, overflow, feeding, and notification settings for admins.

## Global UI Rules
- Maximize screen space usage.
- Use clean card-based IoT dashboard design.
- Use a soft aquatic green theme.
- Keep rounded cards, clean typography hierarchy, minimal shadows, and Firebase real-time updates.
- Keep Farmer and Admin behavior separated.
