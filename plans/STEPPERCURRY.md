Integrate a 28BYJ-48 Stepper Motor Feeder with ULN2003 Driver into the existing PrawnHub ESP32 + Firebase IoT System.

Current Hardware:

* ESP32
* DS18B20 Temperature Sensor (GPIO25)
* TDS Sensor (GPIO34)
* Turbidity Sensor (GPIO35)
* Ultrasonic Sensor HC-SR04

  * TRIG = GPIO12
  * ECHO = GPIO13
* Relay 1 = GPIO16
* Relay 2 = GPIO17
* Stepper Motor 28BYJ-48 with ULN2003

  * IN1 = GPIO32
  * IN2 = GPIO33
  * IN3 = GPIO18
  * IN4 = GPIO19

Current Firebase Structure:
ShrimpHub/
├── temperature
├── tds
├── turbidity
├── waterLevel
├── history
└── control
├── pump1
└── pump2

Add a complete feeding subsystem using Firebase.

New Firebase Structure:

ShrimpHub/
├── feeder
│   ├── feedNow
│   ├── feeding
│   ├── feedCount
│   ├── lastFeedTime
│   ├── feedAmount
│   └── autoFeedEnabled
│
├── schedule
│   ├── feedHour1
│   ├── feedMinute1
│   ├── feedHour2
│   ├── feedMinute2
│   └── feedHour3
│       feedMinute3

Requirements:

1. Manual Feeding

* Android app can set:
  ShrimpHub/feeder/feedNow = true
* ESP32 detects it.
* Rotate stepper motor.
* After feeding:

  * feedNow = false
  * feeding = false
  * increment feedCount
  * save lastFeedTime

2. Feeding Status

* While motor rotates:
  feeder/feeding = true
* When finished:
  feeder/feeding = false

3. Feed Amount

* Firebase stores:
  feeder/feedAmount
* Value represents number of stepper steps.
* Example:
  256 = small feed
  512 = medium feed
  1024 = large feed

4. Scheduled Feeding

* Use DS3231 RTC.
* Read schedules from Firebase.
* Automatically feed at configured times.
* Prevent duplicate feeding within the same minute.

5. Feeding Logs
   Store every feeding event under:

ShrimpHub/feedLogs/

with:
{
"timestamp": unixTime,
"date": readableDate,
"feedAmount": steps,
"type": "manual" or "scheduled"
}

6. Android Application
   Create a Feeding Page.

Features:

* Feed Now button
* Feeding status indicator
* Feed amount selector
* Feed count display
* Last feeding time display
* Enable/Disable auto feeding
* Feeding history table
* Scheduled feeding configuration

7. Safety Features

* Prevent feeding while feeder is already running.
* Ignore duplicate feed requests.
* Restore feedCount after reboot using Firebase.
* Continue monitoring sensors while feeder is idle.

8. Maintain Existing Features
   Do not modify or break:

* Temperature monitoring
* TDS monitoring
* Turbidity monitoring
* Water level monitoring
* Pump 1 control
* Pump 2 control
* Firebase logging
* OTA updates
* WiFi reconnection system

Generate the complete ESP32 Arduino code and Android Studio Firebase integration code necessary to implement this feeder subsystem.
