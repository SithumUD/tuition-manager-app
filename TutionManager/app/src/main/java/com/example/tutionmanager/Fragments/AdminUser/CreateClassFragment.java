package com.example.tutionmanager.Fragments.AdminUser;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tutionmanager.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateClassFragment extends Fragment {

    private EditText classNameEditText, gradeEditText, subjectEditText;
    private Spinner teacherSpinner;
    private CheckBox mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox, thursdayCheckBox,
            fridayCheckBox, saturdayCheckBox, sundayCheckBox;
    private LinearLayout timeSelector;
    private TextView timeTextView;
    private Button createButton;
    private FirebaseFirestore db;
    private List<String> teacherNames = new ArrayList<>();
    private List<String> teacherIds = new ArrayList<>();
    private String selectedTime = "";
    private String selectedTeacherId = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_class, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews(view);

        // Load teachers from Firestore
        loadTeachers();

        // Set up time selector
        setupTimeSelector();

        // Set up create button
        setupCreateButton();

        // Cancel button
        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void initializeViews(View view) {
        classNameEditText = view.findViewById(R.id.classNameEditText);
        gradeEditText = view.findViewById(R.id.gradeEditText);
        subjectEditText = view.findViewById(R.id.subjectEditText);
        teacherSpinner = view.findViewById(R.id.teacherspinner);
        timeSelector = view.findViewById(R.id.timeselector);
        timeTextView = view.findViewById(R.id.timeTextView);
        createButton = view.findViewById(R.id.createButton);

        // Initialize checkboxes
        mondayCheckBox = view.findViewById(R.id.mondayCheckBox);
        tuesdayCheckBox = view.findViewById(R.id.tuesdayCheckBox);
        wednesdayCheckBox = view.findViewById(R.id.wednesdayCheckBox);
        thursdayCheckBox = view.findViewById(R.id.thursdayCheckBox);
        fridayCheckBox = view.findViewById(R.id.fridayCheckBox);
        saturdayCheckBox = view.findViewById(R.id.saturdayCheckBox);
        sundayCheckBox = view.findViewById(R.id.sundayCheckBox);
    }

    private void loadTeachers() {
        db.collection("users")
                .whereEqualTo("userType", "teacher")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        teacherNames.clear();
                        teacherIds.clear();

                        // Add default option
                        teacherNames.add("Select Teacher");
                        teacherIds.add("");

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");
                            String teacherId = document.getId();

                            if (firstName != null && lastName != null) {
                                teacherNames.add(firstName + " " + lastName);
                                teacherIds.add(teacherId);
                            }
                        }

                        // Set up spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                teacherNames
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        teacherSpinner.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "Failed to load teachers", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupTimeSelector() {
        timeSelector.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view, hourOfDay, minute1) -> {
                        selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                        timeTextView.setText(selectedTime);
                        timeTextView.setTextColor(getResources().getColor(android.R.color.black));
                    },
                    hour,
                    minute,
                    true
            );
            timePickerDialog.show();
        });
    }

    private void setupCreateButton() {
        createButton.setOnClickListener(v -> {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }

            // Get selected days
            List<String> selectedDays = getSelectedDays();

            // Create class data
            Map<String, Object> classData = new HashMap<>();
            classData.put("className", classNameEditText.getText().toString().trim());
            classData.put("grade", gradeEditText.getText().toString().trim());
            classData.put("subject", subjectEditText.getText().toString().trim());
            classData.put("teacherId", selectedTeacherId);
            classData.put("teacherName", teacherSpinner.getSelectedItem().toString());
            classData.put("time", selectedTime);
            classData.put("days", selectedDays);
            classData.put("createdAt", System.currentTimeMillis());

            // Save to Firestore
            db.collection("classes")
                    .add(classData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Class created successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to create class: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private boolean validateInputs() {
        String className = classNameEditText.getText().toString().trim();
        String grade = gradeEditText.getText().toString().trim();
        String subject = subjectEditText.getText().toString().trim();

        if (className.isEmpty()) {
            classNameEditText.setError("Class name is required");
            classNameEditText.requestFocus();
            return false;
        }

        if (grade.isEmpty()) {
            gradeEditText.setError("Grade is required");
            gradeEditText.requestFocus();
            return false;
        }

        if (subject.isEmpty()) {
            subjectEditText.setError("Subject is required");
            subjectEditText.requestFocus();
            return false;
        }

        if (teacherSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select a teacher", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            selectedTeacherId = teacherIds.get(teacherSpinner.getSelectedItemPosition());
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(getContext(), "Please select a time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (getSelectedDays().isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one day", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private List<String> getSelectedDays() {
        List<String> selectedDays = new ArrayList<>();

        if (mondayCheckBox.isChecked()) selectedDays.add("Monday");
        if (tuesdayCheckBox.isChecked()) selectedDays.add("Tuesday");
        if (wednesdayCheckBox.isChecked()) selectedDays.add("Wednesday");
        if (thursdayCheckBox.isChecked()) selectedDays.add("Thursday");
        if (fridayCheckBox.isChecked()) selectedDays.add("Friday");
        if (saturdayCheckBox.isChecked()) selectedDays.add("Saturday");
        if (sundayCheckBox.isChecked()) selectedDays.add("Sunday");

        return selectedDays;
    }
}