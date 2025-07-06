package com.example.tutionmanager.Fragments.AdminUser;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.example.tutionmanager.Adapters.ClassAdapter;
import com.example.tutionmanager.Models.ClassItem;
import com.example.tutionmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ClassManagementFragment extends Fragment {

    private FloatingActionButton fabAddClass;
    private RecyclerView classesRecyclerView;
    private ClassAdapter classAdapter;
    private List<ClassItem> classList;
    private List<ClassItem> filteredClassList; // For search results
    private FirebaseFirestore db;
    private EditText searchEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_management, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        fabAddClass = view.findViewById(R.id.fabAddClass);
        classesRecyclerView = view.findViewById(R.id.classesRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);

        // Setup RecyclerView
        setupRecyclerView();

        // Load classes from Firestore
        loadClassesFromFirestore();

        // Setup search functionality
        setupSearch();

        // FAB click listener
        fabAddClass.setOnClickListener(v -> {
            CreateClassFragment createClassFragment = new CreateClassFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, createClassFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void setupRecyclerView() {
        classList = new ArrayList<>();
        filteredClassList = new ArrayList<>();
        classAdapter = new ClassAdapter(filteredClassList);
        classesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classesRecyclerView.setAdapter(classAdapter);
    }

    private void loadClassesFromFirestore() {
        db.collection("classes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        classList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String className = document.getString("className");
                            String grade = document.getString("grade");
                            String subject = document.getString("subject");
                            String teacherName = document.getString("teacherName");
                            String time = document.getString("time");

                            List<String> daysList = (List<String>) document.get("days");
                            String days = formatDays(daysList);

                            List<String> studentsList = (List<String>) document.get("students");
                            int studentCount = (studentsList != null) ? studentsList.size() : 0;

                            String schedule = days + " - " + time;
                            String classTitle = grade + " - " + subject;

                            ClassItem classItem = new ClassItem(
                                    id,
                                    classTitle,
                                    teacherName,
                                    studentCount,
                                    schedule
                            );
                            classList.add(classItem);
                        }
                        // Initially show all classes
                        filteredClassList.clear();
                        filteredClassList.addAll(classList);
                        classAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error loading classes: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClasses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterClasses(String searchText) {
        filteredClassList.clear();

        if (searchText.isEmpty()) {
            filteredClassList.addAll(classList);
        } else {
            searchText = searchText.toLowerCase();
            for (ClassItem classItem : classList) {
                if (classItem.getClassName().toLowerCase().contains(searchText) ||
                        classItem.getTeacherName().toLowerCase().contains(searchText) ||
                        classItem.getSchedule().toLowerCase().contains(searchText)) {
                    filteredClassList.add(classItem);
                }
            }
        }
        classAdapter.notifyDataSetChanged();
    }

    private String formatDays(List<String> daysList) {
        if (daysList == null || daysList.isEmpty()) {
            return "";
        }

        StringBuilder daysBuilder = new StringBuilder();
        for (int i = 0; i < daysList.size(); i++) {
            String day = daysList.get(i).substring(0, 3);
            daysBuilder.append(day);
            if (i < daysList.size() - 1) {
                daysBuilder.append(", ");
            }
        }
        return daysBuilder.toString();
    }
}