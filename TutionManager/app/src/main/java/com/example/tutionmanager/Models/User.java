package com.example.tutionmanager.Models;

import java.util.Date;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String userType;
    private String registrationDate;
    private String grade; // For students
    private String subject; // For teachers
    private String department; // For admins

    // Add constructors, getters and setters
    public User() {}

    // Getters and setters for all fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }
    public String getGrade() { return grade; }
    public void setClass(String grade) { this.grade = grade; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}