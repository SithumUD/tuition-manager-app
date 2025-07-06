package com.example.tutionmanager.Fragments.TeacherUser;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tutionmanager.Activities.AdminBaseActivity;
import com.example.tutionmanager.Activities.TeacherBaseActivity;
import com.example.tutionmanager.Fragments.AdminUser.AssignStudentsFragment;
import com.example.tutionmanager.Fragments.AdminUser.ClassManagementFragment;
import com.example.tutionmanager.Fragments.AdminUser.FinanceFragment;
import com.example.tutionmanager.Fragments.AdminUser.RegistrationFragment;
import com.example.tutionmanager.Fragments.AdminUser.UserManagementFragment;
import com.example.tutionmanager.R;
import com.google.android.material.card.MaterialCardView;

public class DashboardFragment extends Fragment {

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_teacher, container, false);

        // Initialize views
        CardView btnattendance = view.findViewById(R.id.btnattendance);
        CardView btnassignment = view.findViewById(R.id.btnassignment);
        CardView btnmaterial = view.findViewById(R.id.btnmaterial);
        CardView btnsalary = view.findViewById(R.id.btnsalary);

        // Set click listeners
        btnattendance.setOnClickListener(v -> navigateToFragment(new TeacherAttendanceFragment(), "Attendance"));
        btnassignment.setOnClickListener(v -> navigateToFragment(new TeacherAssignmentsFragment(), "Assignments"));
        btnmaterial.setOnClickListener(v -> navigateToFragment(new CourseMaterialsFragment(), "Course Material"));
        btnsalary.setOnClickListener(v -> navigateToFragment(new TeacherSalaryHistory(), "Salary History"));

        return view;
    }

    private void navigateToFragment(Fragment fragment, String title) {
        if (getActivity() != null) {
            // Update the toolbar title
            TextView titleTextView = getActivity().findViewById(R.id.txttile);
            titleTextView.setText(title);

            // Load the selected fragment
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null); // Optional: Add to back stack
            transaction.commit();

            // Update navigation selections
            updateNavigationSelections(fragment);
        }
    }

    private void updateNavigationSelections(Fragment fragment) {
        if (getActivity() instanceof TeacherBaseActivity) {
            TeacherBaseActivity activity = (TeacherBaseActivity) getActivity();

            if (fragment instanceof TeacherAttendanceFragment) {
                activity.updateBottomNavigationSelection(R.id.nav_attendance);
            } else if (fragment instanceof TeacherAssignmentsFragment) {
                activity.updateBottomNavigationSelection(R.id.nav_assignments);
            } else if (fragment instanceof TeacherSalaryHistory) {
                activity.updateBottomNavigationSelection(R.id.nav_salaryhistory);
            }
            // Add more conditions for other fragments as needed
        }
    }
}