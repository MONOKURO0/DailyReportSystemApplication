// tagHattoriWork
package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }


    // 日報一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        // 管理者権限判定用の文字列を用意する
        String roleAdmin = "ADMIN";

        System.out.println("ログイン権限:"+userDetail.getEmployee().getRole());

        if( userDetail.getEmployee().getRole().toString().equals(roleAdmin)) {
            System.out.println("ADMIN権限処理");

            // work_20240310:従業員削除後に、日報一覧を表示しようとすると、日報の削除フラグはセットされているが
            //               削除した従業員に対しても従業員idでのリスト検索をかけようとしてエラーが発生している？

            // 先にメソッドの呼び出しを行う（同じメソッドを複数回呼び出すことを防止するため）
            List<Report> repsAdmin = reportService.findAll();
            //List<Report> repsAdmin = reportService.findByDeleteFlg();

            System.out.println("ADMIN権限処理数："+repsAdmin.size()); // test

            model.addAttribute("reportList", repsAdmin);
            model.addAttribute("listSize", repsAdmin.size());
        }else {
            System.out.println("GENERAL権限処理");

            // 先にメソッドの呼び出しを行う（同じメソッドを複数回呼び出すことを防止するため）
            //List<Report> repsGeneral = reportService.findByCode(userDetail);
            List<Report> repsGeneral = reportService.findByEmployee(userDetail.getEmployee());

            // 取得インスタンスを複数回利用
            model.addAttribute("reportList", repsGeneral);
            model.addAttribute("listSize", repsGeneral.size());
        }

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("report", reportService.findById(id));

        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report) {
    //public String create() {
        //public String report_create() {
        System.out.println("日報新規登録 Get");

        return "reports/new";
    }

    // tagHattoriWork
    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    //public String edit(@PathVariable Integer id, Model model) {
    public String edit(@PathVariable Integer id, Model model) {
        System.out.println("日報更新 Get");

        model.addAttribute("report", reportService.findById(id));

        return "reports/update";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    //public String add(@Validated Report report, @AuthenticationPrincipal UserDetail userDetail, BindingResult res, Model model) {
    public String add(@Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        System.out.println("日報新規登録 Post");

        // 入力チェック
        if (res.hasErrors()) {
            System.out.println("日報新規登録 入力エラー");

            return create(report);
            //return "reports/new";
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = reportService.save(report, userDetail);
            //ErrorKinds result = reportService.save(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(report);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(report);
        }

        return "redirect:/reports";
    }

    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        //ErrorKinds result = reportService.delete(id, userDetail);
        ErrorKinds result = reportService.delete(id);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return detail(id, model);
        }

        return "redirect:/reports";
    }

    // 日報更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@Validated Report report, BindingResult res, @PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetail userDetail ) {

        System.out.println("日報更新 Post");

        // 入力チェック
        if (res.hasErrors()) {
            System.out.println("日報更新 hasErrors");
            // work_20240309:入力エラーを表示できる状態で返せない
            //              report.employee.name(というよりreport.employee?)がnullになっているっぽい
            // 20240310:解決済み

            //model.addAttribute("report", reportService.findById(id)); reportService.findById(id).getEmployee()
            //model.addAttribute("report.employee", reportService.findById(id).getEmployee());
            //model.addAttribute("report.employee", "テストだよ！");
            report.setEmployee(reportService.findById(id).getEmployee());

            //return edit(id, model);
            return "reports/update";
        }

        try {
            ErrorKinds result = reportService.update(report, id, reportService.findById(id).getEmployee());

            if (ErrorMessage.contains(result)) {
                System.out.println("日報更新 ErrorMessage");
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return edit(id, model);
            }

        } catch (DataIntegrityViolationException e) {
            // 想定外の問題が発生した場合、従業員更新画面に戻す。
            System.out.println("日報更新 想定外");
            return edit(id, model);
        }

        System.out.println("日報更新 redirect");
        return "redirect:/reports";
    }

}
