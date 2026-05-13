# PrawnHub UI Full Redesign Prompt (Codex Instruction File)

## Goal
Redesign and refactor the entire PrawnHub Android system (Farmer + Admin) UI.

Focus
- Maximum UI space utilization
- Clean IoT dashboard design
- Firebase real-time integration
- Role-based system (Farmer vs Admin)
- Minimal clutter UI
- Modern aquaculture monitoring interface

---

# ==================================================
# 1. FARMER DASHBOARD
# ==================================================

## Activity
DashboardActivity

## Layout
activity_dashboard.xml

---

## REMOVE
- Oxygen section
- pH section
- Aerator control

---

## DESIGN

```text
+--------------------------------------------------+
 [FARMER LOGO]                  Dashboard  Logo   
+--------------------------------------------------+
 Pond Status                                      
 Healthy  Warning  Danger                       
+--------------------------------------------------+
 Alert Banner (Firebase real-time alerts)        
+--------------------------------------------------+
 Water Temperature                                
                -- °C                             
                Stable                            
+--------------------------------------------------+
 Salinity                   Turbidity            
 -- ppm                     -- NTU               
+---------------------------+----------------------+
 Water Level                                      
 -- cm                                            
+--------------------------------------------------+
 Growth Prediction                                
 Health status + recommendation                  
+--------------------------------------------------+
 Recent Alerts (Firebase only)                   
+--------------------------------------------------+
 Controls                                         
 [Switch] Pump                                   
 [Switch] Filter                                 
 [Button] Feed Now                               
+--------------------------------------------------+
 Dashboard  History  Growth  Settings          
+--------------------------------------------------+
==================================================
2. FARMER HISTORY
==================================================
Activity

HistoryActivity

Layout

activity_history.xml

CHANGES

REMOVE

Important Events section
TodayWeekMonth spinner

REPLACE

Start Date + Time Picker
End Date + Time Picker
DESIGN
+--------------------------------------------------+
 (Back)                                           
 History                                          
+--------------------------------------------------+
 Start Date     [Date + Time Picker]             
 End Date       [Date + Time Picker]             
+--------------------------------------------------+
 Sensor Filter                                    
 Temperature  Salinity  Turbidity  Water Level 
+--------------------------------------------------+
 Line Chart (Firebase filtered data)             
+--------------------------------------------------+
 Event List (Important Only)                      
 Parameter  Timestamp  Value  Status           
+--------------------------------------------------+
 Dashboard  History  Growth  Settings          
+--------------------------------------------------+
FIREBASE REQUIREMENT
Timestamp-based filtering
Real-time + historical support
Query by date range + sensor type
==================================================
3. FARMER GROWTH
==================================================
Activity

GrowthActivity

Layout

activity_growth.xml

DESIGN
+--------------------------------------------------+
 Growth Analytics                                 
+--------------------------------------------------+
 Health Score                                     
 [Circular Progress Bar - Firebase Driven]        
 Example 78% Healthy                             
+--------------------------------------------------+
 Weekly Growth Trend                              
 [Line Graph - Firebase Data]                     
+--------------------------------------------------+
 Status Badge                                     
 Good  Warning  Critical                        
+--------------------------------------------------+
 Recommendations                                  
 AI-based or rule-based suggestions               
+--------------------------------------------------+
 Dashboard  History  Growth  Settings          
+--------------------------------------------------+
==================================================
4. FARMER SETTINGS
==================================================
Activity

SettingsActivity

Layout

activity_settings.xml

CHANGES

REMOVE

Account section
Oxygen fields
pH fields

REPLACE

user@email.com
 → Farmer
DESIGN
+--------------------------------------------------+
 Monitoring Settings                  Logo         
+--------------------------------------------------+
 Farmer                                           
+--------------------------------------------------+
 Pond Settings                                    
 Max Temp         [read-only]                    
 Min Temp         [read-only]                    
 Max Salinity     [read-only]                    
 Min Salinity     [read-only]                    
 Max Turbidity    [read-only]                    
 Overflow Limit   [read-only]                    
+--------------------------------------------------+
 Feeding Automation                               
 Feed Time        [read-only]                    
 Interval         [read-only]                    
 Auto-feed        [disabled switch]              
+--------------------------------------------------+
 Notifications                                    
 Sound Alert      [disabled]                     
 Vibrate          [disabled]                     
 Push Alerts      [disabled]                     
+--------------------------------------------------+
 [About PrawnHub]                                 
 [Back to Roles]                                  
+--------------------------------------------------+
 Dashboard  History  Growth  Settings          
+--------------------------------------------------+
==================================================
5. ADMIN DASHBOARD SYSTEM
==================================================
Activity

AdminDashboardActivity

DESIGN
+--------------------------------------------------+
 ADMIN DASHBOARD                      Logo        
+--------------------------------------------------+
 System Status                                    
 Firebase Sync ONLINE  OFFLINE                  
 ESP32 Status CONNECTED  DISCONNECTED           
+--------------------------------------------------+
 Quick Actions                                    
 [Manage Users]                                   
 [Farm Reports]                                   
 [System Settings]                                
 [Diagnostics]                                    
+--------------------------------------------------+
 Activity Feed (Real-time logs)                   
+--------------------------------------------------+
 Logout                                           
+--------------------------------------------------+
==================================================
6. ADMIN FARM REPORTS
==================================================
Activity

ReportsActivity

CHANGES

REMOVE

Oxygen
pH
DESIGN
+--------------------------------------------------+
 (Back) Farm Reports              Logo            
+--------------------------------------------------+
 Report Range                                     
 Start Date        [Date Picker]                 
 End Date          [Date Picker]                 
+--------------------------------------------------+
 Report Preview                                   
 Temperature, Salinity, Turbidity, Water Level   
 Alerts, Growth Analytics                         
+--------------------------------------------------+
 [Export PDF]                                     
 [Export Excel]                                   
+--------------------------------------------------+
==================================================
7. ADMIN SYSTEM SETTINGS
==================================================
Activity

SettingsActivity

CHANGES

REMOVE

Min Oxygen
Max pH
Min pH
DESIGN
+--------------------------------------------------+
 (Back) System Settings            Logo           
+--------------------------------------------------+
 Account                                          
 admin@email.com                                  
+--------------------------------------------------+
 Pond Settings                                    
 Max Temp         [editable]                     
 Min Temp         [editable]                     
 Max Salinity     [editable]                     
 Min Salinity     [editable]                     
 Max Turbidity    [editable]                     
 Overflow Limit   [editable]                     
+--------------------------------------------------+
 Feeding Automation                               
 Feed Time        [time picker]                  
 Interval         [editable]                     
 Auto-feed        [switch]                       
+--------------------------------------------------+
 Notifications                                    
 Sound Alert      [switch]                       
 Vibrate          [switch]                       
 Push Alerts      [switch]                       
+--------------------------------------------------+
 [About PrawnHub]                                 
 [Save Settings]                                  
 [Logout]                                         
+--------------------------------------------------+
==================================================
8. GLOBAL UI RULES
==================================================
Layout Rules
Maximize screen space usage
Card-based design only
Avoid empty headers
Group related sensor data
Use grid layouts for sensors
Design Style
Soft aquatic green theme
Rounded cards (12–16dp)
Clean typography hierarchy
Minimal shadows
Real-time Firebase updates
Interaction Rules
Back button always returns previous screen
No redundant navigation
All sensor data must auto-update
History supports datetime filtering
Admin controls override farmer view
END OF FILE

If you want next upgrade, I can convert this into
- contentReference[oaicite0]{index=0}
- contentReference[oaicite1]{index=1}
- or contentReference[oaicite2]{index=2} (very useful for your capstone defense)