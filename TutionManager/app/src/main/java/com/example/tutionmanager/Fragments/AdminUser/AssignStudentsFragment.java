package com.example.tutionmanager.Fragments.AdminUser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutionmanager.Adapters.ClassSelectionAdapter;
import com.example.tutionmanager.Adapters.StudentSelectionAdapter;
import com.example.tutionmanager.Models.ClassItem;
import com.example.tutionmanager.Models.StudentItem;
import com.example.tutionmanager.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignStudentsFragment extends Fragment {

    private RecyclerView classRecyclerView, studentsRecyclerView;
    private ClassSelectionAdapter classAdapter;
    private StudentSelectionAdapter studentAdapter;
    private List<ClassItem> classList = new ArrayList<>();
    private List<StudentItem> studentList = new ArrayList<>();
    private List<String> selectedStudentIds = new ArrayList<>();
    private String selectedClassId = "";
    private Button assignButton, clearSelectionButton;
    private EditText searchStudentsEditText;
    private TextView assignButtonText;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assign_students, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        classRecyclerView = view.findViewById(R.id.classRecyclerView);
        studentsRecyclerView = view.findViewById(R.id.studentsRecyclerView);
        assignButton = view.findViewById(R.id.assignButton);
        clearSelectionButton = view.findViewById(R.id.clearSelectionButton);
        searchStudentsEditText = view.findViewById(R.id.searchStudentsEditText);
        assignButtonText = assignButton; // The button text will show the selected class

        // Setup adapters
        setupClassRecyclerView();
        setupStudentsRecyclerView();

        // Load data
        loadClasses();
        loadStudents();

        // Set click listeners
        assignButton.setOnClickListener(v -> assignStudentsToClass());
        clearSelectionButton.setOnClickListener(v -> clearSelections());

        return view;
    }

    private void setupClassRecyclerView() {
        classAdapter = new ClassSelectionAdapter(classList, new ClassSelectionAdapter.OnClassSelectedListener() {
            @Override
            public void onClassSelected(String classId, String className) {
                selectedClassId = classId;
                assignButtonText.setText("Assign to " + className);
            }
        });
        classRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classRecyclerView.setAdapter(classAdapter);
    }

    private void setupStudentsRecyclerView() {
        studentAdapter = new StudentSelectionAdapter(studentList, new StudentSelectionAdapter.OnStudentSelectionChanged() {
            @Override
            public void onSelectionChanged(List<String> selectedIds) {
                selectedStudentIds = selectedIds;
            }
        });
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentsRecyclerView.setAdapter(studentAdapter);
    }

    private void loadClasses() {
        db.collection("classes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        classList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ClassItem classItem = new ClassItem(
                                    document.getId(),
                                    document.getString("className"),
                                    document.getString("teacherName"),
                                    document.getLong("studentCount") != null ? document.getLong("studentCount").intValue() : 0,
                                    document.getString("schedule")
                            );
                            classList.add(classItem);
                        }
                        classAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadStudents() {
        db.collection("users")
                .whereEqualTo("userType", "student")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        studentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            StudentItem studentItem = new StudentItem(
                                    document.getId(),
                                    document.getString("firstName") + " " + document.getString("lastName"),
                                    document.getString("class")
                            );
                            studentList.add(studentItem);
                        }
                        studentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load students", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void assignStudentsToClass() {
        if (selectedClassId.isEmpty()) {
            Toast.makeText(getContext(), "Please select a class", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedStudentIds.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one student", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the class document with the new students
        db.collection("classes").document(selectedClassId)
                .update("students", selectedStudentIds)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Students assigned successfully", Toast.LENGTH_SHORT).show();
                    clearSelections();
                    loadClasses(); // Refresh class list to update student counts
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to assign students: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearSelections() {
        selectedClassId = "";
        selectedStudentIds.clear();
        assignButtonText.setText("Assign to class");
        classAdapter.clearSelection();
        studentAdapter.clearSelections();
    }
}