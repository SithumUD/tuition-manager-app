package com.example.tutionmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutionmanager.Models.User;
import com.example.tutionmanager.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecentRegistrationAdapter extends RecyclerView.Adapter<RecentRegistrationAdapter.ViewHolder> {

    private List<User> userList;

    public RecentRegistrationAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_registration_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        // Set user name
        holder.txtUsername.setText(user.getFirstName() + " " + user.getLastName());

        // Set registration time ago
        if (user.getRegistrationDate() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date registrationDate = sdf.parse(user.getRegistrationDate());
                Date currentDate = new Date();

                long diffInMillis = currentDate.getTime() - registrationDate.getTime();
                long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                String timeAgo;
                if (days == 0) {
                    timeAgo = "today";
                } else if (days == 1) {
                    timeAgo = "1 day ago";
                } else {
                    timeAgo = days + " days ago";
                }
                holder.txtRegisterDate.setText(timeAgo);
            } catch (ParseException e) {
                holder.txtRegisterDate.setText(user.getRegistrationDate());
            }
        }

        // Set user type and additional info
        String userTypeInfo = "";
        switch (user.getUserType().toLowerCase()) {
            case "admin":
                userTypeInfo = "Admin";
                break;
            case "teacher":
                userTypeInfo = "Teacher";
                break;
            case "student":
                userTypeInfo = "Student";
                break;
        }
        holder.txtUserType.setText(userTypeInfo);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtUsername, txtRegisterDate, txtUserType;

        public ViewHolder(View view) {
            super(view);
            txtUsername = view.findViewById(R.id.txtusername);
            txtRegisterDate = view.findViewById(R.id.registerdate);
            txtUserType = view.findViewById(R.id.txtusertype);
        }
    }
}