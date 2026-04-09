package com.school.studentregistration;

import com.school.studentregistration.config.DatabaseConfig;
import com.school.studentregistration.model.EnrollmentRecord;
import com.school.studentregistration.model.Student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    public Connection getConnection() throws SQLException {
        // This method is used whenever the program needs to talk to PostgreSQL.
        return DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
        );
    }

    public void addDefaultCourses() throws SQLException {
        List<String> courses = List.of(
                "Advanced Object Oriented Programming",
                "Business Process Management",
                "Database Systems",
                "Systems Analysis and Design"
        );

        String sql = "INSERT INTO courses(course_name) VALUES (?) ON CONFLICT (course_name) DO NOTHING";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String course : courses) {
                statement.setString(1, course);
                statement.executeUpdate();
            }
        }
    }

    public List<String> getCourses() throws SQLException {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT course_name FROM courses ORDER BY course_name";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                courses.add(resultSet.getString("course_name"));
            }
        }

        return courses;
    }

    public boolean studentNumberExists(String studentNumber) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE student_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public void saveStudentAndEnrollment(Student student, String courseName) throws SQLException {
        String studentSql = "INSERT INTO students(student_name, student_number, fee_balance) VALUES (?, ?, ?)";
        String courseSql = "SELECT id FROM courses WHERE course_name = ?";
        String enrollmentSql = "INSERT INTO enrollments(student_id, course_id) VALUES (?, ?)";

        try (Connection connection = getConnection()) {
            // If one query fails, both student and enrollment should be cancelled.
            connection.setAutoCommit(false);

            try (PreparedStatement studentStatement = connection.prepareStatement(studentSql, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement courseStatement = connection.prepareStatement(courseSql);
                 PreparedStatement enrollmentStatement = connection.prepareStatement(enrollmentSql)) {

                studentStatement.setString(1, student.getName());
                studentStatement.setString(2, student.getStudentNumber());
                studentStatement.setDouble(3, student.getFeeBalance());
                studentStatement.executeUpdate();

                int studentId;
                try (ResultSet keys = studentStatement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Student could not be saved.");
                    }
                    studentId = keys.getInt(1);
                }

                courseStatement.setString(1, courseName);
                int courseId;
                try (ResultSet resultSet = courseStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new SQLException("Selected course was not found.");
                    }
                    courseId = resultSet.getInt("id");
                }

                enrollmentStatement.setInt(1, studentId);
                enrollmentStatement.setInt(2, courseId);
                enrollmentStatement.executeUpdate();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<EnrollmentRecord> getEnrollments() throws SQLException {
        List<EnrollmentRecord> records = new ArrayList<>();
        String sql = """
                SELECT s.student_name, s.student_number, c.course_name, s.fee_balance
                FROM enrollments e
                JOIN students s ON e.student_id = s.id
                JOIN courses c ON e.course_id = c.id
                ORDER BY s.student_name
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                records.add(new EnrollmentRecord(
                        resultSet.getString("student_name"),
                        resultSet.getString("student_number"),
                        resultSet.getString("course_name"),
                        resultSet.getDouble("fee_balance")
                ));
            }
        }

        return records;
    }
}
