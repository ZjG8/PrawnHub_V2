# PrawnHub V2 — UI Pages Context

## Project Overview

PrawnHub V2 is an Android-based smart aquaculture monitoring application focused on indoor prawn/shrimp farming.

Main technologies:

* Android Studio
* Firebase Authentication
* Firebase Realtime Database
* ESP32
* IoT sensors

Application package:

* `com.example.prawnhub_v2`

Design goal:

* modern
* minimal
* soft and user-friendly
* smart aquaculture aesthetic
* clean IoT dashboard appearance
* visually calming and readable

---

# Recommended Color Palette

| Purpose        | Hex     |
| -------------- | ------- |
| Primary        | #3F8C85 |
| Primary Dark   | #2C6E68 |
| Accent Coral   | #F28C6F |
| Warm Sand      | #D9B26F |
| Background     | #F5F8F7 |
| Surface Card   | #FCFFFE |
| Text Primary   | #22302D |
| Text Secondary | #70807A |
| Success        | #6BBF7A |
| Warning        | #E4B04A |
| Danger         | #D96B6B |

---

# App Flow

```text
Welcome Page
      ↓
Role Selection Page
      ↓
Login Page
      ↓
Dashboard
 ├── History
 ├── Growth
 ├── Settings
 └── About
```

---

# Page Wireframes

## 1. Welcome Page

```text
┌──────────────────────────────────────┐
│                                      │
│                                      │
│               ( LOGO )               │
│                                      │
│              PrawnHub                │
│                                      │
│      Smart Aquaculture Monitoring    │
│                                      │
│         Monitor • Analyze • Grow     │
│                                      │
│                                      │
│                                      │
│        [ GET STARTED BUTTON ]        │
│                                      │
│                                      │
└──────────────────────────────────────┘
```

---

## 2. Role Selection Page

```text
┌──────────────────────────────────────┐
│                                      │
│            Choose Role               │
│                                      │
│   Select how you want to continue    │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │                                  │ │
│ │            👨‍💼                    │ │
│ │                                  │ │
│ │              ADMIN               │ │
│ │                                  │ │
│ │ Manage system and monitoring      │ │
│ │                                  │ │
│ └──────────────────────────────────┘ │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │                                  │ │
│ │             🧑‍🌾                   │ │
│ │                                  │ │
│ │             FARMER               │ │
│ │                                  │ │
│ │ View pond and shrimp conditions   │ │
│ │                                  │ │
│ └──────────────────────────────────┘ │
│                                      │
└──────────────────────────────────────┘
```

---

## 3. Login Page

```text
┌──────────────────────────────────────┐
│ ← Back                               │
│                                      │
│              ( LOGO )                │
│                                      │
│              Login                   │
│                                      │
│      Continue as FARMER              │
│                                      │
│   Email                              │
│   ┌──────────────────────────────┐   │
│   │ Enter your email             │   │
│   └──────────────────────────────┘   │
│                                      │
│   Password                           │
│   ┌──────────────────────────────┐   │
│   │ *************                │👁 │
│   └──────────────────────────────┘   │
│                                      │
│         Forgot Password?             │
│                                      │
│         [ LOGIN BUTTON ]             │
│                                      │
│     Don't have account? Sign Up      │
│                                      │
└──────────────────────────────────────┘
```

---

## 4. Dashboard Page

```text
┌──────────────────────────────────────┐
│ ☰            Dashboard         🔔    │
│                                      │
│   Pond Status: ● Healthy             │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │ Water Temperature                │ │
│ │                                  │ │
│ │            28°C                  │ │
│ │                                  │ │
│ │            Stable                │ │
│ └──────────────────────────────────┘ │
│                                      │
│ ┌──────────────┐ ┌──────────────┐    │
│ │ pH Level     │ │ Oxygen       │    │
│ │ 7.3          │ │ 6.2 mg/L     │    │
│ └──────────────┘ └──────────────┘    │
│                                      │
│ ┌──────────────┐ ┌──────────────┐    │
│ │ Turbidity    │ │ Water Level  │    │
│ │ Clear        │ │ Normal       │    │
│ └──────────────┘ └──────────────┘    │
│                                      │
│ Growth Prediction                    │
│ ┌──────────────────────────────────┐ │
│ │ Healthy Growth Expected          │ │
│ │ +12% This Week                   │ │
│ └──────────────────────────────────┘ │
│                                      │
│ Recent Alerts                        │
│ ┌──────────────────────────────────┐ │
│ │ ⚠ Slight pH fluctuation detected │ │
│ └──────────────────────────────────┘ │
│                                      │
│ Dashboard | History | Growth | ⚙    │
└──────────────────────────────────────┘
```

