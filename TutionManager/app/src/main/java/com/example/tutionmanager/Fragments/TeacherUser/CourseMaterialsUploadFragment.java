package com.example.tutionmanager.Fragments.TeacherUser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.tutionmanager.Models.ClassItem;
import com.example.tutionmanager.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseMaterialsUploadFragment extends Fragment {

    private static final int FILE_REQUEST_CODE = 1001;

    private Spinner classSelector;
    private TextInputEditText etMaterialsTitle;
    private Button btnBrowseFiles, btnUploadFiles, btnCancel;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private List<ClassItem> teacherClasses = new ArrayList<>();
    private String selectedClassId;
    private List<Uri> fileUris = new ArrayList<>();

    public CourseMaterialsUploadFragment() {
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
        return inflater.inflate(R.layout.fragment_course_materials_upload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        classSelector = view.findViewById(R.id.classselector);
        etMaterialsTitle = view.findViewById(R.id.et_materials_title);
        btnBrowseFiles = view.findViewById(R.id.btn_browse_files);
        btnUploadFiles = view.findViewById(R.id.btn_upload_files);
        btnCancel = view.findViewById(R.id.btnCancel);

        // Load teacher's classes
        loadTeacherClasses();

        // Set up browse files button
        btnBrowseFiles.setOnClickListener(v -> openFileChooser());

        // Set up upload files button
        btnUploadFiles.setOnClickListener(v -> uploadMaterials());

        // Set up cancel button
        btnCancel.setOnClickListener(v -> requireActivity().onBackPressed());
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
                fileUris.clear(); // Clear previous selections

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

    private void uploadMaterials() {
        // Validate inputs
        String title = etMaterialsTitle.getText().toString().trim();

        if (title.isEmpty()) {
            etMaterialsTitle.setError("Title is required");
            return;
        }

        if (selectedClassId == null) {
            Toast.makeText(getContext(), "Please select a class", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileUris.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload files to Cloudinary
        uploadFilesToCloudinary(title);
    }

    private void uploadFilesToCloudinary(String title) {
        List<String> fileUrls = new ArrayList<>();
        int totalFiles = fileUris.size();
        final int[] uploadedCount = {0};

        for (Uri fileUri : fileUris) {
            String fileName = getFileName(fileUri);
            String publicId = "course_materials/" + selectedClassId + "/" + System.currentTimeMillis() + "_" + fileName;

            MediaManager.get().upload(fileUri)
                    .option("public_id", publicId)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            // Upload started
                            Toast.makeText(getContext(), "Uploading files...", Toast.LENGTH_SHORT).show();
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
                                saveMaterialsToFirestore(title, fileUrls);
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

    private void saveMaterialsToFirestore(String title, List<String> fileUrls) {
        Map<String, Object> materialsData = new HashMap<>();
        materialsData.put("title", title);
        materialsData.put("classId", selectedClassId);
        materialsData.put("fileUrls", fileUrls);
        materialsData.put("uploadedAt", System.currentTimeMillis());
        materialsData.put("teacherId", auth.getCurrentUser().getUid());

        db.collection("course_materials")
                .add(materialsData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Course materials uploaded successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to upload materials: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}