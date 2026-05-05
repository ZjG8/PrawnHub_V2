package com.example.prawnhub_v2;

public class HistoryItem {
    public final String timestamp;
    public final String parameter;
    public final float value;
    public final String status;

    public HistoryItem(String timestamp, String parameter, float value) {
        this(timestamp, parameter, value, "Important alert");
    }

    public HistoryItem(String timestamp, String parameter, float value, String status) {
        this.timestamp = timestamp;
        this.parameter = parameter;
        this.value = value;
        this.status = status;
    }
}
