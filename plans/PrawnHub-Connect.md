/*************************************************
   ShrimpHub Firebase IoT System (ESP32)
   -------------------------------------
   FEATURES:
   - Firebase Realtime Database
   - DS18B20 Temperature
   - TDS Sensor
   - Turbidity Sensor
   - Ultrasonic Water Level
   - RTC DS3231
   - 3 Relay Pumps
   - 28BYJ-48 Stepper Motor

   REQUIRED LIBRARIES:
   - Firebase ESP Client by Mobizt
   - OneWire
   - DallasTemperature
   - RTClib
*************************************************/

// ================= LIBRARIES =================
#include <WiFi.h>
#include <Firebase_ESP_Client.h>

#include <OneWire.h>
#include <DallasTemperature.h>
#include <Wire.h>
#include <RTClib.h>

// Firebase helper libraries
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

// ================= WIFI =================
#define WIFI_SSID "Khali"
#define WIFI_PASSWORD "Maximysabelle22."

// ================= FIREBASE =================
#define API_KEY "AIzaSyDrdRxa7PuJX5XOtC2_WjAQoNHYy1cFjkA"
#define DATABASE_URL "https://prawnhub-a5eca-default-rtdb.asia-southeast1.firebasedatabase.app/"

// Firebase Login
#define USER_EMAIL "admin@prawnhub.com"
#define USER_PASSWORD "12345678"

// ================= SENSOR PINS =================
#define ONE_WIRE_BUS 4
#define TDS_PIN 34
#define TURBIDITY_PIN 35

// Ultrasonic
#define TRIG_PIN 12
#define ECHO_PIN 13

// ================= RELAY PINS =================
#define RELAY_PUMP1 16
#define RELAY_PUMP2 17
#define RELAY_PUMP3 18

// ================= STEPPER PINS =================
#define IN1 25
#define IN2 26
#define IN3 27
#define IN4 14

// ================= FIREBASE OBJECTS =================
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

// ================= SENSOR OBJECTS =================
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);
RTC_DS3231 rtc;

// ================= VARIABLES =================
float temperature = 0;
float tdsValue = 0;
float turbidityValue = 0;
float waterLevel = 0;

bool signupOK = false;

// ================= STEPPER VARIABLES =================
int stepIndex = 0;

int steps[8][4] = {
  {1,0,0,0},
  {1,1,0,0},
  {0,1,0,0},
  {0,1,1,0},
  {0,0,1,0},
  {0,0,1,1},
  {0,0,0,1},
  {1,0,0,1}
};

// ================= SETUP =================
void setup() {

  Serial.begin(115200);
  delay(1000);

  Serial.println("\n==============================");
  Serial.println("      SHRIMPHUB SYSTEM");
  Serial.println("==============================");

  // ================= WIFI =================
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  Serial.print("Connecting to WiFi");

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }

  Serial.println("\nWiFi Connected!");
  Serial.println(WiFi.localIP());

  // ================= FIREBASE =================
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  config.token_status_callback = tokenStatusCallback;

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  Serial.println("Connecting to Firebase...");

  // ================= SENSORS =================
  sensors.begin();

  Wire.begin(21, 22);

  if (!rtc.begin()) {
    Serial.println("RTC NOT FOUND!");
  }

  // ================= ULTRASONIC =================
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);

  // ================= RELAYS =================
  pinMode(RELAY_PUMP1, OUTPUT);
  pinMode(RELAY_PUMP2, OUTPUT);
  pinMode(RELAY_PUMP3, OUTPUT);

  digitalWrite(RELAY_PUMP1, HIGH);
  digitalWrite(RELAY_PUMP2, HIGH);
  digitalWrite(RELAY_PUMP3, HIGH);

  // ================= STEPPER =================
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);

  Serial.println("System Ready!");
}

// ================= ULTRASONIC =================
float getDistanceCM() {

  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);

  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);

  digitalWrite(TRIG_PIN, LOW);

  long duration = pulseIn(ECHO_PIN, HIGH, 30000);

  if (duration == 0) {
    return -1;
  }

  float distance = duration * 0.0343 / 2;

  return distance;
}

