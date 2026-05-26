package com.performance.appraisal.service;

import com.performance.appraisal.dto.ResultSummaryDTO;
import com.performance.appraisal.dto.SupervisorEvaluationDetailDTO;
import com.performance.appraisal.dto.TrendDTO;

import java.util.List;

public interface ResultService {

    ResultSummaryDTO getDetailById(Long id);

    ResultSummaryDTO getByTemplateAndEmployee(Long templateId, Long employeeId);

    List<ResultSummaryDTO> listSummary(Long templateId, Long deptId, String appraisalGrade, String resultStatus);

    List<ResultSummaryDTO> listPublished(Long templateId, Long deptId);

    List<TrendDTO> getTrend(Long employeeId, String startCycle, String endCycle);

    SupervisorEvaluationDetailDTO getSupervisorEvaluationDetail(Long templateId, Long employeeId);

    void confirmResult(Long id);

    void appeal(Long id, String reason);

    void publish(Long templateId);
}
