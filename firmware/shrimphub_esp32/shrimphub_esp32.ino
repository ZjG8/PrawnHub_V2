// ============================================================
// SHRIMPHUB v2 - ESP32 Firmware
// IoT Smart Shrimp Farm Monitor & Controller
// ============================================================

#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

#include <OneWire.h>
#include <DallasTemperature.h>
#include <Wire.h>
#include <RTClib.h>

// ============================================================
// FIREBASE / WIFI CONFIG
// Fill in WiFi and Firebase Auth account before uploading.
// Database URL and API key are from app/google-services.json.
// ============================================================
#define WIFI_SSID       "REPLACE_THIS_YOUR_HOTSPOT_NAME"
#define WIFI_PASSWORD   "REPLACE_THIS_YOUR_HOTSPOT_PASSWORD"
#define API_KEY         "AIzaSyDrdRxa7PuJX5XOtC2_WjAQoNHYy1cFjkA"
#define DATABASE_URL    "https://prawnhub-a5eca-default-rtdb.asia-southeast1.firebasedatabase.app"
#define USER_EMAIL      "REPLACE_THIS_FIREBASE_AUTH_EMAIL"
#define USER_PASSWORD   "REPLACE_THIS_FIREBASE_AUTH_PASSWORD"
// ============================================================

// Keep these hardware pins aligned with plans/PrawnHub-Connect.md.
#define ONE_WIRE_BUS    4
#define TDS_PIN         34
#define TURBIDITY_PIN   35
#define TRIG_PIN        12
#define ECHO_PIN        13

#define IN1             25
#define IN2             26
#define IN3             27
#define IN4             14

#define RELAY_PUMP      16
#define RELAY_FILTER    17
#define RELAY_AERATOR   18

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature tempSensor(&oneWire);
RTC_DS3231 rtc;

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

bool rtcAvailable = false;
float temperature = 0.0;
float tdsValue = 0.0;
float turbidityValue = 0.0;
float waterLevel = 0.0;
float oxygenValue = 0.0;
float phValue = 0.0;

float TDS_FACTOR = 0.5;
float TURBIDITY_FACTOR = 1.0;

unsigned long lastSensorRead = 0;
unsigned long lastFirebasePush = 0;
unsigned long lastFeedCheck = 0;
const long SENSOR_INTERVAL = 3000;
const long FIREBASE_INTERVAL = 15000;
const long FEED_CHECK_INTERVAL = 60000;
const int HISTORY_MAX_ENTRIES = 100;
unsigned long historySequence = 0;

float max_temp = 32.0;
float min_temp = 28.0;
int min_sal = 15;
int max_sal = 25;
float max_turb = 45.0;
float overflow_limit = 5.0;
float min_oxygen = 5.0;
float min_ph = 6.5;
float max_ph = 8.5;
String feed_time = "08:00";

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

void connectToFirebase() {
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.token_status_callback = tokenStatusCallback;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
  Serial.println("[Firebase] Connecting...");
}

float readTemperature() {
  tempSensor.requestTemperatures();
  float temp = tempSensor.getTempCByIndex(0);
  if (temp == -127.0) {
    Serial.println("[ERROR] Temperature sensor not found! Check wiring.");
    return 0.0;
  }
  return temp;
}

float readTDS() {
  int raw = analogRead(TDS_PIN);
  float voltage = raw * (3.3 / 4095.0);
  return voltage * 1000.0 * TDS_FACTOR;
}

float readTurbidity() {
  int raw = analogRead(TURBIDITY_PIN);
  float voltage = raw * (3.3 / 4095.0);
  return voltage * 100.0 * TURBIDITY_FACTOR;
}

float readWaterLevel() {
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  long duration = pulseIn(ECHO_PIN, HIGH, 30000);
  if (duration == 0) {
    Serial.println("[ERROR] Ultrasonic sensor timeout. Check wiring.");
    return 0.0;
  }
  return duration * 0.034 / 2.0;
}

