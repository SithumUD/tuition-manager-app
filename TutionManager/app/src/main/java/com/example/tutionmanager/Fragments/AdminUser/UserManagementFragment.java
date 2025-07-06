package com.example.tutionmanager.Fragments.AdminUser;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutionmanager.Adapters.UserAdapter;
import com.example.tutionmanager.Models.User;
import com.example.tutionmanager.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserManagementFragment extends Fragment {

    private TabLayout tabLayout;
    private RecyclerView userRecyclerView;
    private EditText searchEditText;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        tabLayout = view.findViewById(R.id.tabLayout);
        userRecyclerView = view.findViewById(R.id.userRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);

        // Setup RecyclerView
        userAdapter = new UserAdapter(userList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userRecyclerView.setAdapter(userAdapter);

        // Load initial data (all users)
        loadUsers("all");

        // Setup tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                searchEditText.setText(""); // Clear search when switching tabs
                switch (tab.getPosition()) {
                    case 0: // All Users
                        loadUsers("all");
                        break;
                    case 1: // Students
                        loadUsers("student");
                        break;
                    case 2: // Teachers
                        loadUsers("teacher");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Setup search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void loadUsers(String userType) {
        Query query = db.collection("users").orderBy("firstName");

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            user.setId(document.getId());
                            userList.add(user);
                        }
                        // Use the new updateList method with filter type
                        userAdapter.updateList(userList, userType);
                    }
                });
    }
}