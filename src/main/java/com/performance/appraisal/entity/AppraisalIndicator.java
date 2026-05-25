package com.performance.appraisal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppraisalIndicator {

    private Long id;

    private Long templateId;

    private Long parentId;

    private String indicatorName;

    private String indicatorCode;

    private String indicatorType;

    private BigDecimal weight;

    private BigDecimal maxScore;

    private String scoringCriteria;

    private String description;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
