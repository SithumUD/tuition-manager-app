package com.example.tutionmanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tutionmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextView btnforgotPassword;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        btnforgotPassword = findViewById(R.id.btnforgotPassword);

        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
           checkUserTypeAndRedirect(currentUser.getUid());
        }

        btnforgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty()) {
                emailEditText.setError("Email is required");
                emailEditText.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordEditText.setError("Password is required");
                passwordEditText.requestFocus();
                return;
            }

            if (password.length() < 6) {
                passwordEditText.setError("Password must be at least 6 characters");
                passwordEditText.requestFocus();
                return;
            }

            // Show loading indicator
            loginButton.setEnabled(false);
            loginButton.setText("Logging in...");

            // Authenticate user with Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Login success
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkUserTypeAndRedirect(user.getUid());
                            }
                        } else {
                            // Login failed
                            loginButton.setEnabled(true);
                            loginButton.setText("Login");
                            Toast.makeText(LoginActivity.this,
                                    "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void checkUserTypeAndRedirect(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userType = document.getString("userType");
                            if (userType != null) {
                                redirectBasedOnUserType(userType);
                            } else {
                                // User type not found
                                Toast.makeText(LoginActivity.this,
                                        "User type not specified",
                                        Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }
                        } else {
                            // User document doesn't exist
                            Toast.makeText(LoginActivity.this,
                                    "User data not found",
                                    Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    } else {
                        // Error getting user data
                        Toast.makeText(LoginActivity.this,
                                "Error getting user data: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                });
    }

    private void redirectBasedOnUserType(String userType) {
        Intent intent;
        switch (userType.toLowerCase()) {
            case "admin":
                intent = new Intent(LoginActivity.this, AdminBaseActivity.class);
                break;
            case "teacher":
                intent = new Intent(LoginActivity.this, TeacherBaseActivity.class);
                break;
            case "student":
                intent = new Intent(LoginActivity.this, TeacherBaseActivity.class);
                break;
            default:
                Toast.makeText(this, "Unknown user type", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                return;
        }

        // Clear back stack so user can't go back to login with back button
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset login button state when activity resumes
        if (loginButton != null) {
            loginButton.setEnabled(true);
            loginButton.setText("Login");
        }
    }
}