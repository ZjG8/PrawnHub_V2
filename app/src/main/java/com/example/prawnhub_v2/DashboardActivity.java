package com.example.prawnhub_v2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Locale;

/** Farmer dashboard: sensor monitoring and deliberately manual system controls. */
public class DashboardActivity extends BaseNavActivity {
    private DatabaseReference database, controlRef, feederRef;
    private TextView pondStatusText, tempText, tempStatusText, salText, salStatusText, turbText, turbStatusText, waterText, waterStatusText, userSummaryText, settingsPreviewText;
    private LinearLayout adminSection, tempCard, salCard, turbCard, waterCard;
    private Switch pump1Switch, pump2Switch, pump3Switch, pump4Switch;
    private TextView feedStatusText, lastFeedText, feedAmountText, feedCountText;
    private Button feedNowButton;
    private float minTemp = 28, maxTemp = 32, minSal = 15, maxSal = 25, minTurb = 0, maxTurb = 45, overflowLimit = 5;
    private boolean updating, feederRunning;
    private SensorAlertDebouncer.State temperatureAlertState = SensorAlertDebouncer.State.initial();
    private SensorAlertDebouncer.State salinityAlertState = SensorAlertDebouncer.State.initial();
    private SensorAlertDebouncer.State turbidityAlertState = SensorAlertDebouncer.State.initial();
    private SensorAlertDebouncer.State waterAlertState = SensorAlertDebouncer.State.initial();

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_dashboard); setupBottomNav();
        database = FirebaseDatabase.getInstance().getReference(); controlRef = database.child("ShrimpHub").child("control"); feederRef=database.child("ShrimpHub").child("feeder"); bind();
        adminSection.setVisibility("admin".equalsIgnoreCase(SessionStore.getRole(this)) ? android.view.View.VISIBLE : android.view.View.GONE);
        requestNotifications(); listenSettings(); listenSensors(); listenControls(); listenFeeder(); listenAdmin(); bindControls();
    }
    private void bind() {
        pondStatusText=f(R.id.pondStatusText); tempText=f(R.id.tempText); tempStatusText=f(R.id.tempStatusText); salText=f(R.id.salText); salStatusText=f(R.id.salStatusText); turbText=f(R.id.turbText); turbStatusText=f(R.id.turbStatusText); waterText=f(R.id.waterText); waterStatusText=f(R.id.waterStatusText); userSummaryText=f(R.id.userSummaryText); settingsPreviewText=f(R.id.settingsPreviewText);
        adminSection=findViewById(R.id.adminSection); tempCard=findViewById(R.id.tempCard); salCard=findViewById(R.id.salCard); turbCard=findViewById(R.id.turbCard); waterCard=findViewById(R.id.waterCard); pump1Switch=findViewById(R.id.pump1Switch); pump2Switch=findViewById(R.id.pump2Switch); pump3Switch=findViewById(R.id.pump3Switch); pump4Switch=findViewById(R.id.pump4Switch); feedStatusText=f(R.id.feedStatusText); lastFeedText=f(R.id.lastFeedText); feedAmountText=f(R.id.feedAmountText); feedCountText=f(R.id.feedCountText); feedNowButton=findViewById(R.id.feedNowButton);
    }
    private TextView f(int id) { return findViewById(id); }
    private void bindControls() {
        CompoundButton.OnCheckedChangeListener listener=(v,on)-> { if (!updating) controlRef.child(pumpKeyFor(v.getId())).setValue(on).addOnFailureListener(e->Toast.makeText(this,"Control update failed.",Toast.LENGTH_SHORT).show()); };
        pump1Switch.setOnCheckedChangeListener(listener); pump2Switch.setOnCheckedChangeListener(listener); pump3Switch.setOnCheckedChangeListener(listener); pump4Switch.setOnCheckedChangeListener(listener);
        feedNowButton.setOnClickListener(v -> { if(!feederRunning) feederRef.child("feedNow").setValue(true).addOnFailureListener(e -> Toast.makeText(this,"Feed request failed.",Toast.LENGTH_SHORT).show()); });
        ((Button)findViewById(R.id.manageUsersButton)).setOnClickListener(v->startActivity(new Intent(this,ManageUsersActivity.class)));
        ((Button)findViewById(R.id.exportReportButton)).setOnClickListener(v->startActivity(new Intent(this,ReportsActivity.class)));
    }
    private String pumpKeyFor(int switchId) {
        if (switchId == R.id.pump1Switch) return PumpControlMapper.firebaseKeyFor(1);
        if (switchId == R.id.pump2Switch) return PumpControlMapper.firebaseKeyFor(2);
        if (switchId == R.id.pump3Switch) return PumpControlMapper.firebaseKeyFor(3);
        if (switchId == R.id.pump4Switch) return PumpControlMapper.firebaseKeyFor(4);
        throw new IllegalArgumentException("Unsupported pump switch: " + switchId);
    }
    private void listenSettings() { database.child("settings").addValueEventListener(new ValueEventListener() {
        @Override public void onDataChange(@NonNull DataSnapshot s) { minTemp=n(s,"min_temp",28); maxTemp=n(s,"max_temp",32); minSal=n(s,"min_sal",15); maxSal=n(s,"max_sal",25); minTurb=n(s,"min_turb",0); maxTurb=n(s,"max_turb",45); overflowLimit=n(s,"overflow_limit",5); NotificationSettings.syncFromSettings(DashboardActivity.this,s); settingsPreviewText.setText(String.format(Locale.US,"Temp %.1f–%.1f C | Salinity %.0f–%.0f ppt | Turbidity %.1f–%.1f",minTemp,maxTemp,minSal,maxSal,minTurb,maxTurb)); }
        @Override public void onCancelled(@NonNull DatabaseError e) { Toast.makeText(DashboardActivity.this,"Settings unavailable.",Toast.LENGTH_SHORT).show(); }
    }); }
    private void listenSensors() { database.child("ShrimpHub").addValueEventListener(new ValueEventListener() {
        @Override public void onDataChange(@NonNull DataSnapshot s) { if(s.hasChild("temperature")||s.hasChild("tds")||s.hasChild("turbidity")||s.hasChild("waterLevel")) render(n(s,"temperature",0),n(s,"tds",0),n(s,"turbidity",0),n(s,"waterLevel",0)); }
        @Override public void onCancelled(@NonNull DatabaseError e) { Toast.makeText(DashboardActivity.this,"Live sensor data unavailable.",Toast.LENGTH_SHORT).show(); }
    }); }
    private void render(float temp,float salinity,float turb,float waterCm) {
        tempText.setText(String.format(Locale.US,"%.1f C",temp)); salText.setText(String.format(Locale.US,"%.1f ppt",salinity)); turbText.setText(String.format(Locale.US,"%.1f",turb)); float waterFt=waterCm/30.48f; waterText.setText(String.format(Locale.US,"%.1f ft",waterFt));
        PondConditionEvaluator.Result conditions=PondConditionEvaluator.evaluate(temp,salinity,turb,waterCm,minTemp,maxTemp,minSal,maxSal,minTurb,maxTurb,overflowLimit); boolean t=conditions.temperature!=PondConditionEvaluator.Status.NORMAL, s=conditions.tds!=PondConditionEvaluator.Status.NORMAL, u=conditions.turbidity!=PondConditionEvaluator.Status.NORMAL, w=conditions.water!=PondConditionEvaluator.Status.NORMAL; card(tempCard,tempStatusText,conditionLabel(conditions.temperature,"Stable"),t,R.color.alert_red); card(salCard,salStatusText,conditionLabel(conditions.tds,"Normal"),s,R.color.alert_yellow); card(turbCard,turbStatusText,conditionLabel(conditions.turbidity,"Normal"),u,R.color.alert_red); card(waterCard,waterStatusText,conditionLabel(conditions.water,"Normal"),w,R.color.alert_yellow);
        int issues=(t?1:0)+(s?1:0)+(u?1:0)+(w?1:0); pondStatusText.setText(issues==0?"Healthy":issues<3?"Monitor conditions":"Action needed"); pondStatusText.setTextColor(ContextCompat.getColor(this,issues==0?R.color.status_green:issues<3?R.color.alert_yellow:R.color.alert_red));
        temperatureAlertState = notifyAfterConfirmation(temperatureAlertState, t, "Temperature is outside the safe range.", 1003);
        salinityAlertState = notifyAfterConfirmation(salinityAlertState, s, "Salinity is outside the configured ppt range.", 1004);
        turbidityAlertState = notifyAfterConfirmation(turbidityAlertState, u, "Turbidity is outside the configured range.", 1005);
        waterAlertState = notifyAfterConfirmation(waterAlertState, w, "Water level is outside the safe range.", 1002);
    }
    private SensorAlertDebouncer.State notifyAfterConfirmation(SensorAlertDebouncer.State state, boolean outOfRange, String message, int notificationId) { SensorAlertDebouncer.Decision decision=SensorAlertDebouncer.evaluate(state,outOfRange,2); if(decision.shouldAlert) NotificationHelper.showAlert(this,"PrawnHub Alert",message,notificationId); return decision.state; }
    private void card(LinearLayout c,TextView label,String text,boolean bad,int color) { c.setBackgroundResource(bad?R.drawable.card_alert:R.drawable.card_background); label.setText(text); label.setTextColor(ContextCompat.getColor(this,bad?color:R.color.status_green)); }
    private String conditionLabel(PondConditionEvaluator.Status status,String normal) { return status==PondConditionEvaluator.Status.NORMAL?normal:status==PondConditionEvaluator.Status.CRITICAL?"Critical":status==PondConditionEvaluator.Status.WARNING?"Warning":"Unavailable"; }
    private void listenControls() { controlRef.addValueEventListener(new ValueEventListener() { @Override public void onDataChange(@NonNull DataSnapshot s) { updating=true; try { pump1Switch.setChecked(Boolean.TRUE.equals(s.child(PumpControlMapper.firebaseKeyFor(1)).getValue(Boolean.class))); pump2Switch.setChecked(Boolean.TRUE.equals(s.child(PumpControlMapper.firebaseKeyFor(2)).getValue(Boolean.class))); pump3Switch.setChecked(Boolean.TRUE.equals(s.child(PumpControlMapper.firebaseKeyFor(3)).getValue(Boolean.class))); pump4Switch.setChecked(Boolean.TRUE.equals(s.child(PumpControlMapper.firebaseKeyFor(4)).getValue(Boolean.class))); } finally { updating=false; } } @Override public void onCancelled(@NonNull DatabaseError e) { updating=false; } }); }
    private void listenFeeder() { feederRef.addValueEventListener(new ValueEventListener() { @Override public void onDataChange(@NonNull DataSnapshot s) { feederRunning=Boolean.TRUE.equals(s.child("feeding").getValue(Boolean.class)); feedStatusText.setText(feederRunning?"Status: Feeding":"Status: Ready"); lastFeedText.setText("Last feed: "+String.valueOf(s.child("lastFeedTime").getValue()==null?"--":s.child("lastFeedTime").getValue())); feedAmountText.setText(String.format(Locale.US,"Feed amount: %.0f g",n(s,"feedAmount",0))); feedCountText.setText(String.format(Locale.US,"Feed count: %.0f",n(s,"feedCount",0))); feedNowButton.setEnabled(!feederRunning); } @Override public void onCancelled(@NonNull DatabaseError e) { feedStatusText.setText("Status: Unavailable"); feedNowButton.setEnabled(false); } }); }
    private void listenAdmin() { if(!"admin".equalsIgnoreCase(SessionStore.getRole(this))) return; database.child("users").addValueEventListener(new ValueEventListener() { @Override public void onDataChange(@NonNull DataSnapshot s) { userSummaryText.setText(String.format(Locale.US,"%d registered users",s.getChildrenCount())); } @Override public void onCancelled(@NonNull DatabaseError e) { userSummaryText.setText("User summary unavailable"); } }); }
    private float n(DataSnapshot s,String key,float fallback) { Object v=s.child(key).getValue(); if(v instanceof Number)return ((Number)v).floatValue(); try{return v==null?fallback:Float.parseFloat(String.valueOf(v));}catch(NumberFormatException e){return fallback;} }
    private void requestNotifications() { if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU&&ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},44); }
}
