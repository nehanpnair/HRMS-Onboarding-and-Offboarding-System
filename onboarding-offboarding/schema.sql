CREATE TABLE IF NOT EXISTS candidates (
    candidate_id VARCHAR(36) PRIMARY KEY,
    contact_info VARCHAR(100),
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    name VARCHAR(100) NOT NULL,
    skills TEXT,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS employees (
    emp_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    job_role VARCHAR(100),
    employment_status VARCHAR(50) NOT NULL,
    email VARCHAR(100)
);
