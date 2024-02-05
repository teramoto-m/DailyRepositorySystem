package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
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
        
        //日付重複チェック
        if (findById(report.getId()).getEmployee().equals(userDetail.getEmployee()) && !(findById(report.getId()).getReportDate().equals(null))) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        report.setEmployee(userDetail.getEmployee());
        
        report.setDeleteFlg(false);
        
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
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
