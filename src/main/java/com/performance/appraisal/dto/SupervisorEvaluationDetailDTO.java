package com.performance.appraisal.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SupervisorEvaluationDetailDTO {

    private Long templateId;

    private String templateName;

    private String cycleValue;

    private Long employeeId;

    private String empNo;

    private String empName;

    private Long deptId;

    private String deptName;

    private String position;

    private BigDecimal selfTotalScore;

    private BigDecimal supervisorTotalScore;

    private BigDecimal finalTotalScore;

    private String appraisalGrade;

    private String overallComment;

    private List<ScoreRecordDetailDTO> scoreRecords;

    private AttendanceSummaryDTO attendanceSummary;

    private List<AttendanceRecordDTO> attendanceRecords;

    @Data
    public static class ScoreRecordDetailDTO {

        private Long indicatorId;

        private String indicatorName;

        private String indicatorType;

        private BigDecimal weight;

        private BigDecimal maxScore;

        private BigDecimal selfScore;

        private String selfComment;

        private BigDecimal supervisorScore;

        private String supervisorComment;

        private BigDecimal finalScore;
    }
}