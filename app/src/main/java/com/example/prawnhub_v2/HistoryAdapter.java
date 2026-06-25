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
        holder.parameter.setText("Sensor: " + formatParameter(item.parameter));
        holder.timestamp.setText("Timestamp: " + formatTime(item.timestamp));
        holder.value.setText(String.format(Locale.US, "Value: %.1f%s", item.value, unitFor(item.parameter)));
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
        if ("temp".equals(raw) || "temperature".equals(raw)) return "Temperature";
        if ("salinity".equals(raw) || "tds".equals(raw)) return "Salinity";
        if ("turbidity".equals(raw)) return "Turbidity";
        if ("water_level".equals(raw) || "waterLevel".equals(raw)) return "Water Level";
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

    private String unitFor(String parameter) {
        if ("temp".equals(parameter) || "temperature".equals(parameter)) return " C";
        if ("salinity".equals(parameter) || "tds".equals(parameter)) return " ppt";
        if ("turbidity".equals(parameter)) return " NTU";
        if ("water_level".equals(parameter) || "waterLevel".equals(parameter)) return " ft";
        return "";
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
