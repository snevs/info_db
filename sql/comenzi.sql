-- Create COSTI_Departamente Table
CREATE TABLE COSTI_Departamente (
    dept_id NUMBER PRIMARY KEY                      -- Identificatorul unic al departamentului
    dept_name VARCHAR2(100),                        -- Numele departamentului
    dept_location VARCHAR2(100)                     -- Locatia departamentului
);

-- Create COSTI_Angajati Table
CREATE TABLE COSTI_Angajati (
    emp_id NUMBER PRIMARY KEY,                      -- Identificatorul unic al angajatului
    emp_name VARCHAR2(100),                         -- Numele angajatului
    emp_email VARCHAR2(100),                        -- Adresa de email a angajatului
    emp_phone VARCHAR2(20),                         -- Numarul de telefon al angajatului
    emp_address VARCHAR2(200),                      -- Adresa angajatului
    emp_department_id NUMBER,                       -- ID-ul departamentului al angajatului
    FOREIGN KEY (emp_department_id) 
    REFERENCES COSTI_Departamente(dept_id) .        -- Cheie externa care face referire la tabela departamentelor
);

-- Create COSTI_Salarii Table
CREATE TABLE COSTI_Salarii (
    salary_id NUMBER PRIMARY KEY,                   -- Identificatorul unic al salariului
    emp_id NUMBER,                                  -- Identificatorul angajatului asociat salariului
    salary_amount NUMBER,                           -- Suma salariului
    salary_date DATE,                               -- Data la care s-a efectuat plata salariului
    FOREIGN KEY (emp_id) 
    REFERENCES COSTI_Angajati(emp_id)               -- Cheie externa care face referire la tabela angajatilor
);

-- Create COSTI_Prezenta Table
CREATE TABLE COSTI_Prezenta (
    attendance_id NUMBER PRIMARY KEY,               -- Identificatorul unic al prezentei
    emp_id NUMBER,                                  -- Identificatorul angajatului asociat prezentei
    attendance_date DATE,                           -- Data la care s-a inregistrat prezenta
    attendance_status VARCHAR2(50) 
    CHECK (attendance_status IN ('Prezent', 'Absent')),       -- Verificare constraint. Valorile din attendance_status pot fi doar Absent sau Prezent
    FOREIGN KEY (emp_id) REFERENCES COSTI_Angajati(emp_id)    -- Cheie externa care face referire la tabela angajatilor
);



-- Insert into COSTI_Departamente Table
INSERT INTO COSTI_Departamente (dept_id, dept_name, dept_location) VALUES (1, 'Engineering', 'Paris');
INSERT INTO COSTI_Departamente (dept_id, dept_name, dept_location) VALUES (2, 'Marketing', 'Berlin');
INSERT INTO COSTI_Departamente (dept_id, dept_name, dept_location) VALUES (3, 'Finance', 'Madrid');

-- Insert into COSTI_Angajati Table
INSERT INTO COSTI_Angajati (emp_id, emp_name, emp_email, emp_phone, emp_address, emp_department_id) VALUES (1, 'Andrei Popescu', 'andrei@example.com', '1234567890', 'Strada Principala 123, Paris', 1);
INSERT INTO COSTI_Angajati (emp_id, emp_name, emp_email, emp_phone, emp_address, emp_department_id) VALUES (2, 'Elena Ionescu', 'elena@example.com', '0987654321', 'Strada Elm 456, Berlin', 2);
INSERT INTO COSTI_Angajati (emp_id, emp_name, emp_email, emp_phone, emp_address, emp_department_id) VALUES (3, 'Mihai Radu', 'mihai@example.com', '1112223333', 'Strada Stejar 789, Madrid', 3);

-- Insert into COSTI_Salarii Table
INSERT INTO COSTI_Salarii (salary_id, emp_id, salary_amount, salary_date) VALUES (1, 1, 60000, TO_DATE('2024-03-15', 'YYYY-MM-DD'));
INSERT INTO COSTI_Salarii (salary_id, emp_id, salary_amount, salary_date) VALUES (2, 2, 55000, TO_DATE('2024-03-01', 'YYYY-MM-DD'));
INSERT INTO COSTI_Salarii (salary_id, emp_id, salary_amount, salary_date) VALUES (3, 3, 65000, TO_DATE('2024-03-29', 'YYYY-MM-DD'));

-- Inserting into COSTI_Prezenta Table
INSERT INTO COSTI_Prezenta (attendance_id, emp_id, attendance_date, attendance_status) VALUES (1, 1, TO_DATE('2024-03-28', 'YYYY-MM-DD'), 'Prezent');
INSERT INTO COSTI_Prezenta (attendance_id, emp_id, attendance_date, attendance_status) VALUES (2, 2, TO_DATE('2024-03-28', 'YYYY-MM-DD'), 'Prezent');
INSERT INTO COSTI_Prezenta (attendance_id, emp_id, attendance_date, attendance_status) VALUES (3, 3, TO_DATE('2024-03-28', 'YYYY-MM-DD'), 'Absent');

