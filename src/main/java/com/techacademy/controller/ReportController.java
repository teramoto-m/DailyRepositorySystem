package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
        
        // 自分の権限を取得
        String value = userDetail.getEmployee().getRole().getValue();
        
        if(value.equals("一般")){
            model.addAttribute("listSize", reportService.findByEmployee(userDetail.getEmployee()).size());
            model.addAttribute("reportList",reportService.findByEmployee(userDetail.getEmployee()));
        }else{
        
        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList", reportService.findAll());
        }
        return "reports/list";
    }
    
    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable("id") Integer id, Model model) {
        
        model.addAttribute("report", reportService.findById(id));
        model.addAttribute("employee", reportService.findById(id).getEmployee());
        
        return "reports/detail";
    }
    
    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report,@AuthenticationPrincipal UserDetail userDetail,Model model) {
        
        model.addAttribute("userName",userDetail.getUsername());
        
        return "reports/new";
    }
    
    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model,@AuthenticationPrincipal UserDetail userDetail) {
        //入力チェック
        if (res.hasErrors()) {
            return create(report,userDetail,model);
        }
        
        //日報登録
        ErrorKinds result = reportService.save(report,userDetail);
        
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report,userDetail,model);
        }
        
        return "redirect:/reports";
        
    }
    
    // 日報更新画面
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable("id") Integer id,Model model,Report report) {
        if(id != null) {
        model.addAttribute("report", reportService.findById(id));
        model.addAttribute("userName", reportService.findById(id).getEmployee().getName());
        }else {
            model.addAttribute("report",report);
            model.addAttribute("userName",report.getEmployee().getName());
        }
        
        return "reports/update";
    }
    
    //従業員更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@PathVariable("id") Integer id,@Validated Report report, BindingResult res, Model model) {
        
        report.setEmployee(reportService.findById(id).getEmployee());
        
        // 入力チェック
        if (res.hasErrors()) {
            return edit(null,model,report);
        }
        
        ErrorKinds result = reportService.update(id,report);
        
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return edit(null,model,report);
        }
        
        return "redirect:/reports";
    }
    
    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        reportService.delete(id);

        return "redirect:/reports";
    }
    
}
