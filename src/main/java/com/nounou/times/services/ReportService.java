package com.nounou.times.services;

import com.nounou.times.model.Report;
import com.nounou.times.model.Schedule;
import com.nounou.times.model.User;
import com.nounou.times.repository.ReportRepository;
import com.nounou.times.repository.ScheduleRepository;
import com.nounou.times.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ReportService {

    @Inject
    ReportRepository reportRepository;

    @Inject
    ScheduleRepository scheduleRepository;

    @Inject
    UserRepository userRepository;

    public List<Report> getReportsByParent(Long parentId) {
        return reportRepository.findByParentId(parentId);
    }

    @Transactional
    public Report generateReport(Long parentId, Long nounouId, LocalDate month) {
        User parent = userRepository.findById(parentId);
        User nounou = userRepository.findById(nounouId);
        if (parent != null && nounou != null) {
            List<Schedule> schedules = scheduleRepository.findByParentIdAndNounouIdAndMonth(parentId, nounouId, month);
            double totalPayment = calculateTotalPayment(schedules); // Helper method to calculate total payment
            Report report = new Report();
            report.setParent(parent);
            report.setNounou(nounou);
            report.setMonth(month);
            report.setSchedules(schedules);
            report.setTotalPayment(totalPayment);
            reportRepository.persist(report);
            return report;
        }
        return null;
    }

    // getReportsByParentId method
    public List<Report> getReportsByParentId(Long parentId) {
        return reportRepository.findByParentId(parentId);
    }

    // createReport method
    public Report createReport(Report report) {
        reportRepository.persist(report);
        return report;
    }

    // updateReport method
    public Report updateReport(Long id, Report report) {
        Report existingReport = reportRepository.findById(id);
        if (existingReport != null) {
            existingReport.setMonth(report.getMonth());
            existingReport.setSchedules(report.getSchedules());
            existingReport.setTotalPayment(report.getTotalPayment());
            return existingReport;
        }
        return null;
    }
     //deleteReport method
    @Transactional
    public boolean deleteReport(Long id) {
        return reportRepository.deleteById(id);
    }

    private double calculateTotalPayment(List<Schedule> schedules) {
        // Implement payment calculation logic
        return 0;
    }
}
