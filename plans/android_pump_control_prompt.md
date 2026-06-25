# SHRIMPHUB ANDROID FIREBASE PUMP CONTROL (Pump1 + Pump2)

## Context
We are building an Android Studio app (Kotlin) that controls an ESP32 system via Firebase Realtime Database.

The ESP32 reads these Firebase nodes:


/ShrimpHub/control/pump1 (Boolean)
/ShrimpHub/control/pump2 (Boolean)


Values:
- true  = pump ON
- false = pump OFF

The ESP32 already handles relay switching based on these values.

---

## PROBLEM TO FIX
The Android app currently:
- Creates duplicate nodes (example: pump_k1, pump_stat, or extra control nodes)
- Writes inconsistent structure
- Sometimes does not update ESP32 correctly
- Causes UI instability when history/control is modified

---

## REQUIRED FIREBASE STRUCTURE (STRICT)
The app MUST ONLY use this structure:


ShrimpHub
└── control
├── pump1 (Boolean)
└── pump2 (Boolean)


❌ DO NOT create:
- pump_k1
- pump_stat
- control inside control
- duplicated nodes
- dynamic random keys

---

## ANDROID FEATURES REQUIRED

### 1. Real-time sync (Firebase listener)
- Use `addValueEventListener`
- Listen ONLY to:


/ShrimpHub/control


- Read:
  - pump1
  - pump2

Update UI switches in real time.

---

### 2. Switch behavior (VERY IMPORTANT)

Each switch must:

#### Pump 1 Switch
- ON → set:

ShrimpHub/control/pump1 = true


- OFF → set:

ShrimpHub/control/pump1 = false


#### Pump 2 Switch
- ON → set:

ShrimpHub/control/pump2 = true


- OFF → set:

ShrimpHub/control/pump2 = false


---

### 3. Prevent Firebase duplication bug

Implement ALL of the following:

#### RULE 1: No push() usage
❌ NEVER use:
- push()
- setValue under random nodes
- child(autoId)

#### RULE 2: Always use direct path update
✔ Use:

databaseReference.child("ShrimpHub")
.child("control")
.child("pump1")
.setValue(true)


---

### 4. UI REQUIREMENTS

Dashboard must include:

- Switch Pump 1
- Switch Pump 2

Each switch:
- reflects Firebase state in real time
- does NOT trigger loops (avoid infinite listener update)

---

### 5. CRITICAL FIX (MOST IMPORTANT)

Avoid this bug:

❌ When listener updates switch → switch triggers Firebase write again → loop crash

✔ Solution:
- Add boolean flag:

```kotlin
var isUpdatingUI = false

When Firebase updates UI:

set isUpdatingUI = true
update switch
set isUpdatingUI = false

When switch clicked:

only write if isUpdatingUI == false
6. DATA LISTENER EXAMPLE (REQUIRED)

Implement:

FirebaseDatabase.getInstance().reference
addValueEventListener on:
/ShrimpHub/control

Parse:

val pump1 = snapshot.child("pump1").getValue(Boolean::class.java) ?: false
val pump2 = snapshot.child("pump2").getValue(Boolean::class.java) ?: false

Update UI switches accordingly.

7. EXPECTED RESULT
No duplicate nodes in Firebase
Clean structure only:
pump1, pump2
Switch instantly reflects ESP32 state
ESP32 reacts in real-time
No UI freezing / crashing
No history interference
OPTIONAL IMPROVEMENT

Add debounce:

300–500ms delay before writing Firebase on switch toggle
GOAL

Make a stable Android Kotlin Firebase control panel that safely controls ESP32 pump1 and pump2 without:

duplication bugs
infinite loops
UI crashes
incorrect Firebase structure

---

If you want next step, I can also:
- :contentReference[oaicite:0]{index=0}
- or :contentReference[oaicite:1]{index=1}
- or :contentReference[oaicite:2]{index=2} (this is usually a listener/write loop bug)