String getTimestamp() {
  if (!rtcAvailable) {
    return "boot-" + String(millis());
  }
  DateTime now = rtc.now();
  char buf[20];
  sprintf(buf, "%04d-%02d-%02d %02d:%02d:%02d",
          now.year(), now.month(), now.day(),
          now.hour(), now.minute(), now.second());
  return String(buf);
}

String historyKey(unsigned long sequence) {
  return "/history/log_" + String(sequence);
}

void syncHistorySequenceFromFirebase() {
  if (!Firebase.ready()) return;
  if (Firebase.RTDB.getInt(&fbdo, "/history_meta/next_id")) {
    historySequence = (unsigned long)fbdo.intData();
  }
}

void trimOldHistoryEntry() {
  if (historySequence <= HISTORY_MAX_ENTRIES) {
    return;
  }
  unsigned long oldSequence = historySequence - HISTORY_MAX_ENTRIES;
  Firebase.RTDB.remove(&fbdo, historyKey(oldSequence));
}

void logHistoryRecord(const String &paramType, float value, const String &status, const String &timestamp) {
  if (!Firebase.ready()) return;

  historySequence++;
  FirebaseJson json;
  json.set("param_type", paramType);
  json.set("timestamp", timestamp);
  json.set("rec_val", value);
  json.set("status", status);

  Firebase.RTDB.setJSON(&fbdo, historyKey(historySequence), &json);
  Firebase.RTDB.setInt(&fbdo, "/history_meta/next_id", historySequence);
  trimOldHistoryEntry();
}

void stepMotor() {
  digitalWrite(IN1, stepSequence[stepIndex][0]);
  digitalWrite(IN2, stepSequence[stepIndex][1]);
  digitalWrite(IN3, stepSequence[stepIndex][2]);
  digitalWrite(IN4, stepSequence[stepIndex][3]);
  stepIndex = (stepIndex + 1) % 8;
}

void dispenseFeed(int stepsCount) {
  Serial.println("[FEED] Dispensing feed...");
  for (int i = 0; i < stepsCount; i++) {
    stepMotor();
    delay(2);
  }
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);
  Serial.println("[FEED] Done.");

  if (Firebase.ready()) {
    logHistoryRecord("feed", 1.0, "feed dispensed", getTimestamp());
  }
}

void setFirebaseBool(const String &path, bool value) {
  if (Firebase.ready()) {
    Firebase.RTDB.setBool(&fbdo, path, value);
  }
}

void checkAmmoniaRisk() {
  bool risk = (turbidityValue > max_turb && temperature > 30.0);
  bool clear = (turbidityValue < 30.0 && temperature < 30.0);

  if (risk) {
    Serial.println("[ALERT] HIGH AMMONIA RISK! Activating filter and pump.");
    setFirebaseBool("/alerts/ammonia_risk", true);
    setFirebaseBool("/control/filter_stat", true);
    setFirebaseBool("/control/pump_stat", true);
    digitalWrite(RELAY_FILTER, LOW);
    digitalWrite(RELAY_PUMP, LOW);
  } else if (clear) {
    setFirebaseBool("/alerts/ammonia_risk", false);
    setFirebaseBool("/control/filter_stat", false);
    setFirebaseBool("/control/pump_stat", false);
    digitalWrite(RELAY_FILTER, HIGH);
    digitalWrite(RELAY_PUMP, HIGH);
  }
}

void checkOverflow() {
  if (waterLevel > overflow_limit) {
    Serial.println("[ALERT] WATER LEVEL LOW! Activating pump.");
    setFirebaseBool("/alerts/overflow_risk", true);
    setFirebaseBool("/control/pump_stat", true);
    digitalWrite(RELAY_PUMP, LOW);
  } else {
    setFirebaseBool("/alerts/overflow_risk", false);
    digitalWrite(RELAY_PUMP, HIGH);
  }
}

String statusText(bool alert) {
  return alert ? "alert" : "normal";
}

