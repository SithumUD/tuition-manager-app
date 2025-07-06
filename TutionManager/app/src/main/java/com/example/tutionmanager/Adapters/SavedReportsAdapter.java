package com.example.tutionmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutionmanager.Models.SavedReport;
import com.example.tutionmanager.R;

import java.util.List;

public class SavedReportsAdapter extends RecyclerView.Adapter<SavedReportsAdapter.ViewHolder> {

    private final List<SavedReport> savedReports;

    public SavedReportsAdapter(List<SavedReport> savedReports) {
        this.savedReports = savedReports;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedReport report = savedReports.get(position);
        holder.reportName.setText(report.getName());
        holder.reportDate.setText("Generated on " + report.getDate());
    }

    @Override
    public int getItemCount() {
        return savedReports.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reportName;
        public TextView reportDate;

        public ViewHolder(View view) {
            super(view);
            reportName = view.findViewById(R.id.reportName);
            reportDate = view.findViewById(R.id.reportDate);
        }
    }
}