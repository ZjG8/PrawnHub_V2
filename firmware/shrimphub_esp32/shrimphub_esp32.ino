// ============================================================
// SHRIMPHUB - ESP32 Firmware
// IoT Smart Shrimp Farm Monitor & Controller
// ============================================================

#include <WiFi.h>
#include <ArduinoOTA.h>
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

#define ONE_WIRE_BUS    25
#define TDS_PIN         34
#define TURBIDITY_PIN   35
#define TRIG_PIN        12
#define ECHO_PIN        13

// Stepper feeder pins from plans/STEPPERCURRY.md.
#define IN1             32
#define IN2             33
#define IN3             18
#define IN4             19

// Relay pins from plans/STEPPERCURRY.md.
#define RELAY_PUMP      16
#define RELAY_PUMP2     17
#define RELAY_FILTER    27
#define RELAY_AERATOR   5

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature tempSensor(&oneWire);
RTC_DS3231 rtc;

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

bool rtcAvailable = false;
bool otaStarted = false;
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
unsigned long lastFeedMillis = 0;
unsigned long lastFeedControlRead = 0;
unsigned long lastScheduleCheck = 0;
unsigned long lastMotorStepMillis = 0;
const long SENSOR_INTERVAL = 3000;
const long FIREBASE_INTERVAL = 15000;
const long FEED_CONTROL_INTERVAL = 1000;
const long SCHEDULE_CHECK_INTERVAL = 1000;
const unsigned long FEED_INTERVAL = 21600000;
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
unsigned long feedIntervalMillis = FEED_INTERVAL;
int feedAmount = 512;
int feedCount = 0;
bool autoFeedEnabled = true;
bool feeding = false;
bool feedRequested = false;
String feedRequestType = "manual";
String lastScheduleKey = "";
String activeFeedType = "manual";
int activeFeedAmount = 0;
int feedStepsRemaining = 0;
int feedSpeedDelay = 2;
int feedHour1 = 8;
int feedMinute1 = 0;
int feedHour2 = 12;
int feedMinute2 = 0;
int feedHour3 = 18;
int feedMinute3 = 0;

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

void setupOTA() {
  if (otaStarted) {
    return;
  }
  ArduinoOTA.setHostname("ShrimpHub-ESP32");
  ArduinoOTA.onStart([]() {
    Serial.println("[OTA] Update start");
  });
  ArduinoOTA.onEnd([]() {
    Serial.println("\n[OTA] Update complete");
  });
  ArduinoOTA.onError([](ota_error_t error) {
    Serial.printf("[OTA] Error[%u]\n", error);
  });
  ArduinoOTA.begin();
  otaStarted = true;
  Serial.println("[OTA] Ready");
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

unsigned long getUnixTime() {
  if (!rtcAvailable) {
    return millis() / 1000UL;
  }
  return rtc.now().unixtime();
}

String getReadableDate() {
  return getTimestamp();
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

void releaseStepper() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);
}

void stepMotor(int steps, int speedDelay) {
  if (steps <= 0) {
    return;
  }

  unsigned long now = millis();
  unsigned long delayMs = (unsigned long)max(1, speedDelay);
  if (now - lastMotorStepMillis < delayMs) {
    return;
  }

  lastMotorStepMillis = now;
  digitalWrite(IN1, stepSequence[stepIndex][0]);
  digitalWrite(IN2, stepSequence[stepIndex][1]);
  digitalWrite(IN3, stepSequence[stepIndex][2]);
  digitalWrite(IN4, stepSequence[stepIndex][3]);
  stepIndex = (stepIndex + 1) % 8;
  feedStepsRemaining--;
}

void writeFeederStatus(bool active) {
  if (!Firebase.ready()) return;
  Firebase.RTDB.setBool(&fbdo, "/ShrimpHub/feeder/feeding", active);
}

