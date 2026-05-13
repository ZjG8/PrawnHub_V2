package com.example.prawnhub_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<HistoryItem> items = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.US);

    public void submitList(List<HistoryItem> nextItems) {
        items = new ArrayList<>(nextItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        holder.parameter.setText(formatStatusIcon(item.status) + " " + formatTime(item.timestamp));
        holder.timestamp.setText(formatMessage(item.parameter, item.value, item.status));
        holder.value.setText("Parameter: " + formatParameter(item.parameter));
        holder.status.setText("Status: " + item.status);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatParameter(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "Unknown";
        }
        return raw.replace("_", " ");
    }

    private String formatTime(String timestamp) {
        try {
            long raw = Long.parseLong(timestamp);
            long millis = raw < 100000000000L ? raw * 1000L : raw;
            return timeFormat.format(new Date(millis));
        } catch (NumberFormatException ignored) {
            return timestamp;
        }
    }

    private String formatStatusIcon(String status) {
        if (status == null) {
            return "!";
        }
        return "normal".equalsIgnoreCase(status) || "stable".equalsIgnoreCase(status) ? "LOG" : "ALERT";
    }

    private String formatMessage(String parameter, float value, String status) {
        String unit = unitFor(parameter);
        if ("temp".equals(parameter) || "temperature".equals(parameter)) {
            return String.format(Locale.US, "Temperature recorded: %.1f%s", value, unit);
        }
        if ("salinity".equals(parameter)) {
            return String.format(Locale.US, "Salinity detected: %.1f%s", value, unit);
        }
        if ("turbidity".equals(parameter)) {
            String label = isWarning(status) ? "Turbidity spike detected" : "Turbidity recorded";
            return String.format(Locale.US, "%s: %.1f%s", label, value, unit);
        }
        if ("water_level".equals(parameter) || "waterLevel".equals(parameter)) {
            return String.format(Locale.US, "Water Level: %.1f%s", value, unit);
        }
        return String.format(Locale.US, "%s recorded: %.1f%s", formatParameter(parameter), value, unit);
    }

    private String unitFor(String parameter) {
        if ("temp".equals(parameter) || "temperature".equals(parameter)) return " C";
        if ("salinity".equals(parameter)) return " ppm";
        if ("turbidity".equals(parameter)) return " NTU";
        if ("water_level".equals(parameter) || "waterLevel".equals(parameter)) return " cm";
        return "";
    }

    private boolean isWarning(String status) {
        return status != null && !"normal".equalsIgnoreCase(status) && !"stable".equalsIgnoreCase(status);
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        final TextView parameter;
        final TextView timestamp;
        final TextView value;
        final TextView status;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            parameter = itemView.findViewById(R.id.historyParam);
            timestamp = itemView.findViewById(R.id.historyTimestamp);
            value = itemView.findViewById(R.id.historyValue);
            status = itemView.findViewById(R.id.historyStatus);
        }
    }
}
