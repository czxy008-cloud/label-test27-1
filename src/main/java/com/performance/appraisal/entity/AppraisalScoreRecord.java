package com.performance.appraisal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppraisalScoreRecord {

    private Long id;

    private Long templateId;

    private Long employeeId;

    private Long indicatorId;

    private BigDecimal selfScore;

    private String selfComment;

    private LocalDateTime selfSubmitTime;

    private Long supervisorId;

    private BigDecimal supervisorScore;

    private String supervisorComment;

    private LocalDateTime supervisorSubmitTime;

    private BigDecimal finalScore;

    private String scoreStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
