package com.example.tutionmanager.Fragments.AdminUser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tutionmanager.Activities.AdminBaseActivity;
import com.example.tutionmanager.R;
import com.google.android.material.card.MaterialCardView;

public class DashboardFragment extends Fragment {

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize views
        MaterialCardView btnUserManagement = view.findViewById(R.id.card_user_management);
        MaterialCardView btnRegistration = view.findViewById(R.id.card_registration);
        MaterialCardView btnClassManagement = view.findViewById(R.id.card_class_management);
        MaterialCardView btnFinance = view.findViewById(R.id.card_finance);

        // Set click listeners
        btnUserManagement.setOnClickListener(v -> navigateToFragment(new UserManagementFragment(), "User Management"));
        btnRegistration.setOnClickListener(v -> navigateToFragment(new RegistrationFragment(), "Registration"));
        btnClassManagement.setOnClickListener(v -> navigateToFragment(new ClassManagementFragment(), "Class Management"));
        btnFinance.setOnClickListener(v -> navigateToFragment(new FinanceFragment(), "Finance"));

        return view;
    }

    private void navigateToFragment(Fragment fragment, String title) {
        if (getActivity() != null) {
            // Update the toolbar title
            ((AdminBaseActivity) getActivity()).getSupportActionBar().setTitle(title);

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
        if (getActivity() instanceof AdminBaseActivity) {
            AdminBaseActivity activity = (AdminBaseActivity) getActivity();

            if (fragment instanceof UserManagementFragment) {
                activity.updateBottomNavigationSelection(R.id.nav_user_management);
                activity.updateNavigationDrawerSelection(R.id.nav_user_management);
            } else if (fragment instanceof ClassManagementFragment) {
                activity.updateBottomNavigationSelection(R.id.nav_class_management);
                activity.updateNavigationDrawerSelection(R.id.nav_class_management);
            } else if (fragment instanceof AssignStudentsFragment) {
                activity.updateBottomNavigationSelection(R.id.nav_assign_students);
                activity.updateNavigationDrawerSelection(R.id.nav_assign_students);
            } else if (fragment instanceof RegistrationFragment) {
                activity.updateBottomNavigationSelection(R.id.nav_registration);
                activity.updateNavigationDrawerSelection(R.id.nav_registration);
            } else if (fragment instanceof FinanceFragment) {
                activity.updateBottomNavigationSelection(R.id.nav_finance);
                activity.updateNavigationDrawerSelection(R.id.nav_finance);
            }
            // Add more conditions for other fragments as needed
        }
    }
}