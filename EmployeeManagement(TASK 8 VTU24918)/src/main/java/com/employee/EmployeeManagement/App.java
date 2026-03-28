package com.employee.EmployeeManagement;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.employee.EmployeeManagement")
public class App {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(App.class);

        EmployeeService service = context.getBean(EmployeeService.class);

        // Add employees
        service.addEmployee(1, "Alice", "HR");
        service.addEmployee(2, "Bob", "Engineering");
        service.addEmployee(3, "Charlie", "Finance");

        // Display all
        service.displayAllEmployees();

        // Search by ID
        service.displayEmployeeById(2);

        // Delete an employee
        service.deleteEmployee(1);

        // Display all after deletion
        service.displayAllEmployees();

        context.close();
    }
}