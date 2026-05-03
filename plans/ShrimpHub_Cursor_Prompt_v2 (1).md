# PROJECT OVERVIEW

**ShrimpHub** connects IoT sensors in a shrimp tank to a mobile Android app through Firebase. The system monitors water quality 24/7, automatically controls hardware (pumps, feeders, lights), alerts the farmer on their phone, and helps predict the best time to harvest — replacing manual logbooks with a smart digital dashboard.

**Tech stack:**
- **Hardware:** ESP32 + DS18B20 (temp), TDS (salinity), Turbidity, Ultrasonic HC-SR04 (water level), RTC DS3231, ULN2003 Stepper Driver + 28BYJ-48 Motor, 4-channel Relay
- **Firmware:** C++ using Arduino IDE
- **Backend:** Firebase Realtime Database + Firebase Auth + Firebase Cloud Messaging (FCM)
- **Mobile App:** Android (Java) in Android Studio
- **Network:** ESP32 connects via the farmer's phone hotspot — no router needed

---

# STEP-BY-STEP BUILD PLAN

---

## STEP 1 — SOFTWARE INSTALLATION & PROJECT SETUP

### 1A — What to Install (do all of these before anything else)

**On your computer, install these 3 programs:**

1. **Arduino IDE 2.x**
   - Go to: https://www.arduino.cc/en/software
   - Click "Windows Win 10 and newer, 64 bits" (or Mac if you use Mac)
   - Install it like a normal program (Next → Next → Install)

2. **Android Studio**
   - Go to: https://developer.android.com/studio
   - Click the big green "Download Android Studio" button
   - Install it. When it opens for the first time, click "Next" on every screen until it finishes downloading. This takes 10–20 minutes.

3. **Cursor AI** (if not already installed)
   - Go to: https://cursor.sh
   - Download and install it

---

### 1B — Add ESP32 Support to Arduino IDE

Arduino IDE does not know about ESP32 by default. Do this once to add it:

1. Open Arduino IDE
2. Click **File** → **Preferences**
3. Find the box that says "Additional boards manager URLs"
4. Paste this URL into that box:
   `https://raw.githubusercontent.com/espressif/arduino-esp32/gh-pages/package_esp32_index.json`
5. Click **OK**
6. Click **Tools** → **Board** → **Boards Manager**
7. In the search box, type: `esp32`
8. Find "esp32 by Espressif Systems" and click **Install**
9. Wait for it to finish (2–5 minutes)

**Success looks like:** You can now go to Tools → Board → ESP32 Arduino → and see "ESP32 Dev Module" in the list.

---

### 1C — Install Required Arduino Libraries

In Arduino IDE:
1. Click **Sketch** → **Include Library** → **Manage Libraries**
2. Search for and install each of these one at a time:

| Library Name to Search | Author |
|---|---|
| `Firebase ESP Client` | Mobizt |
| `DallasTemperature` | Miles Burton |
| `OneWire` | Paul Stoffregen |
| `RTClib` | Adafruit |
| `ArduinoJson` | Benoit Blanchon |

For each one: type the name → find it in the list → click **Install**.

---

### 1D — Create the Project Folder Structure

Create a folder on your Desktop called `ShrimpHub`. Inside it, create these folders:

```
ShrimpHub/
├── firmware/
│   └── shrimphub_esp32/
│       └── shrimphub_esp32.ino   ← Arduino code goes here
├── android-app/                   ← Android Studio project goes here
├── firebase/
│   ├── database_schema.json
│   └── database.rules.json
└── docs/
    ├── wiring_guide.md
    └── testing_checklist.md
```

Create all the folders now. The `.ino` and `.json` files will be created in the next steps.

---

### 1E — Create a Firebase Project

1. Go to: https://console.firebase.google.com
2. Sign in with your Google account
3. Click **"Add project"**
4. Name it: `ShrimpHub`
5. Turn OFF Google Analytics (not needed) → click **Create project**
6. Once created, click **Continue**

**Enable Realtime Database:**
1. In the left menu, click **Build** → **Realtime Database**
2. Click **Create Database**
3. Choose your region (select the closest to Philippines — `asia-southeast1`)
4. Choose **"Start in test mode"** → click **Enable**

**Enable Authentication:**
1. In the left menu, click **Build** → **Authentication**
2. Click **Get started**
3. Click **Email/Password**
4. Toggle the first switch to **Enable** → click **Save**

**Create a user account for your farm:**
1. Still in Authentication, click the **Users** tab
2. Click **Add user**
3. Email: `farm@shrimphub.com`
4. Password: choose a strong password and write it down
5. Click **Add user**

