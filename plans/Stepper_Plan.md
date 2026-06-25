✅ PROMPT: Add Smart Stepper Feeding System to Existing PrawnHub Code

You are working with an ESP32 IoT system called PrawnHub that already includes:

Firebase Realtime Database (live + history logging)
2 water pumps (relay control via Firebase)
OTA wireless updates (ArduinoOTA)
Sensors (temperature, TDS, turbidity, ultrasonic water level)
RTC DS3231 for timestamps
🎯 TASK

Integrate a stepper motor feeding system into the existing code without breaking any current functionality.

🔧 HARDWARE ADDED

A 4-wire stepper motor is connected using GPIO pins:

IN1 = GPIO32  
IN2 = GPIO33  
IN3 = GPIO18  
IN4 = GPIO19

Driver: ULN2003 or similar

⚙️ REQUIREMENTS
1. Stepper Motor Control
Use half-step sequence (8-step table) or equivalent stable sequence
Must include:
stepMotor(steps, speedDelay)
smooth rotation (no jitter)
Ensure pins are properly set as OUTPUT in setup()
2. Feeding Logic (AUTOMATIC)

Replace or upgrade existing feeding logic:

Feed every 6 hours (default)
Use:
const unsigned long FEED_INTERVAL = 21600000;
Function:
handleFeeder()

must:

check time interval using millis()
trigger stepper motor rotation
log serial messages:
"FEEDING START"
"FEED DONE"
3. OPTIONAL (IMPORTANT UPGRADE)

Add Firebase manual control:

Create node:

/ShrimpHub/control/feed

Behavior:

If TRUE → trigger feeding immediately
After feeding → reset to FALSE automatically
4. SAFETY RULES
Feeding must NOT block:
Firebase loop
OTA handling
pump control
No use of delay() inside main loop (only inside stepper function is allowed but must be short)
Must not interfere with WiFi stability
5. INTEGRATION RULES
Do NOT modify:
Firebase structure
Pump control logic
Sensor reading logic
Only ADD:
Stepper variables
Stepper functions
Feeder controller
Optional Firebase trigger
6. DESIRED RESULT

System must:

Continue normal IoT monitoring
Still control pumps via Firebase
Still support OTA updates
Automatically feed fish/shrimp using stepper motor every 6 hours
Allow manual feeding via Firebase
🔥 OUTPUT EXPECTATION

Return:

Full updated Arduino code
Clean integration
No missing pins
No broken OTA
Stable loop execution