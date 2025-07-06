package com.example.tutionmanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tutionmanager.R;

public class PasswordResetActivity extends AppCompatActivity {

    private LinearLayout resetFormContainer, confirmationContainer;
    private EditText emailEditText;
    private TextView backToLogin, emailSentText;
    private Button resetButton, returnToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);

        // Initialize views
        resetFormContainer = findViewById(R.id.resetFormContainer);
        confirmationContainer = findViewById(R.id.confirmationContainer);
        emailEditText = findViewById(R.id.emailEditText);
        backToLogin = findViewById(R.id.backToLogin);
        emailSentText = findViewById(R.id.emailSentText);
        resetButton = findViewById(R.id.resetButton);
        returnToLoginButton = findViewById(R.id.returnToLoginButton);

        // Set click listeners
        backToLogin.setOnClickListener(v -> finish());

        resetButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                emailEditText.setError("Please enter your email");
                return;
            }

            // Show confirmation screen
            emailSentText.setText(email);
            resetFormContainer.setVisibility(View.GONE);
            confirmationContainer.setVisibility(View.VISIBLE);
        });

        returnToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordResetActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}