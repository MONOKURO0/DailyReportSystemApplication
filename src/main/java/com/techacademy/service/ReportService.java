// tagHattoriWork

package com.techacademy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    //private final PasswordEncoder passwordEncoder;

    @Autowired
    /*
    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }
    */
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findByCode(String code) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(code);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }


}
