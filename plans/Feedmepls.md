You are working on an ESP32 + Firebase + Android Studio IoT system called "PrawnHub".

You must update BOTH:
1. ESP32 Arduino firmware
2. Android dashboard UI logic (text-based architecture only)

IMPORTANT RULES:
- Do NOT create a separate Feeding page/activity.
- Feeding must be inside the existing Dashboard only.
- Remove all "V2" naming.
- Remove ESP32/Firebase status indicators from UI header.
- Keep system stable and do not break existing sensors, pumps, OTA, or Firebase logging.
- Maintain backward compatibility with existing Firebase structure.

====================================================
PART 1: UI CHANGE (DASHBOARD ONLY)
====================================================

Modify Farmer Dashboard:

REMOVE this section completely:
- "Automatic Feeder block (every 6 hours text explanation)"
- Any automatic feeder explanation inside dashboard

REMOVE header elements:
- "[Role: Farmer/Admin]"
- "[Firebase Waiting/Online]"
- "[ESP32 Waiting/Online]"
- Remove "V2" from title

UPDATE TITLE:
"PrawnHub"

ADD ONLY THIS FEED CONTROL SECTION:

--------------------------------------------------
| Feeding Control                                 |
|                                                |
| Feed Now Button                                |
| [ FEED NOW ]                                   |
|                                                |
| Status Indicator                               |
| Feeding: ON / OFF (from Firebase)              |
|                                                |
| Toggle Button                                  |
| [ ENABLE FEEDER ] / [ DISABLE FEEDER ]         |
|                                                |
| Feed Count: 0                                  |
| Last Feed Time: --                             |
--------------------------------------------------

RULES:
- Feed Now triggers manual feeding request
- Toggle enables/disables auto feeding system
- No separate feeding page exists
- Everything stays inside DashboardActivity

====================================================
PART 2: FIREBASE STRUCTURE UPDATE
====================================================

Add/Use this structure:

/ShrimpHub/feeder
    feedNow (bool)
    feeding (bool)
    feedCount (int)
    lastFeedTime (timestamp)
    feedAmount (int)
    autoFeedEnabled (bool)

/ShrimpHub/feedLogs/{pushId}
    timestamp
    date
    feedAmount
    type ("manual" or "scheduled")

====================================================
PART 3: ESP32 ARDUINO UPDATE
====================================================

You must integrate a NON-BLOCKING feeder system into this firmware:

- 28BYJ-48 stepper motor
- ULN2003 driver
- Pins:
    IN1 = 32
    IN2 = 33
    IN3 = 18
    IN4 = 19

Use proper step sequence (8-step half drive):
{1,0,0,0}
{1,1,0,0}
{0,1,0,0}
{0,1,1,0}
{0,0,1,0}
{0,0,1,1}
{0,0,0,1}
{1,0,0,1}

====================================================
FEEDING LOGIC REQUIRED:
====================================================

1. MANUAL FEEDING
- If Firebase "/ShrimpHub/feeder/feedNow == true":
    - Start motor rotation using feedAmount steps
    - Set feeder/feeding = true
    - After completion:
        - feeder/feeding = false
        - feedNow = false
        - increment feedCount
        - update lastFeedTime
        - push log into /ShrimpHub/feedLogs

2. AUTO FEEDING
- If feeder/autoFeedEnabled == true:
    - Use RTC DS3231 time
    - Check schedule fields:
        feedHour1/feedMinute1
        feedHour2/feedMinute2
        feedHour3/feedMinute3
    - Prevent duplicate feeding within same minute
    - Run same motor logic as manual feed
    - Log as "scheduled"

3. SAFETY RULES
- Do not allow feeding while feeder is already running
- Ignore repeated feedNow triggers
- Keep sensors running while feeding
- Do not block main loop (no long blocking delays affecting Firebase)

4. MOTOR CONTROL
- Create function:
    stepMotor(int steps, int speedDelay)
- Must use stepTable sequence above
- Turn off coils after feeding

====================================================
PART 4: EXISTING SYSTEM MUST NOT BREAK
====================================================

DO NOT modify:
- Temperature sensor logic
- TDS sensor logic
- Turbidity sensor logic
- Ultrasonic water level logic
- Pump relay system
- Firebase history logging
- OTA update system
- WiFi reconnect system

ONLY ADD feeder subsystem cleanly.

====================================================
OUTPUT REQUIRED:
====================================================

Return:

1. FULL UPDATED ESP32 ARDUINO CODE
2. ANDROID DASHBOARD UI TEXT STRUCTURE (UPDATED CLEAN VERSION)
3. FIREBASE FIELD MAPPING SUMMARY
4. FEEDING FLOW LOGIC EXPLANATION (short, bullet style)

Make sure the final solution is production-ready and stable.