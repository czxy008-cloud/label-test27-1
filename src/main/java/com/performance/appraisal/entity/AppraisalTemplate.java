package com.performance.appraisal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppraisalTemplate {

    private Long id;

    private String templateName;

    private String appraisalCycle;

    private String cycleValue;

    private Long applicableDeptId;

    private BigDecimal selfWeight;

    private LocalDateTime selfStartTime;

    private LocalDateTime selfEndTime;

    private LocalDateTime supervisorStartTime;

    private LocalDateTime supervisorEndTime;

    private LocalDateTime publishStartTime;

    private LocalDateTime publishEndTime;

    private String templateStatus;

    private String description;

    private Long createdBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;
}
