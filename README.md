# Employee Management System - Spring Boot Application

## Overview
This Spring Boot application provides a comprehensive employee management system with departments, employees, and projects. The Day 12 update introduces advanced features including inheritance mapping and specialized view projections for enhanced data representation.

## Features
- **CRUD Operations**: Full create, read, update, delete for departments and employees
- **Entity Relationships**: 
  - Departments → Employees (One-to-Many)
  - Employees ↔ Projects (Many-to-Many)
- **Inheritance Mapping**: `Person` superclass with `@MappedSuperclass`
- **View Projections**: Specialized `ViewDepartment` with employee count
- **Custom Queries**: Advanced JPQL queries for data retrieval
- **RESTful API**: Proper HTTP methods and status codes
- **Error Handling**: Global exception handling with custom exceptions


## Project Structure
```
src/main/java
├── com/example/emp
│   ├── controllers
│   │   ├── DepartmentController.java
│   │   └── EmployeeController.java
│   ├── models
│   │   ├── Department.java
│   │   ├── Employee.java
│   │   ├── Person.java
│   │   ├── Project.java
│   │   └── ViewDepartment.java
│   ├── repositories
│   │   ├── DepartmentRepo.java
│   │   ├── EmployeeRepo.java
│   │   └── ProjectRepo.java
│   └── services
│       ├── DepartmentService.java
│       └── EmployeeService.java
└── EmpApplication.java

```

## Getting Started


### Prerequisites
- Java 17
- MySQL 8+
- Maven

### Installation
1. Clone the repository:
```bash
git clone https://github.com/your-repo/sampleapp.git
cd sampleapp
```

2. Create and configure database:
```bash
mysql -u root -p
CREATE DATABASE emp_db;
```

3. Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/emp_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

4. Build and run:
```bash
mvn spring-boot:run
```

## API Endpoints

### Department Endpoints
| Method | Endpoint               | Description                     |
|--------|------------------------|---------------------------------|
| GET    | `/dept`                | Get all departments             |
| GET    | `/dept/{id}`           | Get department by ID            |
| POST   | `/dept`                | Create new department           |
| PUT    | `/dept/{id}`           | Update department               |
| DELETE | `/dept/{id}`           | Delete department               |
| GET    | `/dept/names`          | Get department names            |
| GET    | `/dept/names/{name}`   | Search departments by name      |
| GET    | `/dept/vempcount/{id}` | Get department view with count  |

### Employee Endpoints
| Method | Endpoint               | Description                     |
|--------|------------------------|---------------------------------|
| GET    | `/emp`                 | Get all employees               |
| GET    | `/emp/{id}`            | Get employee by ID              |
| POST   | `/emp`                 | Create new employee             |
| PUT    | `/emp/{id}`            | Update employee                 |
| DELETE | `/emp/{id}`            | Delete employee                 |

## Entity Models

### Person (MappedSuperclass)
```java
@MappedSuperclass
public class Person {
    private String name;
    private int age;
    private String gender;
}
```

### Employee (Extends Person)
```java
@Entity
public class Employee extends Person {
    @Id
    private String empNo;
    private double salary;
    
    @ManyToOne
    private Department department;
    
    @ManyToMany(mappedBy = "employees")
    private List<Project> projects;
}
```

### Department
```java
@Entity
public class Department {
    @Id
    @Column(name = "dept_id")
    private int id;
    private String name;
    private Date established;
    
    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
}
```

### ViewDepartment Projection
```java
public class ViewDepartment extends Department {
    private int empcount;
    
    public ViewDepartment(int id, String name, Date established, int employees) {
        super(id, name, established);
        this.empcount = employees;
    }
}
```

## Repository Queries

### Department Repository
```java
public interface DepartmentRepo extends JpaRepository<Department, Integer> {
    @Query("SELECT COUNT(e) FROM Department d JOIN d.employees e WHERE d.id = ?1")
    int numberOfEmp(int departmentId);
    
    @Query("SELECT d.name FROM Department d")
    List<String> getDeptNames();
}
```

## Service Layer

### Department Service
```java
@Service
public class DepartmentService {
    // Get employee count as integer
    public int getEmpCount(int id) {
        if(repo.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Department Not Found");
        }
        return repo.numberOfEmp(id);
    }
    
    // Get department view with employee count
    public ViewDepartment getEmpCountView(int id) {
        Department dept = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Department Not Found"));
            
        return new ViewDepartment(
            dept.getId(),
            dept.getName(),
            dept.getEstablished(),
            getEmpCount(id)
        );
    }
}
```

### Employee Service
```java
@Service
public class EmployeeService {
    public String addEmp(Employee employee) {
        if (repo.findById(employee.getEmpNo()).isPresent()) {
            throw new DuplicateKeyException("Employee ID already exists");
        }
        
        // Validate department exists
        if (!departmentRepo.existsById(employee.getDepartment().getId())) {
            throw new EntityNotFoundException("Department not found");
        }
        
        repo.save(employee);
        return "Employee created successfully";
    }
}
```

## Controller Implementations

### Department Controller
```java
@RestController
@RequestMapping("/dept")
public class DepartmentController {
    @GetMapping("/vempcount/{id}")
    public ResponseEntity<ViewDepartment> getDepartmentView(
            @PathVariable int id) {
        return ResponseEntity.ok(service.getEmpCountView(id));
    }
    
    @PostMapping
    public ResponseEntity<String> createDepartment(
            @RequestBody Department department) {
        return new ResponseEntity<>(
            service.addDept(department), 
            HttpStatus.CREATED
        );
    }
}
```

### Employee Controller
```java
@RestController
@RequestMapping("/emp")
public class EmployeeController {
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(
            @PathVariable String id) {
        return ResponseEntity.ok(service.getEmp(id));
    }
    
    @PostMapping
    public ResponseEntity<String> createEmployee(
            @RequestBody Employee employee) {
        return new ResponseEntity<>(
            service.addEmp(employee),
            HttpStatus.CREATED
        );
    }
}
```

## Exception Handling
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicate(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
```
