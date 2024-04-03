import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

// Employee class representing an employee
class Employee {
    private String name; // Private attribute for employee's name
    private String lastName; // Private attribute for employee's last name
    private int age; // Private attribute for employee's age

    // Constructor for Employee class
    public Employee(String name, String lastName, int age) {
        this.name = name; // Assigning name
        this.lastName = lastName; // Assigning last name
        this.age = age; // Assigning age
    }

    // Getters for Employee attributes
    public String getName() {
        return name; // Returning name
    }

    public String getLastName() {
        return lastName; // Returning last name
    }

    public int getAge() {
        return age; // Returning age
    }

    @Override
    public String toString() {
        return "Employee{" + // Returning string representation of Employee object
                "name='" + name + '\'' + // Concatenating name
                ", lastName='" + lastName + '\'' + // Concatenating last name
                ", age=" + age + // Concatenating age
                '}';
    }
}

// EmployeeService interface defining operations related to managing employees
interface EmployeeService {
    void addEmployee(Employee employee); // Add employee method
    void removeEmployee(String lastName); // Remove employee method
    List<Employee> getAllEmployees(); // Get all employees method
}

// EmployeeServiceImpl class implementing EmployeeService interface
class EmployeeServiceImpl implements EmployeeService {
    private Map<String, Employee> employeeMap; // Using map to store employees by last name

    // Constructor for EmployeeServiceImpl class
    public EmployeeServiceImpl() {
        this.employeeMap = new HashMap<>(); // Initializing employee map
    }

    @Override
    public void addEmployee(Employee employee) {
        employeeMap.put(employee.getLastName(), employee); // Adding employee to the map
        AuditService.logAction("Employee added", employee, new Date()); // Logging the action
    }

    @Override
    public void removeEmployee(String lastName) {
        Employee removedEmployee = employeeMap.remove(lastName); // Removing employee from the map
        if (removedEmployee != null) {
            AuditService.logAction("Employee removed", removedEmployee, new Date()); // Logging the action
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values()); // Getting all employees from the map
    }
}

// FileStorage singleton class for reading and writing from/to files
class FileStorage {
    private static FileStorage instance;

    // Private constructor for singleton pattern
    private FileStorage() {
    }

    // Method to get the instance of FileStorage
    public static FileStorage getInstance() {
        if (instance == null) {
            instance = new FileStorage(); // Creating instance if it doesn't exist
        }
        return instance; // Returning instance
    }

    // Method to write employees to file
    public void writeToFile(List<Employee> employees, String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) { // Try-with-resources to automatically close resources
            for (Employee employee : employees) {
                writer.println(employee.getName() + "," + employee.getLastName() + "," + employee.getAge()); // Writing employee details to file
            }
        }
    }

    // Method to read employees from file
    public List<Employee> readFromFile(String fileName) throws IOException {
        List<Employee> employees = new ArrayList<>(); // Creating list to store employees
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) { // Try-with-resources to automatically close resources
            String line;
            while ((line = reader.readLine()) != null) { // Reading each line from file
                String[] parts = line.split(","); // Splitting line by comma
                employees.add(new Employee(parts[0], parts[1], Integer.parseInt(parts[2]))); // Creating Employee object and adding to list
            }
        }
        return employees; // Returning list of employees
    }
}

// AuditService class for logging actions to a CSV file
class AuditService {
    private static final String AUDIT_LOG_FILE = "audit_log.csv"; // File name for audit log
    private static final String APPLICATION_NAME = "EmployeeManagementApp"; // Application name for logging

    private static AuditService instance; // Singleton instance

    // Private constructor for singleton pattern
    private AuditService() {
    }

    // Method to get the instance of AuditService
    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService(); // Creating instance if it doesn't exist
        }
        return instance; // Returning instance
    }

    // Method to log actions with timestamp
    public static void logAction(String action, Employee employee, Date timestamp) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(AUDIT_LOG_FILE, true))) { // Try-with-resources to automatically close resources
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Date format for timestamp
            String logEntry = "[" + dateFormat.format(timestamp) + "] [" + APPLICATION_NAME + "] " +
                    action + " " + (employee != null ? "Employee: " + employee.getName() + " " + employee.getLastName() + ", " + employee.getAge() : ""); // Log entry format
            writer.println(logEntry); // Writing log entry to file
        } catch (IOException e) {
            System.out.println("Error logging action: " + e.getMessage()); // Handling logging error
        }
    }

    // Method to log failed login attempt with actual password
    public static void logLoginFailed(String username, String password) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(AUDIT_LOG_FILE, true))) { // Try-with-resources to automatically close resources
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Date format for timestamp
            String logEntry = "[" + dateFormat.format(new Date()) + "] [" + APPLICATION_NAME + "] " +
                    "Login failed for username: " + username + ", password: " + password; // Log entry format
            writer.println(logEntry); // Writing log entry to file
        } catch (IOException e) {
            System.out.println("Error logging action: " + e.getMessage()); // Handling logging error
        }
    }
}

