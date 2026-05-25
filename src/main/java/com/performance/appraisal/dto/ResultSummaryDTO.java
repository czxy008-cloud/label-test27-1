package com.performance.appraisal.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResultSummaryDTO {

    private Long employeeId;

    private String empNo;

    private String empName;

    private Long deptId;

    private String deptName;

    private BigDecimal selfTotalScore;

    private BigDecimal supervisorTotalScore;

    private BigDecimal finalTotalScore;

    private String appraisalGrade;

    private String overallComment;

    private String resultStatus;

    private Integer confirmStatus;
}
