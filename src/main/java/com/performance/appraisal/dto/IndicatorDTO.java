package com.performance.appraisal.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class IndicatorDTO {

    private Long id;

    @NotBlank(message = "指标名称不能为空")
    private String indicatorName;

    private String indicatorCode;

    private String indicatorType;

    @DecimalMin(value = "0", message = "权重不能小于0")
    @DecimalMax(value = "100", message = "权重不能大于100")
    private BigDecimal weight;

    private BigDecimal maxScore;

    private String scoringCriteria;

    private String description;

    private Integer sortOrder;

    private List<IndicatorDTO> children;
}
