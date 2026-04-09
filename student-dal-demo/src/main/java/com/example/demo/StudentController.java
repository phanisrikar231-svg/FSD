package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService service;

    @GetMapping("/test")
    public String test() {
        return "Controller working";
    }

    @GetMapping("/department/{dept}")
    public List<Student> getByDepartment(@PathVariable String dept) {
        return service.getByDepartment(dept);
    }

    @GetMapping("/age/{age}")
    public List<Student> getByAge(@PathVariable int age) {
        return service.getByAge(age);
    }

    @GetMapping("/department/{dept}/page")
    public Page<Student> getByDepartmentWithPagination(
            @PathVariable String dept,
            @RequestParam int page,
            @RequestParam int size) {
        return service.getByDepartmentWithPagination(dept, page, size);
    }
}