**Get your Firebase credentials (you'll need these later):**
1. Click the ⚙️ gear icon (top left) → **Project settings**
2. Scroll down to find **"Your apps"** section
3. Click the **Web icon `</>`**
4. Register app name: `ShrimpHub Web`
5. You will see a block of code — find and copy these two values:
   - `apiKey: "..."` → this is your **API Key**
   - The database URL is: `https://shrimphub-default-rtdb.asia-southeast1.firebasedatabase.app` (shown in Realtime Database page)
6. Save both values in a text file — you'll need them in Step 3

---

## STEP 2 — FIREBASE DATABASE SCHEMA & RULES

### 2A — Create the database_schema.json file

Create the file `firebase/database_schema.json` with this content:

```json
{
  "aquarium": {
    "temp_val": 0.0,
    "tds_val": 0,
    "turb_val": 0,
    "water_lvl": 0.0,
    "last_sync": ""
  },
  "settings": {
    "max_temp": 32.0,
    "min_temp": 28.0,
    "min_sal": 15,
    "max_sal": 25,
    "max_turb": 45,
    "feed_time": "08:00",
    "overflow_limit": 5.0,
    "target_harvest_days": 75
  },
  "control": {
    "pump_stat": false,
    "filter_stat": false,
    "motor_trig": false,
    "aerator_stat": false,
    "op_mode": "auto"
  },
  "history": {
    "placeholder": {
      "log_id": "init",
      "timestamp": "2025-01-01 00:00:00",
      "param_type": "temp",
      "rec_val": 0.0
    }
  },
  "alerts": {
    "ammonia_risk": false,
    "overflow_risk": false,
    "temp_alert": false,
    "sal_alert": false
  },
  "growth": {
    "start_date": "",
    "days_of_culture": 0,
    "target_harvest_days": 75,
    "days_in_optimal_temp": 0,
    "performance_score": 0.0
  }
}
```

### 2B — Upload the schema to Firebase

1. Go to Firebase Console → **Realtime Database**
2. Click the 3-dot menu (⋮) at the top right of the data view
3. Click **Import JSON**
4. Upload the `database_schema.json` file you just created
5. Click **Import**

**Success looks like:** You see all the nodes (aquarium, settings, control, etc.) appear in the Firebase database.

### 2C — Set Security Rules

Create the file `firebase/database.rules.json`:

```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null",
    "history": {
      ".indexOn": ["timestamp", "param_type"]
    }
  }
}
```

To apply these rules in Firebase:
1. In Realtime Database, click the **Rules** tab
2. Delete everything in the text box
3. Paste the JSON content from above (the part starting with `{` and ending with `}`)
4. Click **Publish**

---

## STEP 3 — ESP32 FIRMWARE (Arduino Code)

### ⚠️ IMPORTANT NOTE ABOUT THE PIN LAYOUT

This firmware uses a **ULN2003 stepper driver** (for the 28BYJ-48 motor), NOT the NEMA-17. The ULN2003 uses 4 control pins (IN1–IN4) with half-stepping for smooth feeding motion.

**Updated Pin Map:**
```
DS18B20 Temperature Sensor   → GPIO 4
TDS/Salinity Sensor          → GPIO 34 (analog input)
Turbidity Sensor             → GPIO 35 (analog input)
Ultrasonic Sensor TRIG       → GPIO 12
Ultrasonic Sensor ECHO       → GPIO 13
Stepper IN1 (ULN2003)        → GPIO 25
Stepper IN2 (ULN2003)        → GPIO 26
Stepper IN3 (ULN2003)        → GPIO 27
Stepper IN4 (ULN2003)        → GPIO 14
RTC DS3231 SDA               → GPIO 21 (I2C Data)
RTC DS3231 SCL               → GPIO 22 (I2C Clock)
Relay 1 - Water Pump         → GPIO 32
Relay 2 - 3-Stage Filter     → GPIO 33
Relay 3 - Aerator            → GPIO 5
Relay 4 - LED Light          → GPIO 18
```

### 3A — Create the firmware file

Create the file `firmware/shrimphub_esp32/shrimphub_esp32.ino` with the complete code below.

**⚠️ BEFORE UPLOADING: Find the section labeled "REPLACE THESE VALUES" and fill in your own WiFi hotspot name, password, and Firebase credentials.**

```cpp
// ============================================================
// SHRIMPHUB v2 — ESP32 Firmware
// IoT Smart Shrimp Farm Monitor & Controller
// ============================================================
// This code reads sensors, controls hardware, syncs with
// Firebase, and runs automated feeding and water management.
// ============================================================

// --- Core Libraries ---
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

// --- Sensor Libraries ---
#include <OneWire.h>
#include <DallasTemperature.h>
#include <Wire.h>
#include <RTClib.h>

// ============================================================
// ⚠️ REPLACE THESE VALUES WITH YOUR OWN
// ============================================================
#define WIFI_SSID       "YOUR_HOTSPOT_NAME"       // Your phone hotspot name
#define WIFI_PASSWORD   "YOUR_HOTSPOT_PASSWORD"    // Your phone hotspot password
#define API_KEY         "YOUR_FIREBASE_API_KEY"    // From Firebase Project Settings
#define DATABASE_URL    "YOUR_FIREBASE_DATABASE_URL" // e.g. https://shrimphub-xxx.firebaseio.com
#define USER_EMAIL      "farm@shrimphub.com"       // The email you created in Firebase Auth
#define USER_PASSWORD   "YOUR_FIREBASE_PASSWORD"   // The password you created in Firebase Auth
// ============================================================

// ========== PIN DEFINITIONS ==========
// Sensor pins
#define ONE_WIRE_BUS    4    // DS18B20 temperature sensor data wire
#define TDS_PIN         34   // TDS/salinity sensor (analog)
#define TURBIDITY_PIN   35   // Turbidity sensor (analog)
#define TRIG_PIN        12   // Ultrasonic sensor trigger
#define ECHO_PIN        13   // Ultrasonic sensor echo

// Stepper motor pins (ULN2003 driver, 28BYJ-48 motor)
#define IN1             25
#define IN2             26
#define IN3             27
#define IN4             14

// Relay pins (LOW = ON for most relay modules)
#define RELAY_PUMP      32   // Relay 1: Water exchange pump
#define RELAY_FILTER    33   // Relay 2: 3-stage filter
#define RELAY_AERATOR   5    // Relay 3: Aerator/air pump
#define RELAY_LIGHT     18   // Relay 4: LED grow light

// ========== SENSOR OBJECTS ==========
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature tempSensor(&oneWire);
RTC_DS3231 rtc;

// ========== FIREBASE OBJECTS ==========
FirebaseData fbdo;      // Handles data read/write
FirebaseAuth auth;      // Handles authentication
FirebaseConfig config;  // Holds API key and database URL

// ========== SENSOR READING VARIABLES ==========
float temperature    = 0.0;  // Current temperature in Celsius
float tdsValue       = 0.0;  // Current TDS/salinity in ppm
float turbidityValue = 0.0;  // Current turbidity in NTU
float waterLevel     = 0.0;  // Current water level in cm

// ========== CALIBRATION FACTORS ==========
// Adjust these if your sensor readings seem off
float TDS_FACTOR       = 0.5;   // Multiplier for TDS calculation
float TURBIDITY_FACTOR = 1.0;   // Multiplier for turbidity calculation

// ========== TIMING VARIABLES ==========
unsigned long lastSensorRead    = 0;  // Tracks when we last read sensors
unsigned long lastFirebasePush  = 0;  // Tracks when we last sent to Firebase
unsigned long lastFeedCheck     = 0;  // Tracks when we last checked feed time
const long SENSOR_INTERVAL      = 3000;   // Read sensors every 3 seconds
const long FIREBASE_INTERVAL    = 15000;  // Push to Firebase every 15 seconds
const long FEED_CHECK_INTERVAL  = 60000;  // Check feed schedule every 60 seconds

// ========== SETTINGS FROM FIREBASE ==========
// These get updated from Firebase settings so the farmer can change them in the app
float max_temp       = 32.0;
float min_temp       = 28.0;
int   min_sal        = 15;
int   max_sal        = 25;
float max_turb       = 45.0;
float overflow_limit = 5.0;
String feed_time     = "08:00";

// ========== STEPPER MOTOR HALF-STEP SEQUENCE ==========
// This is the pattern for smooth 8-step motor control
int stepIndex = 0;
int stepSequence[8][4] = {
  {1, 0, 0, 0},
  {1, 1, 0, 0},
  {0, 1, 0, 0},
  {0, 1, 1, 0},
  {0, 0, 1, 0},
  {0, 0, 1, 1},
  {0, 0, 0, 1},
  {1, 0, 0, 1}
};

// ============================================================
// FUNCTION: connectToWiFi
// What it does: Connects the ESP32 to your phone's hotspot
// ============================================================
void connectToWiFi() {
  Serial.print("[WiFi] Connecting to: ");
  Serial.println(WIFI_SSID);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 20) {
    delay(500);
    Serial.print(".");
    attempts++;
  }
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n[WiFi] Connected! IP: " + WiFi.localIP().toString());
  } else {
    Serial.println("\n[WiFi] FAILED. Check hotspot name/password. Running offline.");
  }
}

// ============================================================
// FUNCTION: connectToFirebase
// What it does: Logs into Firebase using your email/password
// ============================================================
void connectToFirebase() {
  config.api_key      = API_KEY;
  config.database_url = DATABASE_URL;
  auth.user.email     = USER_EMAIL;
  auth.user.password  = USER_PASSWORD;
  config.token_status_callback = tokenStatusCallback;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
  Serial.println("[Firebase] Connecting...");
}

// ============================================================
// FUNCTION: readTemperature
// What it does: Gets the water temperature from DS18B20 sensor
// Returns: Temperature in Celsius
// ============================================================
float readTemperature() {
  tempSensor.requestTemperatures();
  float temp = tempSensor.getTempCByIndex(0);
  if (temp == -127.0) {
    Serial.println("[ERROR] Temperature sensor not found! Check wiring.");
    return 0.0;
  }
  return temp;
}

// ============================================================
// FUNCTION: readTDS
// What it does: Reads salinity/TDS from analog sensor
// Returns: TDS value in ppm (parts per million)
// ============================================================
float readTDS() {
  int raw = analogRead(TDS_PIN);
  float voltage = raw * (3.3 / 4095.0);
  float tds = voltage * 1000.0 * TDS_FACTOR;
  return tds;
}

// ============================================================
// FUNCTION: readTurbidity
// What it does: Reads water cloudiness from turbidity sensor
// Returns: Turbidity value in NTU (higher = cloudier = dirtier)
// ============================================================
float readTurbidity() {
  int raw = analogRead(TURBIDITY_PIN);
  float voltage = raw * (3.3 / 4095.0);
  float ntu = voltage * 100.0 * TURBIDITY_FACTOR;
  return ntu;
}

// ============================================================
// FUNCTION: readWaterLevel
// What it does: Uses ultrasonic sensor to measure water depth
// Returns: Distance in centimeters (smaller = more water)
// ============================================================
float readWaterLevel() {
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  long duration = pulseIn(ECHO_PIN, HIGH, 30000); // timeout after 30ms
  if (duration == 0) {
    Serial.println("[ERROR] Ultrasonic sensor timeout. Check wiring.");
    return 0.0;
  }
  float distance = duration * 0.034 / 2.0;
  return distance;
}

// ============================================================
// FUNCTION: stepMotor
// What it does: Moves the stepper motor one step forward
// Called many times to rotate the feed dispenser
// ============================================================
void stepMotor() {
  digitalWrite(IN1, stepSequence[stepIndex][0]);
  digitalWrite(IN2, stepSequence[stepIndex][1]);
  digitalWrite(IN3, stepSequence[stepIndex][2]);
  digitalWrite(IN4, stepSequence[stepIndex][3]);
  stepIndex = (stepIndex + 1) % 8;
}

// ============================================================
// FUNCTION: dispenseFeed
// What it does: Rotates motor to drop one portion of shrimp feed
// stepsCount: how many steps = how much feed (512 = ~1 full rotation)
// ============================================================
void dispenseFeed(int stepsCount) {
  Serial.println("[FEED] Dispensing feed...");
  for (int i = 0; i < stepsCount; i++) {
    stepMotor();
    delay(2);
  }
  // Turn off all stepper coils to save power and prevent heat
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);
  Serial.println("[FEED] Done.");
  // Log feed event to Firebase
  if (Firebase.ready()) {
    String key = "/history/" + String(millis());
    Firebase.RTDB.setString(&fbdo, key + "/param_type", "feed");
    Firebase.RTDB.setString(&fbdo, key + "/timestamp", getTimestamp());
    Firebase.RTDB.setFloat(&fbdo,  key + "/rec_val", 1.0);
  }
}

// ============================================================
// FUNCTION: getTimestamp
// What it does: Gets current date and time from RTC as a string
// Returns: String like "2025-06-15 08:30:00"
// ============================================================
String getTimestamp() {
  DateTime now = rtc.now();
  char buf[20];
  sprintf(buf, "%04d-%02d-%02d %02d:%02d:%02d",
    now.year(), now.month(), now.day(),
    now.hour(), now.minute(), now.second());
  return String(buf);
}

// ============================================================
// FUNCTION: checkAmmoniaRisk
// What it does: Estimates if ammonia is building up
// Uses turbidity + temperature as proxy indicators
// Automatically turns on filter and pump if risk detected
// ============================================================
void checkAmmoniaRisk() {
  bool risk = (turbidityValue > max_turb && temperature > 30.0);
  bool clear = (turbidityValue < 30.0 && temperature < 30.0);

  if (risk) {
    Serial.println("[ALERT] HIGH AMMONIA RISK! Activating filter and pump.");
    Firebase.RTDB.setBool(&fbdo, "/alerts/ammonia_risk", true);
    Firebase.RTDB.setBool(&fbdo, "/control/filter_stat", true);
    Firebase.RTDB.setBool(&fbdo, "/control/pump_stat", true);
    digitalWrite(RELAY_FILTER, LOW);  // LOW = ON for most relay modules
    digitalWrite(RELAY_PUMP, LOW);
  } else if (clear) {
    Firebase.RTDB.setBool(&fbdo, "/alerts/ammonia_risk", false);
    Firebase.RTDB.setBool(&fbdo, "/control/filter_stat", false);
    Firebase.RTDB.setBool(&fbdo, "/control/pump_stat", false);
    digitalWrite(RELAY_FILTER, HIGH); // HIGH = OFF
    digitalWrite(RELAY_PUMP, HIGH);
  }
}

// ============================================================
// FUNCTION: checkOverflow
// What it does: Checks if water level is dangerously low
// Activates pump to refill if needed
// ============================================================
void checkOverflow() {
  if (waterLevel > overflow_limit) { // Ultrasonic reads DISTANCE, so bigger = less water
    Serial.println("[ALERT] WATER LEVEL LOW! Activating pump.");
    Firebase.RTDB.setBool(&fbdo, "/alerts/overflow_risk", true);
    Firebase.RTDB.setBool(&fbdo, "/control/pump_stat", true);
    digitalWrite(RELAY_PUMP, LOW);
  } else {
    Firebase.RTDB.setBool(&fbdo, "/alerts/overflow_risk", false);
    digitalWrite(RELAY_PUMP, HIGH);
  }
}

// ============================================================
// FUNCTION: checkTempAlert
// What it does: Sets temperature alert flag in Firebase
// ============================================================
void checkTempAlert() {
  bool alert = (temperature > max_temp || temperature < min_temp);
  Firebase.RTDB.setBool(&fbdo, "/alerts/temp_alert", alert);
}

// ============================================================
// FUNCTION: checkFeedSchedule
// What it does: Checks the RTC clock against the scheduled feed time
// If it matches, dispenses one portion of feed
// ============================================================
void checkFeedSchedule() {
  DateTime now = rtc.now();
  String currentTime = String(now.hour()) + ":" +
                       (now.minute() < 10 ? "0" : "") + String(now.minute());
  if (currentTime == feed_time) {
    Serial.println("[FEED] Scheduled feed time reached: " + currentTime);
    dispenseFeed(512); // 512 steps = approx. 1 full rotation
  }
}

// ============================================================
// FUNCTION: pushSensorData
// What it does: Sends all sensor readings to Firebase
// This is what the Android app reads to show live data
// ============================================================
void pushSensorData() {
  if (!Firebase.ready()) {
    Serial.println("[Firebase] Not ready. Skipping push.");
    return;
  }
  String ts = getTimestamp();
  // Update live aquarium readings
  Firebase.RTDB.setFloat(&fbdo,  "/aquarium/temp_val",  temperature);
  Firebase.RTDB.setFloat(&fbdo,  "/aquarium/tds_val",   tdsValue);
  Firebase.RTDB.setFloat(&fbdo,  "/aquarium/turb_val",  turbidityValue);
  Firebase.RTDB.setFloat(&fbdo,  "/aquarium/water_lvl", waterLevel);
  Firebase.RTDB.setString(&fbdo, "/aquarium/last_sync", ts);

  // Log to history (each reading gets a unique key based on time)
  String baseKey = "/history/" + String(millis());
  Firebase.RTDB.setString(&fbdo, baseKey + "t/param_type", "temp");
  Firebase.RTDB.setFloat(&fbdo,  baseKey + "t/rec_val",   temperature);
  Firebase.RTDB.setString(&fbdo, baseKey + "t/timestamp", ts);

  Firebase.RTDB.setString(&fbdo, baseKey + "s/param_type", "salinity");
  Firebase.RTDB.setFloat(&fbdo,  baseKey + "s/rec_val",   tdsValue);
  Firebase.RTDB.setString(&fbdo, baseKey + "s/timestamp", ts);

  Firebase.RTDB.setString(&fbdo, baseKey + "u/param_type", "turbidity");
  Firebase.RTDB.setFloat(&fbdo,  baseKey + "u/rec_val",   turbidityValue);
  Firebase.RTDB.setString(&fbdo, baseKey + "u/timestamp", ts);

  Firebase.RTDB.setString(&fbdo, baseKey + "w/param_type", "water_level");
  Firebase.RTDB.setFloat(&fbdo,  baseKey + "w/rec_val",   waterLevel);
  Firebase.RTDB.setString(&fbdo, baseKey + "w/timestamp", ts);

  Serial.println("[Firebase] Data pushed at: " + ts);
}

// ============================================================
// FUNCTION: readSettingsFromFirebase
// What it does: Downloads the latest settings from Firebase
// This lets the farmer change thresholds from the app
// ============================================================
void readSettingsFromFirebase() {
  if (!Firebase.ready()) return;
  if (Firebase.RTDB.getFloat(&fbdo,  "/settings/max_temp"))       max_temp       = fbdo.floatData();
  if (Firebase.RTDB.getFloat(&fbdo,  "/settings/min_temp"))       min_temp       = fbdo.floatData();
  if (Firebase.RTDB.getInt(&fbdo,    "/settings/min_sal"))        min_sal        = fbdo.intData();
  if (Firebase.RTDB.getInt(&fbdo,    "/settings/max_sal"))        max_sal        = fbdo.intData();
  if (Firebase.RTDB.getFloat(&fbdo,  "/settings/max_turb"))       max_turb       = fbdo.floatData();
  if (Firebase.RTDB.getFloat(&fbdo,  "/settings/overflow_limit")) overflow_limit = fbdo.floatData();
  if (Firebase.RTDB.getString(&fbdo, "/settings/feed_time"))      feed_time      = fbdo.stringData();
}

// ============================================================
// FUNCTION: readControlFromFirebase
// What it does: Checks if the farmer toggled any relays from the app
// Executes the command by turning relays on or off
// ============================================================
void readControlFromFirebase() {
  if (!Firebase.ready()) return;
  bool pump_stat, filter_stat, aerator_stat, motor_trig;
  if (Firebase.RTDB.getBool(&fbdo, "/control/pump_stat"))    { pump_stat    = fbdo.boolData(); digitalWrite(RELAY_PUMP,    pump_stat    ? LOW : HIGH); }
  if (Firebase.RTDB.getBool(&fbdo, "/control/filter_stat"))  { filter_stat  = fbdo.boolData(); digitalWrite(RELAY_FILTER,  filter_stat  ? LOW : HIGH); }
  if (Firebase.RTDB.getBool(&fbdo, "/control/aerator_stat")) { aerator_stat = fbdo.boolData(); digitalWrite(RELAY_AERATOR, aerator_stat ? LOW : HIGH); }
  if (Firebase.RTDB.getBool(&fbdo, "/control/motor_trig")) {
    motor_trig = fbdo.boolData();
    if (motor_trig) {
      dispenseFeed(512);
      Firebase.RTDB.setBool(&fbdo, "/control/motor_trig", false); // Reset after feeding
    }
  }
}

// ============================================================
// SETUP — Runs once when the ESP32 powers on
// ============================================================
void setup() {
  Serial.begin(115200);
  delay(500);
  Serial.println("\n=============================");
  Serial.println("   ShrimpHub v2 Starting...");
  Serial.println("=============================");

  // Initialize temperature sensor
  tempSensor.begin();

  // Initialize I2C for RTC
  Wire.begin(21, 22);
  if (!rtc.begin()) {
    Serial.println("[ERROR] RTC module not found! Check SDA→GPIO21 and SCL→GPIO22.");
    while (1); // Stop here if RTC is missing
  }
  if (rtc.lostPower()) {
    Serial.println("[RTC] Power was lost, setting time to compile time.");
    rtc.adjust(DateTime(F(__DATE__), F(__TIME__)));
  }

  // Set relay pins as outputs and turn all OFF (HIGH = OFF for active-low relays)
  pinMode(RELAY_PUMP,    OUTPUT); digitalWrite(RELAY_PUMP,    HIGH);
  pinMode(RELAY_FILTER,  OUTPUT); digitalWrite(RELAY_FILTER,  HIGH);
  pinMode(RELAY_AERATOR, OUTPUT); digitalWrite(RELAY_AERATOR, HIGH);
  pinMode(RELAY_LIGHT,   OUTPUT); digitalWrite(RELAY_LIGHT,   HIGH);

  // Set ultrasonic pins
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);

  // Set stepper motor pins
  pinMode(IN1, OUTPUT); pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT); pinMode(IN4, OUTPUT);

  // Connect to WiFi and Firebase
  connectToWiFi();
  if (WiFi.status() == WL_CONNECTED) {
    connectToFirebase();
    delay(3000); // Give Firebase time to authenticate
    readSettingsFromFirebase();
  }

  Serial.println("[READY] ShrimpHub is running!");
}

// ============================================================
// LOOP — Runs continuously every few milliseconds
// ============================================================
void loop() {
  unsigned long now = millis();

  // === Read sensors every 3 seconds ===
  if (now - lastSensorRead >= SENSOR_INTERVAL) {
    lastSensorRead = now;
    temperature    = readTemperature();
    tdsValue       = readTDS();
    turbidityValue = readTurbidity();
    waterLevel     = readWaterLevel();

    // Print to Serial Monitor so you can see readings on your computer
    Serial.println("\n===== SHRIMPHUB SENSOR DATA =====");
    DateTime t = rtc.now();
    Serial.printf("Time       : %02d:%02d:%02d\n", t.hour(), t.minute(), t.second());
    Serial.printf("Temperature: %.2f °C\n", temperature);
    Serial.printf("TDS/Sal    : %.2f ppm\n", tdsValue);
    Serial.printf("Turbidity  : %.2f NTU\n", turbidityValue);
    Serial.printf("Water Level: %.2f cm\n", waterLevel);
    Serial.println("WiFi: " + String(WiFi.status() == WL_CONNECTED ? "Connected" : "OFFLINE"));
    Serial.println("=================================");
  }

  // === Push to Firebase and run automations every 15 seconds ===
  if (now - lastFirebasePush >= FIREBASE_INTERVAL) {
    lastFirebasePush = now;

    if (WiFi.status() != WL_CONNECTED) {
      Serial.println("[WiFi] Reconnecting...");
      connectToWiFi();
    }

    if (WiFi.status() == WL_CONNECTED) {
      pushSensorData();
      readSettingsFromFirebase();
      readControlFromFirebase();
      checkAmmoniaRisk();
      checkOverflow();
      checkTempAlert();
    } else {
      Serial.println("[OFFLINE] Running local automation only.");
      // Local automation still works even without WiFi
      checkAmmoniaRisk();
      checkOverflow();
    }
  }

  // === Check feed schedule every 60 seconds ===
  if (now - lastFeedCheck >= FEED_CHECK_INTERVAL) {
    lastFeedCheck = now;
    checkFeedSchedule();
  }
}
```

### 3B — How to Upload the Code to ESP32

1. Connect your ESP32 to your computer using a USB cable
2. Open Arduino IDE
3. Go to **Tools** → **Board** → **ESP32 Arduino** → select **"ESP32 Dev Module"**
4. Go to **Tools** → **Port** → select the port that appeared (usually COM3, COM4 on Windows, or /dev/cu... on Mac)
5. Click the **Upload button** (→ arrow icon) at the top
6. Wait for it to finish — you'll see "Done uploading" at the bottom
7. Open **Tools** → **Serial Monitor**, set baud rate to **115200**
8. You should see sensor readings printing every 3 seconds

**If you see errors:**
- `RTC not found` → Check that GPIO21 = SDA wire and GPIO22 = SCL wire
- `Temperature: -127` → Check DS18B20 data wire is on GPIO4
- `WiFi: OFFLINE` → Check your hotspot name and password are spelled correctly

---

## STEP 4 — ANDROID STUDIO PROJECT SETUP

### 4A — Create a New Android Project

1. Open **Android Studio**
2. Click **"New Project"**
3. Select **"Empty Views Activity"** → click **Next**
4. Fill in these details:
   - **Name:** `ShrimpHub`
   - **Package name:** `com.shrimphub.app`
   - **Save location:** Click the folder icon and choose your `ShrimpHub/android-app/` folder
   - **Language:** `Java`
   - **Minimum SDK:** `API 29 ("Q"; Android 10.0)`
5. Click **Finish**
6. Wait for Android Studio to finish loading (the progress bar at the bottom must complete — can take 5 minutes)

### 4B — Add Firebase to the Android App

**Method: Use the Firebase Assistant (easiest way)**

1. In Android Studio, go to **Tools** → **Firebase**
2. A panel opens on the right side
3. Click **Authentication** → **Authenticate using a custom authentication system**
4. Click **"Connect to Firebase"** — it opens your browser
5. Select your **ShrimpHub** Firebase project → click **Connect**
6. Come back to Android Studio → click **"Add the Firebase Authentication SDK to your app"** → click **Accept Changes**

Repeat the same for Realtime Database:
1. In Firebase panel, click **Realtime Database** → **Save and retrieve data**
2. Click **"Add the Realtime Database SDK"** → **Accept Changes**

### 4C — Add All Dependencies to build.gradle

1. In Android Studio, on the left side find and open: **app → build.gradle** (the one that says `Module: app`)
2. Inside the `dependencies { }` block, add these lines:

```gradle
implementation 'com.google.firebase:firebase-database:20.3.0'
implementation 'com.google.firebase:firebase-auth:22.3.0'
implementation 'com.google.firebase:firebase-messaging:23.4.0'
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
implementation 'com.google.android.material:material:1.11.0'
```

3. At the very top of `build.gradle`, also add this inside the `repositories` section if not already there:
```gradle
maven { url 'https://jitpack.io' }
```

4. Click **"Sync Now"** (yellow bar at the top) — wait for it to finish

---

## STEP 5 — ANDROID APP SCREENS

Build the following 5 screens. For each screen, give me:
- The **complete XML layout file** (the visual design)
- The **complete Java Activity file** (the logic/code)
- Plain English instructions on where to create each file in Android Studio

---

### SCREEN 1: Login Screen (`LoginActivity.java` + `activity_login.xml`)
- ShrimpHub logo/title at top
- Email input field
- Password input field
- "Login" button → Firebase Auth sign in
- Error message if login fails (show as red text below the button)
- On success → navigate to DashboardActivity

---

### SCREEN 2: Dashboard Screen (`DashboardActivity.java` + `activity_dashboard.xml`)
Real-time live readings from Firebase `/aquarium/`.

Layout:
- **Alert banner** at top (hidden by default, appears red when any alert is active)
- **4 sensor cards** in a 2x2 grid:
  - 🌡️ Temperature (°C) — card turns RED if outside 28–32°C range
  - 🧂 Salinity/TDS (ppm) — card turns RED if outside 15–25 ppm
  - 💧 Turbidity (NTU) — card turns RED if above 45 NTU
  - 📏 Water Level (cm) — card turns RED if above overflow_limit
- **Control section** with 4 toggle buttons:
  - Pump (writes to `/control/pump_stat`)
  - Filter (writes to `/control/filter_stat`)
  - Aerator (writes to `/control/aerator_stat`)
  - Feed Now (writes `true` to `/control/motor_trig`)
- **Bottom navigation bar** with 4 tabs: Dashboard | History | Growth | Settings

Firebase listener: Use `addValueEventListener` on `/aquarium/` and `/alerts/` to update UI in real-time.

---

### SCREEN 3: History Screen (`HistoryActivity.java` + `activity_history.xml`)
- **MPAndroidChart LineChart** at top showing last 24 hours
- **4 toggle buttons** above the chart: Temperature | Salinity | Turbidity | Water Level
  - Tapping each one filters the chart to show only that parameter
- **RecyclerView** list below the chart showing recent history entries
  - Each list item shows: timestamp, parameter name, value
- Data source: Firebase `/history/` node, ordered by timestamp, limited to last 50 entries

---

### SCREEN 4: Growth Advisory Screen (`GrowthActivity.java` + `activity_growth.xml`)
Data from Firebase `/growth/`.

Layout:
- **Days of Culture counter** (big number, days since start_date)
- **Target harvest day** (from settings)
- **Performance score** (0–100%) calculated as:
  ```
  Score = [(days_in_optimal_temp / days_of_culture × 0.6) + (target_days / days_of_culture × 0.4)] × 100
  ```
- **Status badge:**
  - ≥ 80% → green → "🟢 On Track — Excellent conditions!"
  - 60–79% → yellow → "🟡 Good — Monitor water closely."
  - < 60% → red → "🔴 Needs Attention — Check parameters now."
- **"Set Start Date" button** → opens a DatePickerDialog → saves selected date to Firebase `/growth/start_date`
- Auto-calculate and write `days_of_culture` to Firebase when start date changes

---

### SCREEN 5: Settings Screen (`SettingsActivity.java` + `activity_settings.xml`)
Load current values from Firebase `/settings/` when screen opens.
Show editable fields for:
- Max Temperature
- Min Temperature
- Min Salinity
- Max Salinity
- Max Turbidity
- Feed Time (use a TimePickerDialog when tapped)
- Overflow Limit (cm)
- Target Harvest Days

**"Save Settings" button** at bottom → writes all values back to Firebase `/settings/`.
Show a success toast message: "✅ Settings saved!"

---

## STEP 6 — PUSH NOTIFICATIONS (FCM)

### 6A — Create the messaging service

Create a new Java class in Android Studio: `MyFirebaseMessagingService.java`

This service must:
1. Listen for FCM messages even when the app is closed
2. When `ammonia_risk = true` → show notification: "⚠️ ShrimpHub: High Ammonia Risk! Filter activated."
3. When `overflow_risk = true` → show notification: "⚠️ ShrimpHub: Water level low! Pump activated."
4. When `temp_alert = true` → show notification: "⚠️ ShrimpHub: Temperature out of safe range!"

### 6B — Register listeners for alert changes

In `DashboardActivity.java`, add Firebase listeners on `/alerts/` that:
- Watch for any alert value changing to `true`
- Trigger a local notification immediately

### 6C — Firebase Console setup for FCM

Tell me step by step how to:
1. Enable Cloud Messaging in Firebase Console
2. Find the Server Key
3. Test a notification manually from the Firebase console

---

## STEP 7 — WIRING GUIDE

Create `docs/wiring_guide.md` with a complete beginner-friendly wiring table and instructions.

Include:
- A full wiring table (Component | Wire Color | ESP32 Pin)
- Power supply diagram notes (which components need 5V, which need 3.3V, which need 12V)
- Step-by-step instructions to test each sensor one at a time using Serial Monitor
- Safety rules (always power off before connecting wires, never short 12V to sensor pins)
- Photo descriptions (describe what the correct wiring should look like)

---

## STEP 8 — TESTING CHECKLIST

Create `docs/testing_checklist.md` with all tests below:

**Unit Tests:**
- [ ] UT01 — Temperature sensor reads correctly (±2% of a real thermometer)
- [ ] UT02 — TDS sensor reads a non-zero value in saltwater
- [ ] UT03 — Turbidity reads higher in cloudy water vs. clear water
- [ ] UT04 — Ultrasonic reads a smaller number when water level is higher
- [ ] UT05 — Stepper motor rotates when `motor_trig` is set to true in Firebase

**Integration Tests:**
- [ ] IT01 — Serial Monitor shows all 4 readings every 3 seconds
- [ ] IT02 — Firebase dashboard shows updated values within 15 seconds
- [ ] IT03 — Android app dashboard updates within 5 seconds of sensor change
- [ ] IT04 — Toggling pump button in app turns relay on/off on the hardware

**System Tests:**
- [ ] ST01 — Stir water to increase turbidity → verify filter relay activates
- [ ] ST02 — Block ultrasonic sensor → verify overflow alert fires
- [ ] ST03 — Set feed time to 2 minutes from now → verify motor spins
- [ ] ST04 — Turn off hotspot → verify ESP32 keeps running offline

**Acceptance Tests:**
- [ ] AT01 — Run for 24 hours → verify history has continuous entries
- [ ] AT02 — Growth score calculates correctly for a 30-day culture
- [ ] AT03 — App receives push notification within 10 seconds of alert
- [ ] AT04 — App login/logout works correctly

---

## ⚠️ IMPORTANT RULES FOR CURSOR AI — READ THESE FIRST

1. I am a **non-coder**. Write every instruction as if I have never coded before.
2. When I need to find a file in Android Studio, tell me exactly which folder it's in and what to click.
3. Show **complete code only** — never partial snippets with `// ... rest of code`. I need the full file every time.
4. After every code block, tell me the **exact filename** and **exact folder** where it goes.
5. Mark every placeholder I must replace with `⚠️ REPLACE THIS:` so I can find them easily.
6. Tell me what **success looks like** after every step (e.g., "You should see the app load and show the login screen").
7. If an error is likely, tell me in advance: "If you see X error, it means Y. Fix it by doing Z."
8. After Step 8 is done, provide a **Final Deployment Checklist** — everything to do before turning on the system at a real shrimp tank.

---

**Start with STEP 1 now. After you finish, wait for me to say "done, next step".**
