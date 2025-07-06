package com.example.tutionmanager.Activities;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tutionmanager.Fragments.AdminUser.AssignStudentsFragment;
import com.example.tutionmanager.Fragments.AdminUser.ClassManagementFragment;
import com.example.tutionmanager.Fragments.AdminUser.ProfileFragment;
import com.example.tutionmanager.Fragments.AdminUser.UserManagementFragment;
import com.example.tutionmanager.Fragments.TeacherUser.DashboardFragment;
import com.example.tutionmanager.Fragments.TeacherUser.TeacherAssignmentsFragment;
import com.example.tutionmanager.Fragments.TeacherUser.TeacherAttendanceFragment;
import com.example.tutionmanager.Fragments.TeacherUser.TeacherSalaryHistory;
import com.example.tutionmanager.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class TeacherBaseActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView txttitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_base);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        ImageView ivProfile = findViewById(R.id.iv_profile);
        txttitle = findViewById(R.id.txttile);

        ivProfile.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            txttitle.setText("Profile");
        });

        setupBottomNavigationView();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
            txttitle.setText("Teacher Dashboard");
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
                title = "Teacher Dashboard";
            } else if (id == R.id.nav_attendance) {
                fragment = new TeacherAttendanceFragment();
                title = "Attendance";
            } else if (id == R.id.nav_assignments) {
                fragment = new TeacherAssignmentsFragment();
                title = "Assignments";
            } else if (id == R.id.nav_salaryhistory) {
                fragment = new TeacherSalaryHistory();
                title = "Salary History";
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