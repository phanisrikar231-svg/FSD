package com.example.demo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find by department
    List<Student> findByDepartment(String department);

    // Find students older than age
    List<Student> findByAgeGreaterThan(int age);

    // Sorting + Pagination
    Page<Student> findByDepartment(String department, Pageable pageable);
}