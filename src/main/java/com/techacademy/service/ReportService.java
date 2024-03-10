// tagHattoriWork
package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

        // 画面で表示中の従業員 かつ 入力した日付のエラーチェック
        // (ただし、画面で表示中の日報データは除く)
        ErrorKinds result = findByReportDate(report.getReportDate(), userDetail.getEmployee());
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

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
    //public ErrorKinds delete(Integer id, UserDetail userDetail) {
    public ErrorKinds delete(Integer id) {

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

        // 画面で表示中の従業員 かつ 入力した日付のエラーチェック
        // (ただし、画面で表示中の日報データは除く)
        ErrorKinds result = findByReportDate(report.getReportDate(), employee);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

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
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 日報一覧表示処理
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
    public List<Report> findByCode(UserDetail userDetail) {
        // findByEmployeeで検索
        List<Report> reps = reportRepository.findByEmployee(userDetail.getEmployee());

        return reps;
    }

    // 削除されていない日報全てを検索
    public List<Report> findByDeleteFlg() {
        // findByDeleteFlgで削除されていない日報を検索
        List<Report> reps = reportRepository.findByDeleteFlg(false);

        return reps;
    }

    // 指定した従業員の日報全てを検索する
    // test
    //private ErrorKinds findByReportDate(LocalDate checkReportDate, UserDetail userDetail) {
    private ErrorKinds findByReportDate(LocalDate checkReportDate, Employee employee) {
        // findByEmployeeで検索
        //List<Report> reps = reportRepository.findByEmployee(userDetail.getEmployee());
        List<Report> reps = reportRepository.findByEmployee(employee);

        // 日報のリスト（reportList）を拡張for文を使って繰り返し
        for (Report report : reps) {
            if(report.getReportDate().equals(checkReportDate)) {
                System.out.println("同一日付日報エラー");
                return ErrorKinds.DATECHECK_ERROR;
            }
        }

        return ErrorKinds.CHECK_OK;
    }

}
