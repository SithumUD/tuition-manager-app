package com.example.tutionmanager.Fragments.AdminUser;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutionmanager.Adapters.RecentRegistrationAdapter;
import com.example.tutionmanager.Models.User;
import com.example.tutionmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegistrationFragment extends Fragment {

    // UI Components
    private Button btnStudent, btnTeacher, btnAdmin;
    private LinearLayout studentFields, teacherFields, adminFields;
    private Button btnRegister;
    private EditText txtFirstName, txtLastName, txtEmail, txtPhoneNumber, txtPassword, txtCPassword;
    private Spinner txtClass, txtSubject, txtDepartment;
    private RadioGroup accessLevelRadioGroup;
    private RadioButton radioStandard, radioSupervisor, radioSuper;
    private RecyclerView recentRecyclerView;
    private RecentRegistrationAdapter recentAdapter;
    private List<User> recentUsers = new ArrayList<>();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        initializeViews(view);

        // Initialize spinners with dummy data
        initializeSpinners();

        // Set up RecyclerView for recent registrations
        setupRecentRegistrationsRecyclerView();

        // Load recent registrations
        loadRecentRegistrations();

        // Set default selection
        setTabSelection(btnStudent);
        showStudentFields();
        btnRegister.setText("Register Student");

        // Set click listeners
        setupTabListeners();
        setupRegisterButton();

        return view;
    }

    private void initializeViews(View view) {
        btnStudent = view.findViewById(R.id.btnStudent);
        btnTeacher = view.findViewById(R.id.btnTeacher);
        btnAdmin = view.findViewById(R.id.btnAdmin);
        studentFields = view.findViewById(R.id.studentFields);
        teacherFields = view.findViewById(R.id.teacherFields);
        adminFields = view.findViewById(R.id.adminFields);
        btnRegister = view.findViewById(R.id.btnRegister);
        recentRecyclerView = view.findViewById(R.id.recentRecyclerView);

        // Common fields
        txtFirstName = view.findViewById(R.id.txtfirstname);
        txtLastName = view.findViewById(R.id.txtsecondname);
        txtEmail = view.findViewById(R.id.txtemail);
        txtPhoneNumber = view.findViewById(R.id.txtphonenumber);
        txtPassword = view.findViewById(R.id.txtpassword);
        txtCPassword = view.findViewById(R.id.txtcpassword);

        // Student specific
        txtClass = view.findViewById(R.id.txtclass);

        // Teacher specific
        txtSubject = view.findViewById(R.id.txtsubject);

        // Admin specific
        txtDepartment = view.findViewById(R.id.txtdepartment);
        accessLevelRadioGroup = view.findViewById(R.id.accessLevelRadioGroup);
        radioStandard = view.findViewById(R.id.radioStandard);
        radioSupervisor = view.findViewById(R.id.radioSupervisor);
        radioSuper = view.findViewById(R.id.radioSuper);
    }

    private void setupRecentRegistrationsRecyclerView() {
        recentAdapter = new RecentRegistrationAdapter(recentUsers);
        recentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentRecyclerView.setAdapter(recentAdapter);
    }

    private void loadRecentRegistrations() {
        db.collection("users")
                .orderBy("registrationDate", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        recentUsers.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            user.setId(document.getId());
                            recentUsers.add(user);
                        }
                        recentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load recent registrations", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initializeSpinners() {
        // Dummy data for Class spinner (Student)
        String[] classes = {"Select Class", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5",
                "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10", "Grade 11", "Grade 12"};
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                classes
        );
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtClass.setAdapter(classAdapter);

        // Dummy data for Subject spinner (Teacher)
        String[] subjects = {"Select Subject", "Mathematics", "English", "Science", "History",
                "Geography", "Physics", "Chemistry", "Biology", "Computer Science",
                "Art", "Music", "Physical Education"};
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                subjects
        );
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtSubject.setAdapter(subjectAdapter);

        // Dummy data for Department spinner (Admin)
        String[] departments = {"Select Department", "Administration", "Academics", "Finance",
                "Human Resources", "IT", "Operations", "Student Affairs",
                "Faculty Management"};
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                departments
        );
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtDepartment.setAdapter(departmentAdapter);
    }

    private void setupTabListeners() {
        btnStudent.setOnClickListener(v -> {
            setTabSelection(btnStudent);
            showStudentFields();
            btnRegister.setText("Register Student");
        });

        btnTeacher.setOnClickListener(v -> {
            setTabSelection(btnTeacher);
            showTeacherFields();
            btnRegister.setText("Register Teacher");
        });

        btnAdmin.setOnClickListener(v -> {
            setTabSelection(btnAdmin);
            showAdminFields();
            btnRegister.setText("Register Admin");
        });
    }

    private void setupRegisterButton() {
        btnRegister.setOnClickListener(v -> {
            if (btnStudent.isSelected()) {
                registerStudent();
            } else if (btnTeacher.isSelected()) {
                registerTeacher();
            } else if (btnAdmin.isSelected()) {
                registerAdmin();
            }
        });
    }

    private void registerStudent() {
        if (!validateCommonFields()) return;

        if (txtClass.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select a class", Toast.LENGTH_SHORT).show();
            return;
        }

        String studentClass = txtClass.getSelectedItem().toString();

        registerUser("student", new HashMap<String, Object>() {{
            put("class", studentClass);
        }});
    }

    private void registerTeacher() {
        if (!validateCommonFields()) return;

        if (txtSubject.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select a subject", Toast.LENGTH_SHORT).show();
            return;
        }

        String subject = txtSubject.getSelectedItem().toString();

        registerUser("teacher", new HashMap<String, Object>() {{
            put("subject", subject);
        }});
    }

    private void registerAdmin() {
        if (!validateCommonFields()) return;

        if (txtDepartment.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select a department", Toast.LENGTH_SHORT).show();
            return;
        }

        String department = txtDepartment.getSelectedItem().toString();
        String accessLevel = getSelectedAccessLevel();

        registerUser("admin", new HashMap<String, Object>() {{
            put("department", department);
            put("accessLevel", accessLevel);
        }});
    }

    private String getSelectedAccessLevel() {
        int selectedId = accessLevelRadioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.radioSupervisor) {
            return "supervisor";
        } else if (selectedId == R.id.radioSuper) {
            return "super";
        }
        return "standard"; // default
    }

    private boolean validateCommonFields() {
        String firstName = txtFirstName.getText().toString().trim();
        String lastName = txtLastName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String phone = txtPhoneNumber.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtCPassword.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            txtFirstName.setError("First name is required");
            txtFirstName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            txtLastName.setError("Last name is required");
            txtLastName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Email is required");
            txtEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            txtPhoneNumber.setError("Phone number is required");
            txtPhoneNumber.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Password is required");
            txtPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            txtPassword.setError("Password must be at least 6 characters");
            txtPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            txtCPassword.setError("Passwords do not match");
            txtCPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser(String userType, Map<String, Object> additionalData) {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String firstName = txtFirstName.getText().toString().trim();
        String lastName = txtLastName.getText().toString().trim();
        String phone = txtPhoneNumber.getText().toString().trim();

        // Get current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String registrationDate = sdf.format(new Date());

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            // Create user data map
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("firstName", firstName);
                            userData.put("lastName", lastName);
                            userData.put("email", email);
                            userData.put("phone", phone);
                            userData.put("userType", userType);
                            userData.put("uid", user.getUid());
                            userData.put("registrationDate", registrationDate);
                            userData.put("isActive", true);

                            // Add additional data specific to user type
                            userData.putAll(additionalData);

                            // Save to Firestore
                            db.collection("users").document(user.getUid())
                                    .set(userData)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(getContext(),
                                                    capitalize(userType) + " registration successful!",
                                                    Toast.LENGTH_SHORT).show();
                                            clearForm();
                                            // Refresh recent registrations
                                            loadRecentRegistrations();
                                        } else {
                                            Toast.makeText(getContext(),
                                                    "Failed to store user data: " + dbTask.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(),
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void clearForm() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhoneNumber.setText("");
        txtPassword.setText("");
        txtCPassword.setText("");

        // Reset spinners to first item
        txtClass.setSelection(0);
        txtSubject.setSelection(0);
        txtDepartment.setSelection(0);

        // Reset radio buttons
        radioStandard.setChecked(true);
    }

    private void setTabSelection(Button selectedButton) {
        btnStudent.setSelected(false);
        btnTeacher.setSelected(false);
        btnAdmin.setSelected(false);
        selectedButton.setSelected(true);
    }

    private void showStudentFields() {
        studentFields.setVisibility(View.VISIBLE);
        teacherFields.setVisibility(View.GONE);
        adminFields.setVisibility(View.GONE);
    }

    private void showTeacherFields() {
        studentFields.setVisibility(View.GONE);
        teacherFields.setVisibility(View.VISIBLE);
        adminFields.setVisibility(View.GONE);
    }

    private void showAdminFields() {
        studentFields.setVisibility(View.GONE);
        teacherFields.setVisibility(View.GONE);
        adminFields.setVisibility(View.VISIBLE);
    }
}