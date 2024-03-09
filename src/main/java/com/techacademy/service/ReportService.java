// tagHattoriWork
package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報保存
    @Transactional
    //public ErrorKinds save(Report report) {
    //public ErrorKinds save(Report report, Employee employee) {
    public ErrorKinds save(Report report, UserDetail userDetail) {

        System.out.println("日報保存");

        // 従業員番号重複チェック
        /*
        if (findByCode(employee.getCode()) != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }
        */

        report.setDeleteFlg(false);
        report.setEmployee(userDetail.getEmployee());
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id, UserDetail userDetail) {

        Report report = findById(id);

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }



    // 日報更新
    @Transactional
    //public ErrorKinds update(Report report, Integer id) {
    public ErrorKinds update(Report report, Integer id, Employee employee) {

        System.out.println("日報更新 update処理開始");

        // 従業員コードからパラメータ引継ぎ用employeeをひっぱってくる
        //Employee employee_old = findByCode(code);
        Report report_old = findById(id);

        System.out.println("日報更新 パラメータ before");
        System.out.println("/*******************************/");
        System.out.println("ID:"+report_old.getId());
        System.out.println("日付:"+report_old.getReportDate());
        System.out.println("タイトル:"+report_old.getTitle());
        System.out.println("内容:"+report_old.getContent());
        System.out.println("社員番号(従業員ID):"+employee.getCode());
        System.out.println("登録日時:"+report_old.getCreatedAt());
        System.out.println("更新日時:"+report_old.getUpdatedAt());
        System.out.println("/*******************************/");


        // パスワードチェック
/*
        if ( !("".equals(employee.getPassword())) ) {
            // パスワードが空白でない場合
            ErrorKinds result = employeePasswordCheck(employee);
            if (ErrorKinds.CHECK_OK != result) {
                return result;
            }
        } else {
            // パスワードが空白の場合
            // 前回の値を引き継ぐ
            employee.setPassword(employee_old.getPassword());
        }
*/

        report.setId(id);

        report.setEmployee(employee);

        report.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(report_old.getCreatedAt());
        report.setUpdatedAt(now);

        System.out.println("日報更新 パラメータ更新完了");
        System.out.println("/*******************************/");
        System.out.println("ID:"+report.getId());
        System.out.println("日付:"+report.getReportDate());
        System.out.println("タイトル:"+report.getTitle());
        System.out.println("内容:"+report.getContent());
        //System.out.println("社員番号(従業員ID):"+report.getEmployee());
        System.out.println("登録日時:"+report.getCreatedAt());
        System.out.println("更新日時:"+report.getUpdatedAt());
        System.out.println("/*******************************/");

        reportRepository.save(report);

        System.out.println("日報更新 save完了");

        return ErrorKinds.SUCCESS;
    }


    // 日報一覧表示処理
    //@PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 日報一覧表示処理
    @Transactional
    public List<Report> findCode(UserDetail userDetail) {
        //return reportRepository.RfindByEmployee(userDetail.getEmployee());
        //return reportRepository.findAll();
        //Optional<Report> option = reportRepository.findAllById(null)

        return userDetail.getEmployee().getReportList();
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id.toString());

        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // 指定した従業員の日報全てを検索
    // 2024/03/06_work
    //public Report findByCode(String code) {
    //public Report findByCode(Employee employee) {
    public Report findByCode(UserDetail userDetail) {

        // findByIdで検索
        //Optional<Report> option = reportRepository.findById(code);
        //Optional<Report> option = reportRepository.findByCode(userDetail.getEmployee().getCode());
        Optional<Report> option = reportRepository.findByEmployee(userDetail.getEmployee());

        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

}
