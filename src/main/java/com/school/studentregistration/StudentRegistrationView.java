package com.school.studentregistration;

import com.school.studentregistration.exception.ValidationException;
import com.school.studentregistration.model.EnrollmentRecord;
import com.school.studentregistration.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class StudentRegistrationView {
    // This class handles the form, button actions, and the table.
    private final DatabaseHelper databaseHelper = new DatabaseHelper();
    private final ObservableList<EnrollmentRecord> records = FXCollections.observableArrayList();

    private final TextField nameField = new TextField();
    private final TextField studentNumberField = new TextField();
    private final TextField feeBalanceField = new TextField();
    private final ComboBox<String> courseBox = new ComboBox<>();
    private final Label statusLabel = new Label("Fill in the form to register a student.");
    private final TableView<EnrollmentRecord> tableView = new TableView<>();

    public Parent createContent() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-shell");

        Label heading = new Label("Student Registration System");
        heading.getStyleClass().add("app-title");

        Label subheading = new Label("Register students, enforce fee clearance, and review enrolled courses.");
        subheading.getStyleClass().add("app-subtitle");

        VBox header = new VBox(6, heading, subheading);
        header.setPadding(new Insets(24, 24, 10, 24));

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(createRegistrationTab(), createViewTab());
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        root.setTop(header);
        root.setCenter(tabPane);
        root.setBottom(buildStatusBar());

        loadCourses();
        refreshRecords();

        return root;
    }

    private Tab createRegistrationTab() {
        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(14);
        form.setPadding(new Insets(24));
        form.getStyleClass().add("card");

        courseBox.setPromptText("Select a course");
        nameField.setPromptText("Enter student name");
        studentNumberField.setPromptText("Enter student number");
        feeBalanceField.setPromptText("Enter fee balance");

        form.add(createFieldLabel("Student Name"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(createFieldLabel("Student Number"), 0, 1);
        form.add(studentNumberField, 1, 1);
        form.add(createFieldLabel("Fee Balance"), 0, 2);
        form.add(feeBalanceField, 1, 2);
        form.add(createFieldLabel("Course"), 0, 3);
        form.add(courseBox, 1, 3);

        Button registerButton = new Button("Register Student");
        registerButton.getStyleClass().add("primary-button");
        registerButton.setOnAction(event -> registerStudent());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> clearForm());

        HBox actions = new HBox(12, registerButton, clearButton);
        actions.setAlignment(Pos.CENTER_LEFT);
        form.add(actions, 1, 4);

        VBox wrapper = new VBox(18, buildRulesCard(), form);
        wrapper.setPadding(new Insets(0, 24, 24, 24));

        return new Tab("Register Student", wrapper);
    }

    private VBox buildRulesCard() {
        Label title = new Label("Enrollment Rules");
        title.getStyleClass().add("section-title");

        Label ruleOne = new Label("1. A student can only enroll when fee balance is exactly 0.");
        Label ruleTwo = new Label("2. Student number must be unique.");
        Label ruleThree = new Label("3. All fields are required.");

        VBox card = new VBox(8, title, ruleOne, ruleTwo, ruleThree);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("info-card");
        return card;
    }

    private Tab createViewTab() {
        TableColumn<EnrollmentRecord, String> nameColumn = new TableColumn<>("Student Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<EnrollmentRecord, String> numberColumn = new TableColumn<>("Student Number");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));

        TableColumn<EnrollmentRecord, String> courseColumn = new TableColumn<>("Course");
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<EnrollmentRecord, Double> feeColumn = new TableColumn<>("Fee Balance");
        feeColumn.setCellValueFactory(new PropertyValueFactory<>("feeBalance"));

        tableView.getColumns().setAll(nameColumn, numberColumn, courseColumn, feeColumn);
        tableView.setItems(records);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.setPlaceholder(new Label("No enrolled students yet."));

        Button refreshButton = new Button("Refresh List");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> refreshRecords());

        VBox container = new VBox(16, refreshButton, tableView);
        container.setPadding(new Insets(24));
        VBox.setVgrow(tableView, Priority.ALWAYS);

        return new Tab("View Enrollments", container);
    }

    private HBox buildStatusBar() {
        statusLabel.getStyleClass().add("status-label");
        HBox bar = new HBox(statusLabel);
        bar.setPadding(new Insets(12, 24, 18, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    private void registerStudent() {
        try {
            // Build the student object from what the user typed in the form.
            Student student = buildStudent();
            String courseName = courseBox.getValue();
            validateCourse(courseName);

            if (!student.canEnroll()) {
                throw new ValidationException("Enrollment blocked: fee balance must be zero.");
            }

            databaseHelper.saveStudentAndEnrollment(student, courseName);
            setStatus("Student registered successfully in " + courseName + ".", false);
            clearForm();
            refreshRecords();
        } catch (ValidationException | SQLException exception) {
            setStatus(exception.getMessage(), true);
        } catch (Exception exception) {
            setStatus("Unexpected error: " + exception.getMessage(), true);
        }
    }

    private void refreshRecords() {
        try {
            List<EnrollmentRecord> enrollmentRecords = databaseHelper.getEnrollments();
            records.setAll(enrollmentRecords);
            setStatus("Enrollment list loaded successfully.", false);
        } catch (SQLException exception) {
            setStatus("Could not load records: " + exception.getMessage(), true);
        }
    }

    private void loadCourses() {
        try {
            databaseHelper.addDefaultCourses();
            List<String> courses = databaseHelper.getCourses();
            courseBox.setItems(FXCollections.observableArrayList(courses));
        } catch (SQLException exception) {
            setStatus("Could not load courses: " + exception.getMessage(), true);
        }
    }

    private void clearForm() {
        nameField.clear();
        studentNumberField.clear();
        feeBalanceField.clear();
        courseBox.getSelectionModel().clearSelection();
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-error", "status-success");
        statusLabel.getStyleClass().add(error ? "status-error" : "status-success");
    }

    private Student buildStudent() throws ValidationException, SQLException {
        String name = requireText(nameField.getText(), "Student name");
        String studentNumber = requireText(studentNumberField.getText(), "Student number");
        String feeBalanceText = requireText(feeBalanceField.getText(), "Fee balance");

        double feeBalance;
        try {
            feeBalance = Double.parseDouble(feeBalanceText);
        } catch (NumberFormatException exception) {
            throw new ValidationException("Fee balance must be a valid number.");
        }

        if (feeBalance < 0) {
            throw new ValidationException("Fee balance cannot be negative.");
        }

        if (databaseHelper.studentNumberExists(studentNumber.trim())) {
            throw new ValidationException("Student number already exists.");
        }

        // Create a Student object after validation is complete.
        return new Student(name.trim(), studentNumber.trim(), feeBalance);
    }

    private String requireText(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
        return value;
    }

    private void validateCourse(String courseName) throws ValidationException {
        if (courseName == null || courseName.isBlank()) {
            throw new ValidationException("Course is required.");
        }
    }
}
