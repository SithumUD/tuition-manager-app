package com.example.tutionmanager.Models;

public class ClassItem {
    private String id;
    private final String className;
    private final String teacherName;
    private final int studentCount;
    private final String schedule;

    public ClassItem(String id, String className, String teacherName, int studentCount, String schedule) {
        this.id = id;
        this.className = className;
        this.teacherName = teacherName;
        this.studentCount = studentCount;
        this.schedule = schedule;
    }

    public ClassItem(String className, String teacherName, int studentCount, String schedule) {
        this.className = className;
        this.teacherName = teacherName;
        this.studentCount = studentCount;
        this.schedule = schedule;
    }

    public String getId() {
        return id;
    }

    public String getClassName() { return className; }
    public String getTeacherName() { return teacherName; }
    public int getStudentCount() { return studentCount; }
    public String getSchedule() { return schedule; }
}