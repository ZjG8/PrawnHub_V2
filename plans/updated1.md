# PrawnHub V2 — Updated UI Page Prompts

## Project Path

```text
C:\Users\sijey\AndroidStudioProjects\PrawnHub_V2
```

Logo file location:

```text
C:\Users\sijey\AndroidStudioProjects\PrawnHub_V2\plans\logo
```

Use the logo file from the plans directory across:
- Welcome Page
- Login Page
- Dashboard top-right logo
- About Page

---

# 1. Welcome Page Prompt

Create a modern Android welcome page for PrawnHub V2.

Requirements:
- use soft teal aquatic theme
- centered logo from:
  `C:\Users\sijey\AndroidStudioProjects\PrawnHub_V2\plans\logo`
- modern minimal UI
- rounded button
- calm gradient background
- app title: PrawnHub
- subtitle: Smart Aquaculture Monitoring
- tagline: Monitor • Analyze • Grow
- add a large "Get Started" button
- button navigates to Role Selection Page
- use rounded corners and soft shadows
- modern Android Material Design appearance

---

# 2. Role Selection Page Prompt

Create a role selection page after the welcome page.

Requirements:
- add a back button on the top-left
- back button navigates to Welcome Page
- page title: Choose Role
- subtitle: Select how you want to continue
- create two large rounded cards

Card 1:
- icon for admin
- label: ADMIN
- description: Manage system and monitoring
- teal accent

Card 2:
- icon for farmer
- label: FARMER
- description: View pond and shrimp conditions
- coral accent

Additional requirements:
- soft shadows
- rounded corners
- large touch-friendly UI
- modern smart-farming appearance
- selecting a role navigates to Login Page
- selected role should pass role data to Login Page

---

# 3. Login Page Prompt

Create a modern Android login page for PrawnHub V2.

Requirements:
- add top-left back button
- back button navigates to Role Selection Page
- centered logo from:
  `C:\Users\sijey\AndroidStudioProjects\PrawnHub_V2\plans\logo`
- modern minimal UI
- rounded text fields
- teal/coral accents
- soft shadows

Features:
- email field
- password field
- show/hide password toggle
- login button
- forgot password button
- sign up/create account button

Authentication requirements:
- support ADMIN account
- support FARMER account
- users can create their own account
- use Firebase Authentication
- support password reset through Firebase
- display selected role from previous page

Firebase requirements:
- use Firebase Authentication Email/Password login
- create registration page or dialog
- forgot password should send reset email
- save role type in Firebase Realtime Database

Firebase structure suggestion:

```json
users:
  uid:
    role: "admin"
    email: "sample@email.com"
```

If role is:
- admin → open admin dashboard features
- farmer → open farmer dashboard features

---

# Firebase Authentication Guide Prompt

Create a beginner-friendly step-by-step guide for implementing:

- Firebase Authentication
- user registration
- login system
- forgot password
- admin and farmer roles
- Firebase Realtime Database user role storage

Use:
- Java
- Android Studio
- Firebase Authentication
- Firebase Realtime Database

Requirements:
- explain where to click in Firebase Console
- explain how to enable Email/Password authentication
- explain how to create users
- explain how to detect admin/farmer role
- include Java code examples
- beginner-friendly explanation

---

# 4. Dashboard Page Prompt

Create a modern smart aquaculture dashboard page.

Requirements:
- remove hamburger menu icon
- replace top-left with user profile image/avatar
- top-right should display the app logo from:
  `C:\Users\sijey\AndroidStudioProjects\PrawnHub_V2\plans\logo`
- modern IoT dashboard appearance
- soft teal aquatic color palette
- rounded cards
- floating UI design

Dashboard sections:

1. Pond Status
- display overall pond health
- color indicator:
  - green = healthy
  - yellow = warning
  - red = danger

2. Main Temperature Card
- large temperature display
- real-time Firebase data
- stable/critical status text

3. Sensor Grid Cards
- pH level
- oxygen
- turbidity
- water level
- salinity

4. Growth Prediction
- estimated prawn condition
- percentage health score
- based on Firebase data

5. Recent Important Alerts
- show only important notifications
- examples:
  - oxygen critical
  - water level low
  - salinity abnormal

Requirements:
- data should come from Firebase Realtime Database
- responsive layout
- modern Material Design UI

---

# 5. History Page Prompt

Create a modern history page for PrawnHub V2.

Requirements:
- add back button on top-left
- modern floating card design
- teal aquatic theme
- rounded dropdowns
- history should only display important notifications/events

Replace:

```text
[ Today ] [ Week ] [ Month ]
```

with dropdown filter system.

Main dropdown:
- Today
- Week
- Month

Secondary dropdown based on selected range:
- Temperature
- Salinity
- Turbidity
- Water Level
- Oxygen
- pH

Behavior:
- selecting Today/Week/Month changes the time range
- selecting sensor type filters the history
- data comes from Firebase Realtime Database

History cards should display:
- timestamp
- sensor type
- value
- alert status
- only important events

Examples:
- oxygen below threshold
- abnormal salinity
- low water level
- dangerous turbidity

---

# 6. Growth Analytics Page Prompt

Create a modern growth analytics page for PrawnHub V2.

Requirements:
- teal smart-analytics appearance
- rounded floating cards
- graph support
- Firebase Realtime Database integration

Features:

1. Estimated Growth Health Circle
- replace normal percentage text with circular life/health progress indicator
- indicator color changes:
  - green = healthy
  - yellow = warning
  - red = unhealthy
- automatically calculated from Firebase sensor values

2. Weekly Growth Trend Graph
- display line graph using Firebase data
- graph updates automatically
- show growth trend over time

3. Recommendations Section
- display automated recommendations based on water quality
- examples:
  - maintain oxygen level
  - reduce salinity
  - monitor turbidity

Requirements:
- graph should be smooth and readable
- modern analytics dashboard appearance
- Material Design style

---

# 7. Settings Page Prompt

Create a modern settings page for PrawnHub V2.

Requirements:
- teal aquatic design
- rounded cards
- grouped settings sections
- modern Android settings appearance

Sections:

1. Account
- display email
- profile image

2. Pond Settings
Remove:
- harvest days setting

Reason:
- system should automatically determine harvest readiness based on Firebase data and growth conditions

Keep:
- pond size
- shrimp type
- water thresholds

3. Notifications
Add:
- sound alert toggle
- vibrate toggle
- push notification toggle

4. Feeding Automation
Add feature:
- feeding interval settings
- default every 4 hours
- automatic feeding scheduler

Add nighttime feature:
- auto-feed during sleep mode
- owner can activate automatic midnight feeding schedule
- repeats every day automatically
- integrated with ESP32 feeding system

5. About Button
- opens About Page

Bottom Buttons:
- Save Settings
- Logout

Requirements:
- Firebase integration
- settings persistence
- clean modern UI

---

# 8. About Page Prompt

Create a modern About Page for PrawnHub V2.

Requirements:
- use centered logo from:
  `C:\Users\sijey\AndroidStudioProjects\PrawnHub_V2\plans\logo`
- modern minimal design
- teal aquatic color palette
- rounded cards
- centered layout

Content:

App Name:
- PrawnHub

Version:
- Version 1.0

Description:
- Smart Monitoring and Control System for Indoor Prawn Farming

Developed By:
- Zj Gilbuena
- Mikko Garduna
- Jovin Barretto
- Franz Velasco

School:
- Polytechnic University of the Philippines Lopez, Quezon Branch

Technologies Used:
- ESP32
- Firebase
- Android Studio

Add:
- Contact Team button
- modern typography
- soft shadows
- clean spacing