void writeFeedResult(int stepsCount, const String &type) {
  if (!Firebase.ready()) return;

  unsigned long unixTime = getUnixTime();
  String date = getReadableDate();
  String logKey = "/ShrimpHub/feedLogs/log_" + String(unixTime) + "_" + String(millis());

  feedCount++;
  FirebaseJson json;
  json.set("timestamp", (int)unixTime);
  json.set("date", date);
  json.set("feedAmount", stepsCount);
  json.set("type", type);

  Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/feeder/feedCount", feedCount);
  Firebase.RTDB.setString(&fbdo, "/ShrimpHub/feeder/lastFeedTime", date);
  Firebase.RTDB.setJSON(&fbdo, logKey, &json);
}

void startFeeding(int stepsCount, const String &type) {
  if (feeding) {
    return;
  }

  activeFeedAmount = max(1, stepsCount);
  activeFeedType = type;
  feedStepsRemaining = activeFeedAmount;
  lastMotorStepMillis = 0;
  feeding = true;
  writeFeederStatus(true);
  Serial.println("FEEDING START");
}

void finishFeeding() {
  releaseStepper();
  Serial.println("FEED DONE");
  writeFeedResult(activeFeedAmount, activeFeedType);

  if (Firebase.ready()) {
    Firebase.RTDB.setBool(&fbdo, "/ShrimpHub/feeder/feedNow", false);
    Firebase.RTDB.setBool(&fbdo, "/ShrimpHub/feeder/feeding", false);
    logHistoryRecord("feed", 1.0, "feed dispensed", getTimestamp());
  }
  activeFeedAmount = 0;
  feedStepsRemaining = 0;
  feeding = false;
}

void cancelFeeding() {
  releaseStepper();
  Serial.println("FEED STOPPED");
  if (Firebase.ready()) {
    Firebase.RTDB.setBool(&fbdo, "/ShrimpHub/feeder/feedNow", false);
    Firebase.RTDB.setBool(&fbdo, "/ShrimpHub/feeder/feeding", false);
  }
  activeFeedAmount = 0;
  feedStepsRemaining = 0;
  feedRequested = false;
  feeding = false;
}

void requestFeed(const String &type) {
  if (feeding || feedRequested) {
    return;
  }
  feedRequestType = type;
  feedRequested = true;
}

void setFirebaseBool(const String &path, bool value) {
  if (Firebase.ready()) {
    Firebase.RTDB.setBool(&fbdo, path, value);
  }
}

void setRelay(int pin, bool active) {
  digitalWrite(pin, active ? LOW : HIGH);
}

void checkAmmoniaRisk() {
  bool risk = (turbidityValue > max_turb && temperature > 30.0);
  bool clear = (turbidityValue < 30.0 && temperature < 30.0);

  if (risk) {
    Serial.println("[ALERT] HIGH AMMONIA RISK! Activating filter and pump.");
    setFirebaseBool("/alerts/ammonia_risk", true);
    setFirebaseBool("/ShrimpHub/control/filter", true);
    setFirebaseBool("/ShrimpHub/control/pump1", true);
    setRelay(RELAY_FILTER, true);
    setRelay(RELAY_PUMP, true);
  } else if (clear) {
    setFirebaseBool("/alerts/ammonia_risk", false);
    setFirebaseBool("/ShrimpHub/control/filter", false);
    setFirebaseBool("/ShrimpHub/control/pump1", false);
    setRelay(RELAY_FILTER, false);
    setRelay(RELAY_PUMP, false);
  }
}