void updateAlertFlags() {
  bool tempAlert = (temperature > max_temp || temperature < min_temp);
  bool salAlert = (tdsValue < min_sal || tdsValue > max_sal);
  bool turbidityAlert = (turbidityValue > max_turb);
  bool oxygenAlert = (oxygenValue > 0.0 && oxygenValue < min_oxygen);
  bool phAlert = (phValue > 0.0 && (phValue < min_ph || phValue > max_ph));

  setFirebaseBool("/alerts/temp_alert", tempAlert);
  setFirebaseBool("/alerts/sal_alert", salAlert);
  setFirebaseBool("/alerts/turbidity_alert", turbidityAlert);
  setFirebaseBool("/alerts/oxygen_alert", oxygenAlert);
  setFirebaseBool("/alerts/ph_alert", phAlert);
}

void checkFeedSchedule() {
  if (!rtcAvailable) {
    return;
  }
  DateTime now = rtc.now();
  char currentTimeBuffer[6];
  sprintf(currentTimeBuffer, "%02d:%02d", now.hour(), now.minute());
  String currentTime = String(currentTimeBuffer);
  if (currentTime == feed_time) {
    Serial.println("[FEED] Scheduled feed time reached: " + currentTime);
    dispenseFeed(512);
  }
}

void pushSensorData() {
  if (!Firebase.ready()) {
    Serial.println("[Firebase] Not ready. Skipping push.");
    return;
  }

  String ts = getTimestamp();
  Firebase.RTDB.setFloat(&fbdo, "/aquarium/temp_val", temperature);
  Firebase.RTDB.setFloat(&fbdo, "/aquarium/tds_val", tdsValue);
  Firebase.RTDB.setFloat(&fbdo, "/aquarium/turb_val", turbidityValue);
  Firebase.RTDB.setFloat(&fbdo, "/aquarium/water_lvl", waterLevel);
  Firebase.RTDB.setFloat(&fbdo, "/aquarium/oxygen_val", oxygenValue);
  Firebase.RTDB.setFloat(&fbdo, "/aquarium/do_val", oxygenValue);
  Firebase.RTDB.setFloat(&fbdo, "/aquarium/ph_val", phValue);
  Firebase.RTDB.setString(&fbdo, "/aquarium/last_sync", ts);

  bool tempAlert = (temperature > max_temp || temperature < min_temp);
  bool salAlert = (tdsValue < min_sal || tdsValue > max_sal);
  bool turbidityAlert = (turbidityValue > max_turb);
  bool waterAlert = (waterLevel > overflow_limit);

  logHistoryRecord("temp", temperature, statusText(tempAlert), ts);
  logHistoryRecord("salinity", tdsValue, statusText(salAlert), ts);
  logHistoryRecord("turbidity", turbidityValue, statusText(turbidityAlert), ts);
  logHistoryRecord("water_level", waterLevel, statusText(waterAlert), ts);

  Serial.println("[Firebase] Data pushed at: " + ts);
}

void readSettingsFromFirebase() {
  if (!Firebase.ready()) return;
  if (Firebase.RTDB.getFloat(&fbdo, "/settings/max_temp")) max_temp = fbdo.floatData();
  if (Firebase.RTDB.getFloat(&fbdo, "/settings/min_temp")) min_temp = fbdo.floatData();
  if (Firebase.RTDB.getInt(&fbdo, "/settings/min_sal")) min_sal = fbdo.intData();
  if (Firebase.RTDB.getInt(&fbdo, "/settings/max_sal")) max_sal = fbdo.intData();
  if (Firebase.RTDB.getFloat(&fbdo, "/settings/max_turb")) max_turb = fbdo.floatData();
  if (Firebase.RTDB.getFloat(&fbdo, "/settings/overflow_limit")) overflow_limit = fbdo.floatData();
  if (Firebase.RTDB.getFloat(&fbdo, "/settings/min_oxygen")) min_oxygen = fbdo.floatData();
  if (Firebase.RTDB.getFloat(&fbdo, "/settings/min_ph")) min_ph = fbdo.floatData();
  if (Firebase.RTDB.getFloat(&fbdo, "/settings/max_ph")) max_ph = fbdo.floatData();
  if (Firebase.RTDB.getString(&fbdo, "/settings/feed_time")) feed_time = fbdo.stringData();
}

