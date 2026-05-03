# ShrimpHub Wiring Guide

Use this guide with the ESP32 powered off. Connect one part at a time, test it, then move to the next part.

## Wiring Table

| Component | Wire / Signal | ESP32 Pin |
|---|---|---|
| DS18B20 temperature sensor | Data | GPIO 4 |
| DS18B20 temperature sensor | VCC | 3.3V |
| DS18B20 temperature sensor | GND | GND |
| TDS / salinity sensor | Analog output | GPIO 34 |
| TDS / salinity sensor | VCC | 3.3V or module-rated 5V |
| TDS / salinity sensor | GND | GND |
| Turbidity sensor | Analog output | GPIO 35 |
| Turbidity sensor | VCC | 3.3V or module-rated 5V |
| Turbidity sensor | GND | GND |
| HC-SR04 ultrasonic sensor | TRIG | GPIO 12 |
| HC-SR04 ultrasonic sensor | ECHO | GPIO 13 through a level shifter or voltage divider |
| HC-SR04 ultrasonic sensor | VCC | 5V |
| HC-SR04 ultrasonic sensor | GND | GND |
| RTC DS3231 | SDA | GPIO 21 |
| RTC DS3231 | SCL | GPIO 22 |
| RTC DS3231 | VCC | 3.3V or 5V, depending on module |
| RTC DS3231 | GND | GND |
| ULN2003 stepper driver | IN1 | GPIO 25 |
| ULN2003 stepper driver | IN2 | GPIO 26 |
| ULN2003 stepper driver | IN3 | GPIO 27 |
| ULN2003 stepper driver | IN4 | GPIO 14 |
| 4-channel relay | Relay 1 input, water pump | GPIO 32 |
| 4-channel relay | Relay 2 input, filter | GPIO 33 |
| 4-channel relay | Relay 3 input, aerator | GPIO 5 |
| 4-channel relay | Relay 4 input, LED light | GPIO 18 |

## Power Notes

The ESP32 uses 3.3V logic. Do not connect a 5V signal directly into an ESP32 input pin unless a level shifter or voltage divider is used.

The HC-SR04 ECHO pin normally outputs 5V. Use a voltage divider before GPIO 13.

The relay module may need 5V power. The controlled pump, filter, aerator, or light may need a separate 12V or AC supply depending on your hardware. Keep the ESP32 control wiring separate from high-current pump wiring.

All low-voltage modules must share a common GND with the ESP32.

## Test Each Part

1. Temperature sensor: upload the firmware and open Serial Monitor at 115200 baud. Touch the probe with your hand. Success looks like the temperature changes slowly.
2. TDS sensor: place the probe in clean water, then saltwater. Success looks like saltwater reading higher than clean water.
3. Turbidity sensor: test clear water, then cloudy water. Success looks like cloudy water reading higher.
4. Ultrasonic sensor: move a flat object closer to the sensor. Success looks like the distance number getting smaller.
5. RTC DS3231: restart the ESP32. Success looks like the printed time continuing instead of resetting to zero.
6. Stepper motor: set `/control/motor_trig` to `true` in Firebase. Success looks like the 28BYJ-48 motor rotating once.
7. Relays: toggle pump, filter, and aerator in the app. Success looks like the matching relay clicking.

## Safety Rules

Power off before connecting or moving wires.

Never connect 12V or AC power to an ESP32 pin.

Keep wet probes and pump wiring physically separated from the ESP32 board.

Use waterproof sensor housings and drip loops so water cannot run along wires into electronics.

Test with a small container of water before installing anything near a real shrimp tank.

## Photo Checklist

Your finished wiring should show the ESP32 on a dry surface, sensor cables routed away from relay power terminals, the relay high-voltage side covered or enclosed, and the DS3231 connected to GPIO 21 and GPIO 22. The ULN2003 board should be next to the 28BYJ-48 motor, with IN1 through IN4 connected in pin order.