INSERT INTO COSTI_Departamente (dept_id, dept_name, dept_location) VALUES (4, 'Facilities', 'Bucuresti');
INSERT INTO COSTI_Angajati (emp_id, emp_name, emp_email, emp_phone, emp_address, emp_department_id) VALUES (4, 'Costi Roman', 'costi@s.utm.ro', '1112223333', 'Strada Mihai Bravu 79, Bucuresti', 3);
INSERT INTO COSTI_Salarii (salary_id, emp_id, salary_amount, salary_date) VALUES (4, 4, 66000, TO_DATE('2024-03-01', 'YYYY-MM-DD'));
INSERT INTO COSTI_Prezenta (attendance_id, emp_id, attendance_date, attendance_status) VALUES (4, 4, TO_DATE('2024-03-28', 'YYYY-MM-DD'), 'Prezent');

--DROP TABLE COSTI_Prezenta;
--DROP TABLE COSTI_Salarii;
--DROP TABLE COSTI_Angajati;
--DROP TABLE COSTI_Departamente;

-- Utilizarea operatorilor de comparatie
SELECT emp_name, salary_amount
FROM COSTI_Angajati ang
JOIN COSTI_Salarii sal ON ang.emp_id = sal.emp_id
WHERE salary_amount > 60000;

-- Utilizarea functiilor de grup si conditii asupra acestora
-- Selecteaza media salariului pentru fiecare departament cu media salariala mai mare de 55000
SELECT d.dept_name, AVG(s.salary_amount) AS avg_salary
FROM COSTI_Angajati a
JOIN COSTI_Departamente d ON a.emp_department_id = d.dept_id
JOIN COSTI_Salarii s ON a.emp_id = s.emp_id
GROUP BY d.dept_name
HAVING AVG(s.salary_amount) > 55000;

-- Utilizarea functiilor numerice si de tip caracter pentru data si timp
-- Selecteaza numele angajatului si data angajarii in formatul 'DD-MON-YYYY'
SELECT emp_id, salary_amount, TO_CHAR(salary_date, 'DD-MON-YYYY') AS sal_date_formatted
FROM COSTI_Salarii;

-- Utilizarea operatorilor CASE, DECODE, NVL, NVL2, COALESCE:
-- Selecteaza numele angajatului si categorizeaza salariul ca 'Mare', 'Mediu' sau 'Mic'
SELECT a.emp_name,
       CASE
           WHEN salary_amount > 50000 THEN 'Mare'
           WHEN salary_amount > 30000 THEN 'Mediu'
           ELSE 'Mic'
       END AS salary_amount
FROM COSTI_Angajati a
LEFT JOIN COSTI_Salarii s ON a.emp_id = s.emp_id;

-- Jonctiuni
-- Selecteaza numele angajatului si numele departamentului sau prin jonctiunea tabelelor COSTI_Angajati si COSTI_Departamente
SELECT a.emp_name, d.dept_name
FROM COSTI_Angajati a
JOIN COSTI_Departamente d ON a.emp_department_id = d.dept_id;

-- Subcereri (cereri imbricate)
-- Selecteaza angajatii care apartin departamentului 'Finance'
SELECT emp_name
FROM COSTI_Angajati
WHERE emp_department_id IN (SELECT dept_id FROM COSTI_Departamente WHERE dept_name = 'Finance');

-- Utilizarea operatorilor de tip set
-- Union: Selecteaza numele angajatilor din ambele tabele COSTI_Angajati si COSTI_Prezenta
SELECT emp_id FROM COSTI_Angajati
UNION
SELECT emp_id FROM COSTI_Prezenta;

-- Intersect: Selecteaza numele angajatilor prezenti in ambele tabele COSTI_Angajati si COSTI_Prezenta
SELECT emp_id FROM COSTI_Angajati
INTERSECT
SELECT emp_id FROM COSTI_Prezenta;

-- Except: Selecteaza numele angajatilor din COSTI_Angajati care nu sunt in COSTI_Prezenta
SELECT emp_id FROM COSTI_Angajati
MINUS
SELECT emp_id FROM COSTI_Prezenta;

-- Comenzile de stergere a tabeelor
/*
DROP TABLE COSTI_Prezenta;
DROP TABLE COSTI_Salarii;
DROP TABLE COSTI_Angajati;
DROP TABLE COSTI_Departamente;
*/
