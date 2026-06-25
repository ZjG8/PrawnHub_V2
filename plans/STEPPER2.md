4) 📱 MARKDOWN PROMPT FOR UI + FIREBASE + ARDUINO (CODEx READY)

Save this as: prawnhub_feeder_ui_prompt.md

# PrawnHub Feeder System Integration Prompt

## Objective
Implement a real-time manual + automatic feeding system for PrawnHub using:

- ESP32 (Arduino firmware)
- Firebase Realtime Database
- Android Studio (Dashboard UI only, NO separate feeding page)
- 28BYJ-48 Stepper Motor (ULN2003 driver)

---

## Firebase Structure (Feeder System)


ShrimpHub/
feeder/
feedNow: boolean
feeding: boolean
feedCount: number
lastFeedTime: timestamp
feedAmount: number
autoFeedEnabled: boolean


---

## UI REQUIREMENTS (Dashboard ONLY)

### Add inside DashboardActivity:

### FEED CONTROL CARD


+--------------------------------------+
| FEED CONTROL |
+--------------------------------------+
| Feed Status: IDLE / FEEDING |
| Feed Count: 12 |
| Last Feed: 12:30 PM |
+--------------------------------------+
| Feed Amount Selector |
| [256] [512] [1024] |
+--------------------------------------+
| [ FEED NOW BUTTON ] |
| [ STOP FEED BUTTON ] |
+--------------------------------------+
| Auto Feed: ON/OFF toggle |
+--------------------------------------+


---

## BUTTON LOGIC

### FEED NOW BUTTON
On click:

Firebase.set("ShrimpHub/feeder/feedNow", true)
Firebase.set("ShrimpHub/feeder/feeding", true)


---

### STOP FEED BUTTON
On click:

Firebase.set("ShrimpHub/feeder/feedNow", false)
Firebase.set("ShrimpHub/feeder/feeding", false)


---

### FEED AMOUNT SELECTOR
Updates:

Firebase.set("ShrimpHub/feeder/feedAmount", 256 or 512 or 1024)


---

## REAL-TIME LISTENER (ANDROID)

Listen to:


ShrimpHub/feeder/feeding
ShrimpHub/feeder/feedCount
ShrimpHub/feeder/lastFeedTime


Update UI instantly.

---

## ESP32 BEHAVIOR (CRITICAL)

ESP32 must:

### Manual Feed:
- Trigger when feedNow == true
- Run stepper motor using feedAmount
- Set feeding = true while running
- After completion:
  - feedNow = false
  - feeding = false
  - increment feedCount
  - save lastFeedTime

### Auto Feed:
- Controlled ONLY by autoFeedEnabled
- Uses RTC time schedule (future upgrade)
- Must NOT interfere with manual feed

---

## SAFETY RULES

- Do NOT allow double feeding
- Ignore feedNow if feeding == true
- Motor must fully stop before next cycle
- Always turn OFF coil pins after movement

---

## EXPECTED RESULT

- Farmer clicks "Feed Now"
- ESP32 rotates stepper motor
- Firebase updates real-time status
- App shows FEEDING → DONE
- Feed count increases automatically