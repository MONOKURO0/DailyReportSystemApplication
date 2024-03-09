// tagHattoriWork
package com.techacademy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

@Repository
//public interface ReportRepository extends JpaRepository<Employee, String> {
public interface ReportRepository extends JpaRepository<Report, String> {
    // 2024/03/06_work
    List<Report> findByEmployee(Employee employee);
    //List<Report> RfindByEmployee(Employee employee);
}