package com.example.tutionmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutionmanager.Models.StudentItem;
import com.example.tutionmanager.R;

import java.util.ArrayList;
import java.util.List;

public class StudentSelectionAdapter extends RecyclerView.Adapter<StudentSelectionAdapter.ViewHolder> {

    private List<StudentItem> studentList;
    private OnStudentSelectionChanged listener;
    private List<String> selectedIds = new ArrayList<>();

    public interface OnStudentSelectionChanged {
        void onSelectionChanged(List<String> selectedIds);
    }

    public StudentSelectionAdapter(List<StudentItem> studentList, OnStudentSelectionChanged listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentItem student = studentList.get(position);
        holder.studentCheckBox.setText(student.getName());
        holder.gradeTextView.setText(student.getGrade());

        holder.studentCheckBox.setChecked(selectedIds.contains(student.getId()));
        holder.studentCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !selectedIds.contains(student.getId())) {
                selectedIds.add(student.getId());
            } else if (!isChecked && selectedIds.contains(student.getId())) {
                selectedIds.remove(student.getId());
            }
            listener.onSelectionChanged(selectedIds);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void clearSelections() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox studentCheckBox;
        public TextView gradeTextView;

        public ViewHolder(View view) {
            super(view);
            studentCheckBox = view.findViewById(R.id.studentCheckBox);
            gradeTextView = view.findViewById(R.id.gradeTextView);
        }
    }
}