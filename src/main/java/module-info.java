module com.school.studentregistration {
    requires java.sql;
    requires javafx.controls;

    exports com.school.studentregistration;
    exports com.school.studentregistration.model;
    opens com.school.studentregistration.model to javafx.base;
}
