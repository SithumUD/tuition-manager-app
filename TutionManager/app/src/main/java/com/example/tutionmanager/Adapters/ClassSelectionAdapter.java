package com.example.tutionmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutionmanager.Models.ClassItem;
import com.example.tutionmanager.R;

import java.util.List;

public class ClassSelectionAdapter extends RecyclerView.Adapter<ClassSelectionAdapter.ViewHolder> {

    private List<ClassItem> classList;
    private OnClassSelectedListener listener;
    private int lastSelectedPosition = -1;

    public interface OnClassSelectedListener {
        void onClassSelected(String classId, String className);
    }

    public ClassSelectionAdapter(List<ClassItem> classList, OnClassSelectedListener listener) {
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassItem classItem = classList.get(position);
        holder.classRadioButton.setText(classItem.getClassName());
        holder.teacherTextView.setText("Teacher: " + classItem.getTeacherName());
        holder.studentCountTextView.setText(classItem.getStudentCount() + " students");

        holder.classRadioButton.setChecked(position == lastSelectedPosition);
        holder.classRadioButton.setOnClickListener(v -> {
            lastSelectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            listener.onClassSelected(classItem.getId(), classItem.getClassName());
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void clearSelection() {
        lastSelectedPosition = -1;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RadioButton classRadioButton;
        public TextView teacherTextView;
        public TextView studentCountTextView;

        public ViewHolder(View view) {
            super(view);
            classRadioButton = view.findViewById(R.id.classRadioButton);
            teacherTextView = view.findViewById(R.id.teacherTextView);
            studentCountTextView = view.findViewById(R.id.studentCountTextView);
        }
    }
}