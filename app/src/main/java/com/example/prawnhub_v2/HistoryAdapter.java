package com.example.prawnhub_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<HistoryItem> items = new ArrayList<>();

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
        holder.parameter.setText(formatParameter(item.parameter));
        holder.timestamp.setText(item.timestamp);
        holder.value.setText(String.format("%.2f", item.value));
        holder.status.setText(item.status);
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
