# Student Registration System

This is my JavaFX project for the Advanced Object Oriented Programming unit. The system is used to register students and only allow course enrollment when the student's fee balance is zero.

## What the system does

- takes student details from a form
- checks if the fee balance is cleared
- saves the student and course enrollment in PostgreSQL
- shows the enrolled students in a table

## Main OOP concepts used

- Encapsulation
  private fields are used in classes like `Student`
- Inheritance
  `Student` extends `Person`
- Polymorphism
  `getDisplayRole()` is overridden and `canEnroll()` is overloaded
- Generics
  used in collections like `List<String>` and `ObservableList<EnrollmentRecord>`

## Project files in a simple way

- `MainApp.java`
  starts the program
- `StudentRegistrationView.java`
  contains the JavaFX form and table
- `DatabaseHelper.java`
  handles database connection and SQL queries
- `Person.java` and `Student.java`
  used to show OOP
- `schema.sql`
  creates the tables

## How it works

1. The user enters name, student number, fee balance, and course.
2. The program checks the input.
3. If the fee balance is `0`, the student is saved.
4. The enrollment is stored in the database.
5. The saved records are displayed in the table.

## Database setup

Database name used:

`student_registration_db`

You can create the tables using:

```powershell
.\scripts\init-db.ps1
```

Database details are in:

`src/main/java/com/school/studentregistration/config/DatabaseConfig.java`

## Running the project

In command prompt:

```bat
mvn clean javafx:run
```

Or if Maven is not yet on your path:

```bat
"%USERPROFILE%\tools\apache-maven-3.9.12\bin\mvn.cmd" clean javafx:run
```

## Notes

- the system uses JavaFX for the interface
- PostgreSQL is connected using JDBC
- CSS was used to improve the interface a bit
- `TableView` was used to display enrolled students

## Report

I added a simple report template in:

`docs/REPORT_TEMPLATE.md`