---

## 5. History Page

```text
┌──────────────────────────────────────┐
│ ← History                            │
│                                      │
│ [ Today ] [ Week ] [ Month ]         │
│                                      │
│ TODAY                                │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │ 10:45 AM                         │ │
│ │ Temperature: 28°C                │ │
│ │ Status: Normal                   │ │
│ └──────────────────────────────────┘ │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │ 9:30 AM                          │ │
│ │ pH changed to 7.2                │ │
│ │ Status: Stable                   │ │
│ └──────────────────────────────────┘ │
│                                      │
│ YESTERDAY                            │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │ Oxygen Level Slightly Low        │ │
│ │ Alert Sent                       │ │
│ └──────────────────────────────────┘ │
│                                      │
│ Dashboard | History | Growth | ⚙    │
└──────────────────────────────────────┘
```

---

## 6. Growth Page

```text
┌──────────────────────────────────────┐
│ ← Growth Analytics                   │
│                                      │
│ Estimated Growth                     │
│ ┌──────────────────────────────────┐ │
│ │                                  │ │
│ │          78% Healthy             │ │
│ │                                  │ │
│ │      ( Circular Progress )       │ │
│ │                                  │ │
│ └──────────────────────────────────┘ │
│                                      │
│ Weekly Growth Trend                  │
│ ┌──────────────────────────────────┐ │
│ │                                  │ │
│ │           LINE GRAPH             │ │
│ │                                  │ │
│ └──────────────────────────────────┘ │
│                                      │
│ Recommendations                      │
│ ┌──────────────────────────────────┐ │
│ │ Maintain stable oxygen levels    │ │
│ └──────────────────────────────────┘ │
│                                      │
│ Dashboard | History | Growth | ⚙    │
└──────────────────────────────────────┘
```

---

## 7. Settings Page

```text
┌──────────────────────────────────────┐
│ ← Settings                           │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │ 👤 Account                       │ │
│ │ user@email.com                   │ │
│ └──────────────────────────────────┘ │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │ 🦐 Pond Settings                 │ │
│ │ Pond Size                        │ │
│ │ Shrimp Type                      │ │
│ │ Water Thresholds                 │ │
│ └──────────────────────────────────┘ │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │ 🔔 Notifications                 │ │
│ │ Alerts ON                        │ │
│ │ Sound ON                         │ │
│ └──────────────────────────────────┘ │
│                                      │
│ ┌──────────────────────────────────┐ │
│ │ ℹ About PrawnHub                 │ │
│ └──────────────────────────────────┘ │
│                                      │
│       [ SAVE SETTINGS ]              │
│                                      │
│          [ LOGOUT ]                  │
│                                      │
│ Dashboard | History | Growth | ⚙    │
└──────────────────────────────────────┘
```

---

## 8. About Page

```text
┌──────────────────────────────────────┐
│ ← About                              │
│                                      │
│              ( LOGO )                │
│                                      │
│             PrawnHub                 │
│                                      │
│             Version 2.0              │
│                                      │
│ Smart Monitoring and Control System  │
│ for Indoor Prawn Farming             │
│                                      │
│ Developed By                         │
│ BSIT Students                        │
│ Polytechnic University               │
│                                      │
│ Technologies Used                    │
│ • ESP32                              │
│ • Firebase                           │
│ • Android Studio                     │
│                                      │
│         [ CONTACT TEAM ]             │
│                                      │
└──────────────────────────────────────┘
```

# PrawnHub V2 — Logo Design Prompt, Design References, and Layout Structure

---

# Logo Design Prompt

Create a modern minimalist mobile app logo for an aquaculture IoT application named “PrawnHub”.

Style requirements:

* clean
* flat design
* simple but memorable
* modern startup/app branding
* professional but friendly
* suitable for Android app icon and splash screen
* minimal visual clutter
* scalable and recognizable at small sizes

