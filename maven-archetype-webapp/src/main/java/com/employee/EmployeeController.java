package com.employee;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {

    @GetMapping("/employees")
    public String showEmployees(Model model) {

        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(new Employee(1, "Alice", "HR"));
        employeeList.add(new Employee(2, "Bob", "Engineering"));
        employeeList.add(new Employee(3, "Charlie", "Finance"));

        model.addAttribute("employees", employeeList);
        model.addAttribute("title", "TASK 8 (VTU24918)");

        return "employee";
    }
}