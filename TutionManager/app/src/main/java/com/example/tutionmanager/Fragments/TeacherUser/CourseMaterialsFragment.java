package com.example.tutionmanager.Fragments.TeacherUser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tutionmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CourseMaterialsFragment extends Fragment {

    private FloatingActionButton fabAddmaterials;

    public CourseMaterialsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_course_materials, container, false);

        fabAddmaterials = view.findViewById(R.id.fab_add_material);

        // FAB click listener
        fabAddmaterials.setOnClickListener(v -> {
            CourseMaterialsUploadFragment courseMaterialsUploadFragment = new CourseMaterialsUploadFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, courseMaterialsUploadFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}