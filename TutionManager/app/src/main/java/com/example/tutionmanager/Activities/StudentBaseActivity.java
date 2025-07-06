package com.example.tutionmanager.Activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tutionmanager.Fragments.AdminUser.ProfileFragment;
import com.example.tutionmanager.Fragments.StudentUser.DashboardFragment;
import com.example.tutionmanager.Fragments.StudentUser.StudentAssignmentFragment;
import com.example.tutionmanager.Fragments.StudentUser.StudentAttendanceFragment;
import com.example.tutionmanager.Fragments.StudentUser.StudentNotificationFragment;
import com.example.tutionmanager.Fragments.StudentUser.StudentProfileFragment;
import com.example.tutionmanager.Fragments.TeacherUser.TeacherAssignmentsFragment;
import com.example.tutionmanager.Fragments.TeacherUser.TeacherAttendanceFragment;
import com.example.tutionmanager.Fragments.TeacherUser.TeacherSalaryHistory;
import com.example.tutionmanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StudentBaseActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView txttitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_base);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        ImageView ivProfile = findViewById(R.id.iv_profile);
        txttitle = findViewById(R.id.txttile);

        ivProfile.setOnClickListener(v -> {
            loadFragment(new StudentProfileFragment());
            txttitle.setText("Profile");
        });

        setupBottomNavigationView();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new StudentProfileFragment());
            txttitle.setText("Student Dashboard");
            bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            String title = "";

            if (id == R.id.nav_home) {
                fragment = new DashboardFragment();
                title = "Student Dashboard";
            } else if (id == R.id.nav_attendance) {
                fragment = new StudentAttendanceFragment();
                title = "Attendance";
            } else if (id == R.id.nav_assignments) {
                fragment = new StudentAssignmentFragment();
                title = "Assignments";
            } else if (id == R.id.nav_notification) {
                fragment = new StudentNotificationFragment();
                title = "Notification";
            }

            if (fragment != null) {
                loadFragment(fragment);
                txttitle.setText(title);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public void updateBottomNavigationSelection(int menuItemId) {
        try {
            bottomNavigationView.setSelectedItemId(menuItemId);
        } catch (Exception e) {
            // Item not found in bottom navigation
        }
    }
}
