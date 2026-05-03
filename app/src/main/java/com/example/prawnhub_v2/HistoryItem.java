package com.example.prawnhub_v2;

public class HistoryItem {
    public final String timestamp;
    public final String parameter;
    public final float value;

    public HistoryItem(String timestamp, String parameter, float value) {
        this.timestamp = timestamp;
        this.parameter = parameter;
        this.value = value;
    }
}
