package com.employee.EmployeeManagement;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public void addEmployee(int id, String name, String department) {
        Employee emp = new Employee(id, name, department);
        employeeRepository.addEmployee(emp);
    }

    public void displayAllEmployees() {
        List<Employee> list = employeeRepository.getAllEmployees();
        if (list.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            System.out.println("--- All Employees ---");
            for (Employee e : list) {
                System.out.println(e);
            }
        }
    }

    public void displayEmployeeById(int id) {
        Employee e = employeeRepository.getEmployeeById(id);
        if (e != null) {
            System.out.println("Found: " + e);
        } else {
            System.out.println("Employee with ID " + id + " not found.");
        }
    }

    public void deleteEmployee(int id) {
        employeeRepository.deleteEmployee(id);
    }
}