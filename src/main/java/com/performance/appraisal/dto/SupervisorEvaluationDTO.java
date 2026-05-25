package com.performance.appraisal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SupervisorEvaluationDTO {

    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    @NotNull(message = "被考核员工ID不能为空")
    private Long employeeId;

    @NotEmpty(message = "评分项不能为空")
    @Valid
    private List<ScoreItemDTO> scoreItems;

    private String overallComment;

    @Data
    public static class ScoreItemDTO {

        @NotNull(message = "指标ID不能为空")
        private Long indicatorId;

        private java.math.BigDecimal supervisorScore;

        private String supervisorComment;
    }
}
