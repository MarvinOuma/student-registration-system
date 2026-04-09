package com.school.studentregistration.model;

public class Student extends Person {
    private String studentNumber;
    private double feeBalance;

    public Student(String name, String studentNumber, double feeBalance) {
        super(name);
        this.studentNumber = studentNumber;
        this.feeBalance = feeBalance;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public double getFeeBalance() {
        return feeBalance;
    }

    public void setFeeBalance(double feeBalance) {
        this.feeBalance = feeBalance;
    }

    public boolean canEnroll() {
        return feeBalance == 0.0;
    }

    // Overloaded version of the same method to show polymorphism.
    public boolean canEnroll(double allowedBalance) {
        return feeBalance <= allowedBalance;
    }

    @Override
    public String getDisplayRole() {
        return "Student";
    }
}
