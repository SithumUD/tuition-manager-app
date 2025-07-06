package com.example.tutionmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutionmanager.Models.User;
import com.example.tutionmanager.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {

    private List<User> userList;
    private List<User> userListFiltered;
    private String currentFilterType = "all"; // Track current filter type

    public UserAdapter(List<User> userList) {
        this.userList = userList;
        this.userListFiltered = userList;
    }

    // Add this method to update both the filter type and the list
    public void updateList(List<User> newList, String filterType) {
        this.currentFilterType = filterType;
        this.userList = newList;
        this.userListFiltered = filterByType(newList, filterType);
        notifyDataSetChanged();
    }

    // Helper method to filter by user type
    private List<User> filterByType(List<User> users, String type) {
        if (type.equals("all")) {
            return new ArrayList<>(users);
        }
        List<User> filtered = new ArrayList<>();
        for (User user : users) {
            if (user.getUserType().equalsIgnoreCase(type)) {
                filtered.add(user);
            }
        }
        return filtered;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userListFiltered.get(position);

        holder.userName.setText(user.getFirstName() + " " + user.getLastName());
        holder.userEmail.setText(user.getEmail());

        // Set user type and details based on user type
        switch (user.getUserType().toLowerCase()) {
            case "student":
                holder.userType.setText("Student");
                holder.userDetails.setText(user.getGrade() != null ? user.getGrade() : "Student");
                break;
            case "teacher":
                holder.userType.setText("Teacher");
                holder.userDetails.setText(user.getSubject() != null ? user.getSubject() : "Teacher");
                break;
            case "admin":
                holder.userType.setText("Admin");
                holder.userDetails.setText(user.getDepartment() != null ? user.getDepartment() : "Administrator");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return userListFiltered.size();
    }

    public void updateList(List<User> newList) {
        userList = newList;
        userListFiltered = newList;
        notifyDataSetChanged();
    }

    // Update your getFilter to consider both search text and user type
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString().toLowerCase();
                List<User> filteredList = new ArrayList<>();

                for (User user : userList) {
                    // First filter by type
                    if (!currentFilterType.equals("all") &&
                            !user.getUserType().equalsIgnoreCase(currentFilterType)) {
                        continue;
                    }

                    // Then filter by search text
                    if (charString.isEmpty() ||
                            user.getFirstName().toLowerCase().contains(charString) ||
                            user.getLastName().toLowerCase().contains(charString) ||
                            user.getEmail().toLowerCase().contains(charString)) {
                        filteredList.add(user);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userListFiltered = (List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, userEmail, userType, userDetails;

        public ViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.userName);
            userEmail = view.findViewById(R.id.userEmail);
            userType = view.findViewById(R.id.userType);
            userDetails = view.findViewById(R.id.userDetails);
        }
    }
}