// Main class for the application
public class Main {
    private static final String USERNAME = "user1"; // Static username
    private static final String PASSWORD = "password1"; // Static password

    public static void main(String[] args) {
        // Adding shutdown hook to log program shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AuditService.logAction("Program shutdown", null, new Date()); // Logging program shutdown
        }));

        AuditService.logAction("Program started", null, new Date()); // Log program start

        Scanner scanner = new Scanner(System.in); // Creating scanner object for user input
        EmployeeService employeeService = new EmployeeServiceImpl(); // Creating EmployeeService instance
        FileStorage fileStorage = FileStorage.getInstance(); // Creating FileStorage instance
        String fileName = "employees.csv"; // File name for storing employees

        // Authenticate user
        System.out.println("Welcome to the Employee Management System."); // Printing welcome message
        System.out.println("Please log in to continue."); // Printing login prompt
        System.out.print("Username: "); // Printing username prompt
        String username = scanner.nextLine(); // Reading username input
        System.out.print("Password: "); // Printing password prompt
        String password = readPassword(); // Reading password securely

        if (!authenticate(username, password)) { // Checking if username and password are correct
            System.out.println("Invalid username or password. Exiting program."); // Printing error message
            AuditService.logLoginFailed(username, password); // Logging failed login attempt
            System.exit(1); // Exiting program
        }

        AuditService.logAction("User logged in with username: " + username, null, new Date()); // Logging user login

        // Load data from file if it exists
        try {
            File usersFile = new File(fileName); // Creating file object for users file
            if (usersFile.exists() && !usersFile.isDirectory()) { // Checking if users file exists
                // Load data from file
                List<Employee> employees = fileStorage.readFromFile(fileName); // Reading employees from file
                for (Employee employee : employees) {
                    employeeService.addEmployee(employee); // Adding employees to service
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data from file: " + e.getMessage()); // Handling file loading error
        }

        while (true) {
            System.out.println("Choose an option:"); // Printing menu options
            System.out.println("1. Add employee");
            System.out.println("2. Remove employee");
            System.out.println("3. View all employees");
            System.out.println("4. Exit");

            int choice = scanner.nextInt(); // Reading user choice
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter name: "); // Printing prompt for name
                    String name = scanner.nextLine(); // Reading name input
                    System.out.print("Enter last name: "); // Printing prompt for last name
                    String lastName = scanner.nextLine(); // Reading last name input
                    System.out.print("Enter age: "); // Printing prompt for age
                    int age = scanner.nextInt(); // Reading age input
                    scanner.nextLine(); // Consume newline
                    Employee newEmployee = new Employee(name, lastName, age); // Creating new Employee object
                    employeeService.addEmployee(newEmployee); // Adding new employee
                    break;
                case 2:
                    System.out.print("Enter last name of employee to remove: "); // Printing prompt for last name
                    String removeLastName = scanner.nextLine(); // Reading last name to remove
                    employeeService.removeEmployee(removeLastName); // Removing employee
                    break;
                case 3:
                    List<Employee> allEmployees = employeeService.getAllEmployees(); // Getting all employees
                    for (Employee employee : allEmployees) {
                        System.out.println(employee); // Printing each employee
                    }
                    AuditService.logAction("Employee viewed", null, new Date()); // Logging employee viewed
                    break;
                case 4:
                    // Save data to file before exiting
                    try {
                        List<Employee> allEmps = employeeService.getAllEmployees(); // Getting all employees
                        fileStorage.writeToFile(allEmps, fileName); // Writing employees to file
                    } catch (IOException e) {
                        System.out.println("Error saving data to file: " + e.getMessage()); // Handling file saving error
                    }
                    AuditService.logAction("Program exited", null, new Date()); // Logging program exit
                    System.exit(0); // Exiting program
                default:
                    System.out.println("Invalid choice, please try again."); // Printing error message for invalid choice
            }
        }
    }

    // Method to authenticate user
    private static boolean authenticate(String username, String password) {
        return USERNAME.equals(username) && PASSWORD.equals(password); // Checking if username and password match
    }

    // Method to read password securely
    private static String readPassword() {
        Console console = System.console(); // Getting console instance
        if (console == null) { // Checking if console is available (running in IDE)
            Scanner scanner = new Scanner(System.in); // Creating scanner for input
            return scanner.nextLine(); // Reading password input
        }
        char[] passwordArray = console.readPassword(); // Reading password input securely
        return new String(passwordArray); // Converting password to string
    }
}
