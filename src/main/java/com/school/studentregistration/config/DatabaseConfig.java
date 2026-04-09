package com.school.studentregistration.config;

public final class DatabaseConfig {
    private DatabaseConfig() {
    }

    public static final String URL = "jdbc:postgresql://localhost:5432/student_registration_db";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "pass";
}
