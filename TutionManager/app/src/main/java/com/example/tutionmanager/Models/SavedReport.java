package com.example.tutionmanager.Models;

public class SavedReport {
    private String name;
    private String date;

    public SavedReport(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}