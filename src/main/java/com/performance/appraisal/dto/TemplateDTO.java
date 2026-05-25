package com.performance.appraisal.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TemplateDTO {

    private Long id;

    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @NotBlank(message = "考核周期不能为空")
    private String appraisalCycle;

    private String cycleValue;

    private Long applicableDeptId;

    @DecimalMin(value = "0", message = "自评权重不能小于0")
    @DecimalMax(value = "100", message = "自评权重不能大于100")
    private BigDecimal selfWeight;

    private LocalDateTime selfStartTime;

    private LocalDateTime selfEndTime;

    private LocalDateTime supervisorStartTime;

    private LocalDateTime supervisorEndTime;

    private LocalDateTime publishStartTime;

    private LocalDateTime publishEndTime;

    private String templateStatus;

    private String description;

    private List<IndicatorDTO> indicators;
}
