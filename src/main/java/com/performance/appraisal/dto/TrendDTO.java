package com.performance.appraisal.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrendDTO {

    private String cycleValue;

    private BigDecimal finalTotalScore;

    private String appraisalGrade;

    private BigDecimal selfTotalScore;

    private BigDecimal supervisorTotalScore;
}
