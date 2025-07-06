package com.example.tutionmanager.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tutionmanager.Fragments.AdminUser.AssignStudentsFragment;
import com.example.tutionmanager.Fragments.AdminUser.ClassManagementFragment;
import com.example.tutionmanager.Fragments.AdminUser.DashboardFragment;
import com.example.tutionmanager.Fragments.AdminUser.FinanceFragment;
import com.example.tutionmanager.Fragments.AdminUser.ProfileFragment;
import com.example.tutionmanager.Fragments.AdminUser.RegistrationFragment;
import com.example.tutionmanager.Fragments.AdminUser.ReportsFragment;
import com.example.tutionmanager.Fragments.AdminUser.UserManagementFragment;
import com.example.tutionmanager.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class AdminBaseActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_base);

        // Set window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
        ImageView ivProfile = findViewById(R.id.iv_profile);

        // Set up toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        // Profile icon click listener
        ivProfile.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            toolbar.setTitle("Profile");
            closeDrawer();
        });

        // Set up navigation listeners
        setupNavigationView();
        setupBottomNavigationView();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
            toolbar.setTitle("Dashboard");
            bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            String title = "";

            if (id == R.id.nav_dashboard) {
                fragment = new DashboardFragment();
                title = "Dashboard";
            } else if (id == R.id.nav_user_management) {
                fragment = new UserManagementFragment();
                title = "User Management";
            } else if (id == R.id.nav_registration) {
                fragment = new RegistrationFragment();
                title = "Registration";
            } else if (id == R.id.nav_class_management) {
                fragment = new ClassManagementFragment();
                title = "Class Management";
            } else if (id == R.id.nav_assign_students) {
                fragment = new AssignStudentsFragment();
                title = "Assign Students";
            } else if (id == R.id.nav_finance) {
                fragment = new FinanceFragment();
                title = "Finance";
            } else if (id == R.id.nav_reports) {
                fragment = new ReportsFragment();
                title = "Reports";
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
                title = "Profile";
            }

            if (fragment != null) {
                loadFragment(fragment);
                toolbar.setTitle(title);
                // Update bottom navigation to match if possible
                updateBottomNavigationSelection(id);
            }

            closeDrawer();
            return true;
        });
    }

    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            String title = "";

            if (id == R.id.nav_dashboard) {
                fragment = new DashboardFragment();
                title = "Dashboard";
            } else if (id == R.id.nav_user_management) {
                fragment = new UserManagementFragment();
                title = "User Management";
            } else if (id == R.id.nav_class_management) {
                fragment = new ClassManagementFragment();
                title = "Class Management";
            } else if (id == R.id.nav_assign_students) {
                fragment = new AssignStudentsFragment();
                title = "Assign Students";
            }

            if (fragment != null) {
                loadFragment(fragment);
                toolbar.setTitle(title);
                // Update navigation drawer to match if possible
                updateNavigationDrawerSelection(id);
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

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        }
    }

    public void updateBottomNavigationSelection(int menuItemId) {
        try {
            bottomNavigationView.setSelectedItemId(menuItemId);
        } catch (Exception e) {
            // Item not found in bottom navigation
        }
    }

    public void updateNavigationDrawerSelection(int menuItemId) {
        try {
            navigationView.setCheckedItem(menuItemId);
        } catch (Exception e) {
            // Item not found in navigation drawer
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }
}