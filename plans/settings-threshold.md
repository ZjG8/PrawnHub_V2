# PrawnHub V2 - System Settings UI Redesign

## Objective

Redesign the Admin System Settings page to use modern slider controls instead of individual text fields for thresholds.

---

## Temperature Threshold

Replace:

* Max Temperature [editable]
* Min Temperature [editable]

With:

```text
Temperature

Min -------------------------------------- Max
 20°C  =================================  35°C
```

Requirements:

* Use ONE Range Slider.
* Left thumb = Minimum Temperature.
* Right thumb = Maximum Temperature.
* Display current values beside the slider.
* Values update live while dragging.
* Save to Firebase.

Firebase:

/thresholds/minTemperature
/thresholds/maxTemperature

---

## Salinity Threshold

Replace:

* Max Salinity [editable]
* Min Salinity [editable]

With:

```text
Salinity

Min -------------------------------------- Max
 5 ppt ================================= 25 ppt
```

Requirements:

* Use ONE Range Slider.
* Left thumb = Minimum Salinity.
* Right thumb = Maximum Salinity.
* Display current values live.
* Save to Firebase.

Firebase:

/thresholds/minSalinity
/thresholds/maxSalinity

---

## Turbidity Threshold

Replace:

* Max Turbidity [editable]

With:

```text
Turbidity Limit

0 ----------------------------- 100
                |
              50 NTU
```

Requirements:

* Single Slider.
* Minimum fixed at 0.
* User adjusts only maximum turbidity limit.
* Show current value above slider.
* Save to Firebase.

Firebase:

/thresholds/maxTurbidity

---

## Overflow Limit

Replace:

* Overflow Limit [editable]

With:

```text
Overflow Limit

0 ----------------------------- 100
                |
              75 cm
```

Requirements:

* Single Slider.
* Minimum fixed at 0.
* User adjusts overflow threshold.
* Show current value above slider.
* Save to Firebase.

Firebase:

/thresholds/overflowLimit

---

## Layout

```text
+--------------------------------------------------+
| Back                              System Settings|
+--------------------------------------------------+

Temperature
[ Range Slider ]

Min: 20°C                     Max: 35°C

----------------------------------------------------

Salinity
[ Range Slider ]

Min: 5 ppt                    Max: 25 ppt

----------------------------------------------------

Turbidity Limit
[ Single Slider ]

Current: 50 NTU

----------------------------------------------------

Overflow Limit
[ Single Slider ]

Current: 75 cm

----------------------------------------------------

Feeding Automation
Feed Time
Feeding Interval

----------------------------------------------------

Notifications
Sound Alert
Vibrate
Push Notifications

----------------------------------------------------

[ Save Settings ]
[ Logout ]

+--------------------------------------------------+
```

---

## Technical Requirements

Use:

* Material Design 3
* Material RangeSlider for Temperature
* Material RangeSlider for Salinity
* Material Slider for Turbidity
* Material Slider for Overflow Limit

Persist values in Firebase Realtime Database.

Load saved values on Activity start.

Changes should update UI immediately while dragging.

Save Settings button writes all values to Firebase in a single update operation.