void readControlFromFirebase() {
  if (!Firebase.ready()) return;
  if (Firebase.RTDB.getBool(&fbdo, "/control/pump_stat")) {
    digitalWrite(RELAY_PUMP, fbdo.boolData() ? LOW : HIGH);
  }
  if (Firebase.RTDB.getBool(&fbdo, "/control/filter_stat")) {
    digitalWrite(RELAY_FILTER, fbdo.boolData() ? LOW : HIGH);
  }
  if (Firebase.RTDB.getBool(&fbdo, "/control/aerator_stat")) {
    digitalWrite(RELAY_AERATOR, fbdo.boolData() ? LOW : HIGH);
  }
  if (Firebase.RTDB.getBool(&fbdo, "/control/motor_trig") && fbdo.boolData()) {
    dispenseFeed(512);
    Firebase.RTDB.setBool(&fbdo, "/control/motor_trig", false);
  }
}

void setup() {
  Serial.begin(115200);
  delay(500);
  Serial.println("\n=============================");
  Serial.println("   ShrimpHub v2 Starting...");
  Serial.println("=============================");

  tempSensor.begin();
  Wire.begin(21, 22);
  rtcAvailable = rtc.begin();
  if (!rtcAvailable) {
    Serial.println("[ERROR] RTC module not found! Check SDA->GPIO21 and SCL->GPIO22.");
    Serial.println("[RTC] Continuing without scheduled feeding until RTC is connected.");
  } else if (rtc.lostPower()) {
    Serial.println("[RTC] Power was lost, setting time to compile time.");
    rtc.adjust(DateTime(F(__DATE__), F(__TIME__)));
  }

  pinMode(RELAY_PUMP, OUTPUT);
  pinMode(RELAY_FILTER, OUTPUT);
  pinMode(RELAY_AERATOR, OUTPUT);
  digitalWrite(RELAY_PUMP, HIGH);
  digitalWrite(RELAY_FILTER, HIGH);
  digitalWrite(RELAY_AERATOR, HIGH);

  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);

  connectToWiFi();
  if (WiFi.status() == WL_CONNECTED) {
    connectToFirebase();
    delay(3000);
    syncHistorySequenceFromFirebase();
    readSettingsFromFirebase();
  }

  Serial.println("[READY] ShrimpHub is running!");
}

void loop() {
  unsigned long now = millis();

  if (now - lastSensorRead >= SENSOR_INTERVAL) {
    lastSensorRead = now;
    temperature = readTemperature();
    tdsValue = readTDS();
    turbidityValue = readTurbidity();
    waterLevel = readWaterLevel();

    Serial.println("\n===== SHRIMPHUB SENSOR DATA =====");
    if (rtcAvailable) {
      DateTime t = rtc.now();
      Serial.printf("Time       : %02d:%02d:%02d\n", t.hour(), t.minute(), t.second());
    } else {
      Serial.println("Time       : RTC unavailable");
    }
    Serial.printf("Temperature: %.2f C\n", temperature);
    Serial.printf("TDS/Sal    : %.2f ppm\n", tdsValue);
    Serial.printf("Turbidity  : %.2f NTU\n", turbidityValue);
    Serial.printf("Water Level: %.2f cm\n", waterLevel);
    Serial.println("WiFi: " + String(WiFi.status() == WL_CONNECTED ? "Connected" : "OFFLINE"));
    Serial.println("=================================");
  }

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
      updateAlertFlags();
    } else {
      Serial.println("[OFFLINE] Running local automation only.");
      checkAmmoniaRisk();
      checkOverflow();
    }
  }

  if (now - lastFeedCheck >= FEED_CHECK_INTERVAL) {
    lastFeedCheck = now;
    checkFeedSchedule();
  }
}
