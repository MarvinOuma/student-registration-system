CREATE TABLE IF NOT EXISTS students (
    id SERIAL PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    student_number VARCHAR(30) NOT NULL UNIQUE,
    fee_balance NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
    id SERIAL PRIMARY KEY,
    course_name VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS enrollments (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    course_id INTEGER NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE(student_id, course_id)
);
