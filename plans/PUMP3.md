✅ 2. UI PROMPT (MARKDOWN FOR YOUR DASHBOARD IMPLEMENTATION)

Use this in Codex / UI generator:

# PrawnHub Dashboard Update – Pump 3 Control Integration

Update the existing Farmer Dashboard UI (DashboardActivity) to support a new water pump control system.

## 🔧 New Feature: Pump 3 Control

### Firebase Path

/ShrimpHub/control/pump3


---

## 🧩 UI REQUIREMENTS

### Add to "System Controls" section:

```text
System Controls
[Switch] Pump 1
[Switch] Pump 2
[Switch] Pump 3   ← NEW
🔄 FUNCTIONAL BEHAVIOR
Pump 3 Switch Logic
ON → write true to Firebase /ShrimpHub/control/pump3
OFF → write false to Firebase
Must reflect real-time Firebase state changes
📡 REAL-TIME SYNC
Listen using Firebase Realtime Database listener
Update switch state automatically if changed from another device
Prevent UI flicker or loop updates
🧠 LOGIC RULES
Must not block existing Pump 1 and Pump 2 logic
Must not affect sensor dashboard or history logs
Must work inside existing DashboardActivity only (NO NEW PAGE)
Must use same control architecture as Pump 1 and Pump 2
🎨 UI DESIGN RULE
Keep consistent card layout:
Same toggle switch style
Same spacing
Same animation behavior
⚠️ SAFETY
Default state = OFF
Must handle Firebase offline mode gracefully
Must retry connection if write fails
📊 FINAL DASHBOARD CONTROL BLOCK
System Controls
[Switch] Pump 1
[Switch] Pump 2
[Switch] Pump 3