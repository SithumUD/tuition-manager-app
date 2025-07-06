package com.example.tutionmanager.Models;

public class StudentItem {
    private String id;
    private String name;
    private String grade;

    public StudentItem(String id, String name, String grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getGrade() { return grade; }
}