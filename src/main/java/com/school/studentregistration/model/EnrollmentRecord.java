package com.school.studentregistration.model;

public class EnrollmentRecord {
    private final String studentName;
    private final String studentNumber;
    private final String courseName;
    private final double feeBalance;

    public EnrollmentRecord(String studentName, String studentNumber, String courseName, double feeBalance) {
        this.studentName = studentName;
        this.studentNumber = studentNumber;
        this.courseName = courseName;
        this.feeBalance = feeBalance;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getCourseName() {
        return courseName;
    }

    public double getFeeBalance() {
        return feeBalance;
    }
}
