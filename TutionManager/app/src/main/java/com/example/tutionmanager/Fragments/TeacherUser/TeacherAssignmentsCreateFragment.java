package com.example.tutionmanager.Fragments.TeacherUser;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.tutionmanager.Models.Assignment;
import com.example.tutionmanager.Models.ClassItem;
import com.example.tutionmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TeacherAssignmentsCreateFragment extends Fragment {

    private static final int FILE_REQUEST_CODE = 1001;

    private EditText txtTitle, txtDescription, txtTotalMarks;
    private TextView txtDueDate;
    private Spinner classSelector;
    private LinearLayout btnDueDate, btnAttachment;
    private Button btnCancel, btnCreate;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private List<ClassItem> teacherClasses = new ArrayList<>();
    private String selectedClassId;
    private List<Uri> fileUris = new ArrayList<>();
    private Calendar dueDateCalendar = Calendar.getInstance();

    public TeacherAssignmentsCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize Cloudinary (should be done once in your Application class ideally)
        try {
            Map config = new HashMap();
            config.put("cloud_name", "dg0eycgai");
            config.put("api_key", "819671973687629");
            config.put("api_secret", "6rtcKaUn6BIhTESDhyS0lDnpEww");
            MediaManager.init(requireContext(), config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_assignments_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        txtTitle = view.findViewById(R.id.txttitle);
        txtDescription = view.findViewById(R.id.txtdecs);
        txtTotalMarks = view.findViewById(R.id.txttotalmarks);
        txtDueDate = view.findViewById(R.id.txtduedate);
        classSelector = view.findViewById(R.id.classselector);
        btnDueDate = view.findViewById(R.id.btnduedate);
        btnAttachment = view.findViewById(R.id.btnattachment);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnCreate = view.findViewById(R.id.btnCreate);

        // Load teacher's classes
        loadTeacherClasses();

        // Set up due date picker
        btnDueDate.setOnClickListener(v -> showDatePickerDialog());

        // Set up file attachment
        btnAttachment.setOnClickListener(v -> openFileChooser());

        // Set up cancel button
        btnCancel.setOnClickListener(v -> requireActivity().onBackPressed());

        // Set up create button
        btnCreate.setOnClickListener(v -> createAssignment());
    }

    private void loadTeacherClasses() {
        String teacherId = auth.getCurrentUser().getUid();

        db.collection("classes")
                .whereEqualTo("teacherId", teacherId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        teacherClasses.clear();
                        List<String> classNames = new ArrayList<>();
                        classNames.add("Select Class"); // Default option

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String className = document.getString("className");
                            String grade = document.getString("grade");
                            String subject = document.getString("subject");

                            ClassItem classItem = new ClassItem(
                                    id,
                                    grade + " - " + subject,
                                    document.getString("teacherName"),
                                    0, // student count not needed here
                                    "" // schedule not needed here
                            );
                            teacherClasses.add(classItem);
                            classNames.add(classItem.getClassName());
                        }

                        // Set up spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                classNames
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        classSelector.setAdapter(adapter);

                        classSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    selectedClassId = teacherClasses.get(position - 1).getId();
                                } else {
                                    selectedClassId = null;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                selectedClassId = null;
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    dueDateCalendar.set(year, month, dayOfMonth);
                    updateDueDateText();
                },
                dueDateCalendar.get(Calendar.YEAR),
                dueDateCalendar.get(Calendar.MONTH),
                dueDateCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateDueDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        txtDueDate.setText(sdf.format(dueDateCalendar.getTime()));
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // All file types
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Multiple files selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri fileUri = data.getClipData().getItemAt(i).getUri();
                        fileUris.add(fileUri);
                    }
                } else if (data.getData() != null) {
                    // Single file selected
                    fileUris.add(data.getData());
                }

                // Update UI to show selected files
                Toast.makeText(getContext(), "Selected " + fileUris.size() + " files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createAssignment() {
        // Validate inputs
        String title = txtTitle.getText().toString().trim();
        String description = txtDescription.getText().toString().trim();
        String totalMarksStr = txtTotalMarks.getText().toString().trim();
        String dueDate = txtDueDate.getText().toString().trim();

        if (title.isEmpty()) {
            txtTitle.setError("Title is required");
            return;
        }

        if (selectedClassId == null) {
            Toast.makeText(getContext(), "Please select a class", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dueDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalMarksStr.isEmpty()) {
            txtTotalMarks.setError("Total marks is required");
            return;
        }

        int totalMarks;
        try {
            totalMarks = Integer.parseInt(totalMarksStr);
        } catch (NumberFormatException e) {
            txtTotalMarks.setError("Invalid number");
            return;
        }

        // Create assignment object
        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setClassId(selectedClassId);
        assignment.setDueDate(dueDate);
        assignment.setTotalMarks(totalMarks);
        assignment.setCreatedAt(new Date());
        assignment.setTeacherId(auth.getCurrentUser().getUid());

        // Upload files to Cloudinary if any
        if (!fileUris.isEmpty()) {
            uploadFilesToCloudinary(assignment);
        } else {
            saveAssignmentToFirestore(assignment, new ArrayList<>());
        }
    }

    private void uploadFilesToCloudinary(Assignment assignment) {
        List<String> fileUrls = new ArrayList<>();
        int totalFiles = fileUris.size();
        final int[] uploadedCount = {0};

        for (Uri fileUri : fileUris) {
            String fileName = getFileName(fileUri);
            String publicId = "assignments/" + assignment.getClassId() + "/" + System.currentTimeMillis() + "_" + fileName;

            MediaManager.get().upload(fileUri)
                    .option("public_id", publicId)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            // Upload started
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            // Upload progress
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String secureUrl = (String) resultData.get("secure_url");
                            fileUrls.add(secureUrl);
                            uploadedCount[0]++;

                            // Check if all files are uploaded
                            if (uploadedCount[0] == totalFiles) {
                                assignment.setAttachmentUrls(fileUrls);
                                saveAssignmentToFirestore(assignment, fileUrls);
                            }
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(getContext(), "Failed to upload file: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            // Handle reschedule
                        }
                    })
                    .dispatch();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (var cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow("_display_name"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void saveAssignmentToFirestore(Assignment assignment, List<String> fileUrls) {
        Map<String, Object> assignmentData = new HashMap<>();
        assignmentData.put("title", assignment.getTitle());
        assignmentData.put("description", assignment.getDescription());
        assignmentData.put("classId", assignment.getClassId());
        assignmentData.put("dueDate", assignment.getDueDate());
        assignmentData.put("totalMarks", assignment.getTotalMarks());
        assignmentData.put("createdAt", assignment.getCreatedAt());
        assignmentData.put("teacherId", assignment.getTeacherId());
        assignmentData.put("attachmentUrls", fileUrls);

        db.collection("assignments")
                .add(assignmentData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Assignment created successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to create assignment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}