package com.example.tutionmanager.Fragments.TeacherUser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tutionmanager.Fragments.AdminUser.CreateClassFragment;
import com.example.tutionmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TeacherAssignmentsFragment extends Fragment {

    private FloatingActionButton fabAddAssignment;

    public TeacherAssignmentsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_teacher_assignments, container, false);

        fabAddAssignment = view.findViewById(R.id.fab_add_assignment);

        // FAB click listener
        fabAddAssignment.setOnClickListener(v -> {
            TeacherAssignmentsCreateFragment teacherAssignmentsCreateFragment = new TeacherAssignmentsCreateFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, teacherAssignmentsCreateFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}