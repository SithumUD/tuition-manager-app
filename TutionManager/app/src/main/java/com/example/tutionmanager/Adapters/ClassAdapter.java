package com.example.tutionmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutionmanager.Models.ClassItem;
import com.example.tutionmanager.R;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private final List<ClassItem> classList;

    public ClassAdapter(List<ClassItem> classList) {
        this.classList = classList;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassItem classItem = classList.get(position);
        holder.className.setText(classItem.getClassName());
        holder.teacherName.setText("Teacher: " + classItem.getTeacherName());

        if (classItem.getStudentCount() > 0) {
            holder.studentCount.setText(classItem.getStudentCount() + " Students");
            holder.studentCount.setVisibility(View.VISIBLE);
        } else {
            holder.studentCount.setVisibility(View.GONE);
        }

        if (!classItem.getSchedule().isEmpty()) {
            holder.schedule.setText(classItem.getSchedule());
            holder.schedule.setVisibility(View.VISIBLE);
        } else {
            holder.schedule.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        public final TextView className;
        public final TextView teacherName;
        public final TextView studentCount;
        public final TextView schedule;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.className);
            teacherName = itemView.findViewById(R.id.teacherName);
            studentCount = itemView.findViewById(R.id.studentCount);
            schedule = itemView.findViewById(R.id.schedule);
        }
    }
}