// ================= STEPPER =================
void stepMotor() {

  digitalWrite(IN1, steps[stepIndex][0]);
  digitalWrite(IN2, steps[stepIndex][1]);
  digitalWrite(IN3, steps[stepIndex][2]);
  digitalWrite(IN4, steps[stepIndex][3]);

  stepIndex++;

  if (stepIndex > 7) {
    stepIndex = 0;
  }
}

void dispenseFeed(int stepCount) {

  Serial.println("[FEED] Dispensing Feed");

  for (int i = 0; i < stepCount; i++) {
    stepMotor();
    delay(2);
  }

  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);

  Serial.println("[DONE] Feed Dispensed");
}

// ================= LOOP =================
void loop() {

  // ================= READ SENSORS =================
  sensors.requestTemperatures();
  temperature = sensors.getTempCByIndex(0);

  int tdsRaw = analogRead(TDS_PIN);
  float tdsVoltage = tdsRaw * (3.3 / 4095.0);
  tdsValue = tdsVoltage * 500;

  int turbidityRaw = analogRead(TURBIDITY_PIN);
  float turbidityVoltage = turbidityRaw * (3.3 / 4095.0);
  turbidityValue = turbidityVoltage * 100;

  waterLevel = getDistanceCM();

  DateTime now = rtc.now();

  // ================= SERIAL MONITOR =================
  Serial.println("\n========== SHRIMPHUB ==========");

  Serial.print("Temp: ");
  Serial.print(temperature);
  Serial.println(" C");

  Serial.print("TDS: ");
  Serial.print(tdsValue);
  Serial.println(" ppm");

  Serial.print("Turbidity: ");
  Serial.print(turbidityValue);
  Serial.println(" NTU");

  Serial.print("Water Level: ");
  Serial.print(waterLevel);
  Serial.println(" cm");

  Serial.println("================================");

  // ================= FIREBASE SEND =================
  if (Firebase.ready()) {

    // Android app reads these fields from /aquarium.
    Firebase.RTDB.setFloat(&fbdo, "/aquarium/temp_val", temperature);
    Firebase.RTDB.setFloat(&fbdo, "/aquarium/tds_val", tdsValue);
    Firebase.RTDB.setFloat(&fbdo, "/aquarium/turb_val", turbidityValue);
    Firebase.RTDB.setFloat(&fbdo, "/aquarium/water_lvl", waterLevel);
    Firebase.RTDB.setFloat(&fbdo, "/aquarium/oxygen_val", 0.0);
    Firebase.RTDB.setFloat(&fbdo, "/aquarium/do_val", 0.0);
    Firebase.RTDB.setFloat(&fbdo, "/aquarium/ph_val", 0.0);

    // Keep the old /ShrimpHub fields temporarily for Firebase console checking.
    Firebase.RTDB.setFloat(&fbdo, "/ShrimpHub/temperature", temperature);
    Firebase.RTDB.setFloat(&fbdo, "/ShrimpHub/tds", tdsValue);
    Firebase.RTDB.setFloat(&fbdo, "/ShrimpHub/turbidity", turbidityValue);
    Firebase.RTDB.setFloat(&fbdo, "/ShrimpHub/waterLevel", waterLevel);

    Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/hour", now.hour());
    Firebase.RTDB.setInt(&fbdo, "/ShrimpHub/minute", now.minute());

    Serial.println("[FIREBASE] Data Uploaded");
  }
  else {
    Serial.println("[FIREBASE] Not Ready");
  }

  // ================= AUTOMATION =================

  // Dirty water detected
  if (turbidityValue > 50) {

    Serial.println("[ACTION] Filtration Activated");

    digitalWrite(RELAY_PUMP1, LOW);
    delay(5000);
    digitalWrite(RELAY_PUMP1, HIGH);

    digitalWrite(RELAY_PUMP2, LOW);
    delay(5000);
    digitalWrite(RELAY_PUMP2, HIGH);

    digitalWrite(RELAY_PUMP3, LOW);
    delay(5000);
    digitalWrite(RELAY_PUMP3, HIGH);
  }

  // Feeding schedule
  if (now.hour() == 8 && now.minute() == 0) {
    dispenseFeed(512);
  }

  delay(5000);
}
