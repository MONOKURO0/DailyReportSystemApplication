// tagHattoriWork
package com.techacademy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    List<Report> findByEmployee(Employee employee);
    List<Report> findByDeleteFlg(boolean deleteFlg);
}