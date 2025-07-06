package com.example.tutionmanager.Fragments.TeacherUser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tutionmanager.Models.ClassItem;
import com.example.tutionmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TeacherAttendanceFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private Spinner classSpinner;
    private Button startScanningBtn, finishScanningBtn;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentTeacherId;
    private List<ClassItem> teacherClasses;
    private String selectedClassId;
    private LinearLayout preScanLayout, postScanLayout;
    private DecoratedBarcodeView barcodeView;
    private List<String> scannedStudentIds = new ArrayList<>();

    public TeacherAttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentTeacherId = auth.getCurrentUser().getUid();
        teacherClasses = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_attendance, container, false);

        // Initialize views
        classSpinner = view.findViewById(R.id.classSpinner);
        startScanningBtn = view.findViewById(R.id.startScanningBtn);
        preScanLayout = view.findViewById(R.id.prescanlayout);
        postScanLayout = view.findViewById(R.id.postscanlayout);
        barcodeView = view.findViewById(R.id.barcode_view);
        finishScanningBtn = view.findViewById(R.id.finishScanningBtn);

        // Setup barcode scanner
        barcodeView.setStatusText("");
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null && !scannedStudentIds.contains(result.getText())) {
                    scannedStudentIds.add(result.getText());
                    Toast.makeText(getContext(), "Student scanned: " + result.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load teacher's classes
        loadTeacherClasses();

        // Set up spinner selection listener
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    ClassItem selectedClass = teacherClasses.get(position - 1);
                    selectedClassId = selectedClass.getId();
                } else {
                    selectedClassId = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedClassId = null;
            }
        });

        // Set up QR scanning button
        startScanningBtn.setOnClickListener(v -> {
            if (selectedClassId == null) {
                Toast.makeText(getContext(), "Please select a class first", Toast.LENGTH_SHORT).show();
                return;
            }
            checkCameraPermissionAndStartScanning();
        });

        // Set up finish scanning button
        finishScanningBtn.setOnClickListener(v -> finishScanning());

        return view;
    }

    private void loadTeacherClasses() {
        db.collection("classes")
                .whereEqualTo("teacherId", currentTeacherId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        teacherClasses.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String className = document.getString("className");
                            String grade = document.getString("grade");
                            String subject = document.getString("subject");
                            String teacherName = document.getString("teacherName");
                            String time = document.getString("time");

                            List<String> daysList = (List<String>) document.get("days");
                            String days = formatDays(daysList);

                            String schedule = days + " - " + time;
                            String classTitle = grade + " - " + subject;

                            ClassItem classItem = new ClassItem(
                                    id,
                                    classTitle,
                                    teacherName,
                                    0,
                                    schedule
                            );
                            teacherClasses.add(classItem);
                        }
                        updateClassSpinner();
                    } else {
                        Toast.makeText(getContext(), "Error loading classes", Toast.LENGTH_SHORT).show();
                        Log.e("TeacherAttendance", "Error loading classes", task.getException());
                    }
                });
    }

    private void updateClassSpinner() {
        List<String> classNames = new ArrayList<>();
        classNames.add("Select Class");

        for (ClassItem classItem : teacherClasses) {
            classNames.add(classItem.getClassName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                classNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(adapter);
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

    private void checkCameraPermissionAndStartScanning() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        } else {
            startScanningSession();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanningSession();
            } else {
                Toast.makeText(getContext(), "Camera permission is required for QR scanning", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startScanningSession() {
        scannedStudentIds.clear();
        preScanLayout.setVisibility(View.GONE);
        postScanLayout.setVisibility(View.VISIBLE);
        barcodeView.resume();
    }

    private void finishScanning() {
        barcodeView.pause();

        if (scannedStudentIds.isEmpty()) {
            Toast.makeText(getContext(), "No students scanned", Toast.LENGTH_SHORT).show();
            preScanLayout.setVisibility(View.VISIBLE);
            postScanLayout.setVisibility(View.GONE);
            return;
        }

        saveAttendanceRecord();
    }

    private void saveAttendanceRecord() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Map<String, Object> attendanceData = new HashMap<>();
        attendanceData.put("date", currentDate);
        attendanceData.put("classId", selectedClassId);
        attendanceData.put("timestamp", System.currentTimeMillis());
        attendanceData.put("studentIds", scannedStudentIds);

        db.collection("attendance")
                .add(attendanceData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(),
                            "Attendance recorded for " + scannedStudentIds.size() + " students",
                            Toast.LENGTH_SHORT).show();
                    preScanLayout.setVisibility(View.VISIBLE);
                    postScanLayout.setVisibility(View.GONE);
                    scannedStudentIds.clear();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error saving attendance", Toast.LENGTH_SHORT).show();
                    Log.e("TeacherAttendance", "Error saving attendance", e);
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (postScanLayout.getVisibility() == View.VISIBLE) {
            barcodeView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}