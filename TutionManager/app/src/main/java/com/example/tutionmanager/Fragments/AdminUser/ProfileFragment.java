package com.example.tutionmanager.Fragments.AdminUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tutionmanager.Activities.LoginActivity;
import com.example.tutionmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private TextView txtName, txtDepartment, txtEmail, txtPhoneNumber, txtDate;
    private ImageView profileImage;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        btnLogout = view.findViewById(R.id.btn_logout);
        txtName = view.findViewById(R.id.txtname);
        txtDepartment = view.findViewById(R.id.txtdepartment);
        txtEmail = view.findViewById(R.id.txtemail);
        txtPhoneNumber = view.findViewById(R.id.txtphonenumber);
        txtDate = view.findViewById(R.id.txtdate);

        // Set click listener for logout button
        btnLogout.setOnClickListener(v -> logout());

        // Load user data
        if (currentUser != null) {
            loadUserData();
        } else {
            // If no user is logged in, redirect to login
            redirectToLogin();
        }

        return view;
    }

    private void loadUserData() {
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Set user data to views
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");
                            String email = document.getString("email");
                            String phone = document.getString("phone");
                            String userType = document.getString("userType");
                            String registrationDate = document.getString("registrationDate");

                            // Set name
                            if (firstName != null && lastName != null) {
                                txtName.setText(firstName + " " + lastName);
                            }

                            // Set department/role based on user type
                            if (userType != null) {
                                switch (userType.toLowerCase()) {
                                    case "admin":
                                        String department = document.getString("department");
                                        String accessLevel = document.getString("accessLevel");
                                        if (department != null && accessLevel != null) {
                                            txtDepartment.setText(department + " (" + capitalize(accessLevel) + ")");
                                        } else {
                                            txtDepartment.setText("Administrator");
                                        }
                                        break;
                                    case "teacher":
                                        String subject = document.getString("subject");
                                        if (subject != null) {
                                            txtDepartment.setText("Teacher - " + subject);
                                        } else {
                                            txtDepartment.setText("Teacher");
                                        }
                                        break;
                                    case "student":
                                        String studentClass = document.getString("class");
                                        if (studentClass != null) {
                                            txtDepartment.setText("Student - " + studentClass);
                                        } else {
                                            txtDepartment.setText("Student");
                                        }
                                        break;
                                    default:
                                        txtDepartment.setText("User");
                                }
                            }

                            // Set email and phone
                            if (email != null) {
                                txtEmail.setText(email);
                            }
                            if (phone != null) {
                                txtPhoneNumber.setText(phone);
                            }

                            // Format and set registration date
                            if (registrationDate != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    Date date = sdf.parse(registrationDate);
                                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                                    txtDate.setText("Member since " + displayFormat.format(date));
                                } catch (Exception e) {
                                    txtDate.setText("Member since " + registrationDate);
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout() {
        // Sign out from Firebase
        mAuth.signOut();

        // Redirect to login activity
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}