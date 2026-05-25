package com.performance.appraisal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppraisalResult {

    private Long id;

    private Long templateId;

    private Long employeeId;

    private Long deptId;

    private BigDecimal selfTotalScore;

    private BigDecimal supervisorTotalScore;

    private BigDecimal finalTotalScore;

    private String appraisalGrade;

    private String gradeComment;

    private String overallComment;

    private String resultStatus;

    private Integer confirmStatus;

    private LocalDateTime confirmTime;

    private LocalDateTime publishStartTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
