package com.techacademy.service;

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
    
    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }
    
    // 一般ユーザ日報一覧処理
    public List<Report> findByEmployee(UserDetail userDetail){
         return reportRepository.findByEmployee(userDetail.getEmployee());
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report,UserDetail userDetail) {
        
        // 引数であるuserDetailから ログインしている従業員情報を取得する 
        Employee employee = userDetail.getEmployee();

        // ReportResositoryを使って、ログインしている従業員が投稿した日報一覧を取得する
        List<Report> reports = reportRepository.findByEmployee(employee);

        // 上記日報一覧から、繰り返し構文で1つずつ日報を取り出して、その日報の投稿日と、引数の reportの持つ投稿日を比較する。
        // 同じ投稿日があれば、繰り返し構文の中で、return ErrorKinds.DATECHECK_ERROR;　を実行する
        for(Report record : reports){
           if(record.getReportDate().equals(report.getReportDate())){
               return ErrorKinds.DATECHECK_ERROR;
           }
        }
        // reportインスタンスのセッターを使って ログインした従業員インスタンスをセットする
        report.setEmployee(userDetail.getEmployee());
        
        report.setDeleteFlg(false);
        
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }
    
    // 日報削除
    @Transactional
    public void delete(Integer id) {
        
        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);
        
    }
    
    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }
}
