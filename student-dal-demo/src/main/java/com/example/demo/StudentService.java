package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repository;

    // Find by department
    public List<Student> getByDepartment(String dept) {
        return repository.findByDepartment(dept);
    }

    // Find by age > given
    public List<Student> getByAge(int age) {
        return repository.findByAgeGreaterThan(age);
    }

    // Pagination + sorting
    public Page<Student> getByDepartmentWithPagination(String dept, int page, int size) {
        return repository.findByDepartment(dept, PageRequest.of(page, size));
    }
}