Main concept:

* combine a shrimp/prawn silhouette with smart technology elements
* integrate subtle IoT symbolism such as:

  * WiFi signal
  * sensor pulse
  * digital wave
  * circuit line
  * water ripple

Design direction:

* soft rounded shapes
* smooth curves
* aquatic feel
* balanced composition
* not cartoonish
* not overly detailed
* not realistic

Color palette:

* deep teal (#3F8C85)
* ocean teal (#2C6E68)
* soft aqua (#DDEDEA)
* soft coral accent (#F28C6F)
* white highlights

Visual mood:

* calming
* trustworthy
* innovative
* eco-friendly
* smart aquaculture technology

Preferred composition:

* circular or rounded-square app icon
* centered shrimp icon
* subtle wave or WiFi detail near the shrimp tail or head
* optional water ripple background
* smooth gradient allowed but keep it soft and minimal

Typography:

* modern sans-serif
* Poppins or Inter style
* clean spacing
* app name: “PrawnHub”

Avoid:

* mascots
* aggressive shrimp illustrations
* neon colors
* dark cyberpunk look
* heavy shadows
* excessive detail
* generic farming clipart

Generate:

1. app icon version
2. horizontal logo version
3. monochrome version
4. transparent background version

Make the final design feel similar to modern IoT, smart home, or health-monitoring app branding.

---

# Design References

Use design inspiration similar to:

* Samsung Health
* Google Home
* Xiaomi Home
* Smart aquarium apps
* IoT monitoring dashboards
* Weather apps
* Smart farming apps
* Health analytics apps

Visual style goals:

* modern
* calm
* minimal
* soft colors
* rounded UI
* floating cards
* smooth spacing
* clean sensor dashboards
* easy readability
* user-friendly navigation

Avoid:

* dark hacker/cyberpunk themes
* excessive animations
* heavy gradients
* harsh shadows
* cluttered dashboards
* overly realistic aquatic graphics

Suggested fonts:

* Poppins
* Inter
* Nunito

Suggested icon styles:

* Material Symbols Rounded
* Phosphor Icons
* Hugeicons

---

# Layout Structure

## Overall App Flow

```text
Welcome Page
      ↓
Role Selection Page
      ↓
Login Page
      ↓
Dashboard
 ├── History
 ├── Growth
 ├── Settings
 └── About
```

---

# Layout Guidelines

## General Layout Style

* rounded corners
* floating cards
* soft shadows
* large readable text
* minimal clutter
* consistent spacing
* teal aquatic color palette
* white/light surfaces

---

## Spacing System

Use consistent spacing:

* 8dp → small spacing
* 16dp → normal spacing
* 24dp → section spacing
* 32dp → large section spacing

---

## Corner Radius

* Cards → 20dp
* Buttons → 16dp
* Inputs → 14dp
* Bottom navigation → 24dp

---

## Typography Hierarchy

* Page title → 28sp
* Section title → 20sp
* Card value → 24sp
* Body text → 14–16sp
* Labels → 12–14sp

---

## Dashboard Layout Structure

Recommended order:

1. Top navigation bar
2. Pond/Farm status card
3. Main sensor card
4. Grid sensor cards
5. Growth prediction section
6. Alerts section
7. Bottom navigation

---

## Bottom Navigation

Recommended tabs:

* Dashboard
* History
* Growth
* Settings

About page can be accessed through Settings.

Use rounded icons and minimal labels.

---

## Recommended Component Style

### Buttons

* rounded
* large tap area
* soft shadows
* minimal borders

### Cards

* white/light background
* rounded corners
* soft shadow
* icon + title + value layout

### Inputs

* rounded inputs
* subtle borders
* soft gray background
* large readable placeholder text

---

## Color Usage Recommendation

| Element         | Color         |
| --------------- | ------------- |
| Primary Actions | Deep Teal     |
| Alerts          | Soft Coral    |
| Success Status  | Soft Green    |
| Background      | Mist White    |
| Cards           | White Surface |
| Text            | Deep Gray     |

---

## Recommended Visual Mood

The app should feel:

* calm
* modern
* smart
* professional
* clean
* eco-friendly
* technology-focused
* easy to understand
* suitable for both farmers and administrators