void checkOverflow() {
  if (waterLevel > overflow_limit) {
    Serial.println("[ALERT] WATER LEVEL LOW! Activating pump.");
    setFirebaseBool("/alerts/overflow_risk", true);
    setFirebaseBool("/ShrimpHub/control/pump1", true);
    setRelay(RELAY_PUMP, true);
  } else {
    setFirebaseBool("/alerts/overflow_risk", false);
    setRelay(RELAY_PUMP, false);
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

void handleFeeder() {
  if (feeding) {
    stepMotor(feedStepsRemaining, feedSpeedDelay);
    if (feedStepsRemaining <= 0) {
      finishFeeding();
    }
    return;
  }

  if (feedRequested) {
    String type = feedRequestType;
    feedRequested = false;
    lastFeedMillis = millis();
    startFeeding(feedAmount, type);
  }
}

String scheduleKey(DateTime now) {
  char key[13];
  sprintf(key, "%04d%02d%02d%02d%02d", now.year(), now.month(), now.day(), now.hour(), now.minute());
  return String(key);
}

bool isScheduleTime(DateTime now, int hour, int minute) {
  return now.hour() == hour && now.minute() == minute;
}

void checkScheduledFeeding() {
  if (!rtcAvailable || !autoFeedEnabled || feeding || feedRequested) {
    return;
  }

  DateTime now = rtc.now();
  bool due = isScheduleTime(now, feedHour1, feedMinute1)
          || isScheduleTime(now, feedHour2, feedMinute2)
          || isScheduleTime(now, feedHour3, feedMinute3);
  if (!due) {
    return;
  }

  String key = scheduleKey(now);
  if (key == lastScheduleKey) {
    return;
  }

  lastScheduleKey = key;
  requestFeed("scheduled");
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
  Firebase.RTDB.setFloat(&fbdo, "/ShrimpHub/temperature", temperature);
  Firebase.RTDB.setFloat(&fbdo, "/ShrimpHub/tds", tdsValue);
  Firebase.RTDB.setFloat(&fbdo, "/ShrimpHub/turbidity", turbidityValue);
  Firebase.RTDB.setFloat(&fbdo, "/ShrimpHub/waterLevel", waterLevel);

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
  if (Firebase.RTDB.getInt(&fbdo, "/settings/feed_interval_hours")) {
    int intervalHours = fbdo.intData();
    feedIntervalMillis = intervalHours > 0 ? (unsigned long)intervalHours * 3600000UL : FEED_INTERVAL;
  }
  if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedAmount")) feedAmount = max(1, fbdo.intData());
  if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedCount")) feedCount = max(0, fbdo.intData());
  if (Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/feeder/autoFeedEnabled")) autoFeedEnabled = fbdo.boolData();
  if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedHour1")) feedHour1 = constrain(fbdo.intData(), 0, 23);
  else if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/schedule/feedHour1")) feedHour1 = constrain(fbdo.intData(), 0, 23);
  if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedMinute1")) feedMinute1 = constrain(fbdo.intData(), 0, 59);
  else if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/schedule/feedMinute1")) feedMinute1 = constrain(fbdo.intData(), 0, 59);
  if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedHour2")) feedHour2 = constrain(fbdo.intData(), 0, 23);
  else if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/schedule/feedHour2")) feedHour2 = constrain(fbdo.intData(), 0, 23);
  if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedMinute2")) feedMinute2 = constrain(fbdo.intData(), 0, 59);
  else if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/schedule/feedMinute2")) feedMinute2 = constrain(fbdo.intData(), 0, 59);
  if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedHour3")) feedHour3 = constrain(fbdo.intData(), 0, 23);
  else if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/schedule/feedHour3")) feedHour3 = constrain(fbdo.intData(), 0, 23);
  if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedMinute3")) feedMinute3 = constrain(fbdo.intData(), 0, 59);
  else if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/schedule/feedMinute3")) feedMinute3 = constrain(fbdo.intData(), 0, 59);
}

void initializeFeederDefaults() {
  if (!Firebase.ready()) return;

  Firebase.RTDB.setBool(&fbdo, "/ShrimpHub/feeder/feeding", false);
  Firebase.RTDB.setBool(&fbdo, "/ShrimpHub/feeder/feedNow", false);
  if (!Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedAmount")) {
    Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/feeder/feedAmount", feedAmount);
  } else {
    feedAmount = max(1, fbdo.intData());
  }
  if (!Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedCount")) {
    Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/feeder/feedCount", feedCount);
  } else {
    feedCount = max(0, fbdo.intData());
  }
  if (!Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/feeder/autoFeedEnabled")) {
    Firebase.RTDB.setBool(&fbdo, "/ShrimpHub/feeder/autoFeedEnabled", autoFeedEnabled);
  } else {
    autoFeedEnabled = fbdo.boolData();
  }
}

void initializeScheduleDefaults() {
  if (!Firebase.ready()) return;

  if (!Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedHour1")) {
    Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/feeder/feedHour1", feedHour1);
  } else {
    feedHour1 = constrain(fbdo.intData(), 0, 23);
  }
  if (!Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedMinute1")) {
    Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/feeder/feedMinute1", feedMinute1);
  } else {
    feedMinute1 = constrain(fbdo.intData(), 0, 59);
  }
  if (!Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedHour2")) {
    Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/feeder/feedHour2", feedHour2);
  } else {
    feedHour2 = constrain(fbdo.intData(), 0, 23);
  }
  if (!Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedMinute2")) {
    Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/feeder/feedMinute2", feedMinute2);
  } else {
    feedMinute2 = constrain(fbdo.intData(), 0, 59);
  }
  if (!Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedHour3")) {
    Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/feeder/feedHour3", feedHour3);
  } else {
    feedHour3 = constrain(fbdo.intData(), 0, 23);
  }
  if (!Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedMinute3")) {
    Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/feeder/feedMinute3", feedMinute3);
  } else {
    feedMinute3 = constrain(fbdo.intData(), 0, 59);
  }
}

void readControlFromFirebase() {
  if (!Firebase.ready()) return;
  if (Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/control/pump1")) {
    setRelay(RELAY_PUMP, fbdo.boolData());
  }
  if (Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/control/pump2")) {
    setRelay(RELAY_PUMP2, fbdo.boolData());
  }
  if (Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/control/filter")) {
    setRelay(RELAY_FILTER, fbdo.boolData());
  }
  if (Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/control/aerator")) {
    setRelay(RELAY_AERATOR, fbdo.boolData());
  }
}

void readFeedControlFromFirebase() {
  if (!Firebase.ready()) return;
  if (Firebase.RTDB.getInt(&fbdo, "/ShrimpHub/feeder/feedAmount")) {
    feedAmount = max(1, fbdo.intData());
  }
  if (Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/feeder/autoFeedEnabled")) {
    autoFeedEnabled = fbdo.boolData();
  }
  if (feeding
      && Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/feeder/feeding")
      && !fbdo.boolData()) {
    cancelFeeding();
    return;
  }
  if (Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/feeder/feedNow") && fbdo.boolData()) {
    requestFeed("manual");
  }
  if (Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/control/feed") && fbdo.boolData()) {
    requestFeed("manual");
    Firebase.RTDB.setBool(&fbdo, "/ShrimpHub/control/feed", false);
  }
  if (Firebase.RTDB.getBool(&fbdo, "/ShrimpHub/control/motor_trig") && fbdo.boolData()) {
    requestFeed("manual");
    Firebase.RTDB.setBool(&fbdo, "/ShrimpHub/control/motor_trig", false);
  }
}

void setup() {
  Serial.begin(115200);
  delay(500);
  Serial.println("\n=============================");
  Serial.println("   ShrimpHub Starting...");
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
  pinMode(RELAY_PUMP2, OUTPUT);
  pinMode(RELAY_FILTER, OUTPUT);
  pinMode(RELAY_AERATOR, OUTPUT);
  setRelay(RELAY_PUMP, false);
  setRelay(RELAY_PUMP2, false);
  setRelay(RELAY_FILTER, false);
  setRelay(RELAY_AERATOR, false);

  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);
  releaseStepper();

  connectToWiFi();
  if (WiFi.status() == WL_CONNECTED) {
    setupOTA();
    connectToFirebase();
    delay(3000);
    syncHistorySequenceFromFirebase();
    initializeFeederDefaults();
    initializeScheduleDefaults();
    readSettingsFromFirebase();
  }

  lastFeedMillis = millis();
  Serial.println("[READY] ShrimpHub is running!");
}

void loop() {
  unsigned long now = millis();

  if (WiFi.status() == WL_CONNECTED) {
    ArduinoOTA.handle();
  }

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
      setupOTA();
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

  if (now - lastFeedControlRead >= FEED_CONTROL_INTERVAL) {
    lastFeedControlRead = now;
    readFeedControlFromFirebase();
  }
  if (now - lastScheduleCheck >= SCHEDULE_CHECK_INTERVAL) {
    lastScheduleCheck = now;
    checkScheduledFeeding();
  }
  handleFeeder();
}
