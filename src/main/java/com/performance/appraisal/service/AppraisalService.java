package com.performance.appraisal.service;

import com.performance.appraisal.dto.SelfEvaluationDTO;
import com.performance.appraisal.dto.SupervisorEvaluationDTO;
import com.performance.appraisal.entity.AppraisalScoreRecord;

import java.util.List;

public interface AppraisalService {

    void submitSelfEvaluation(SelfEvaluationDTO dto);

    void submitSupervisorEvaluation(SupervisorEvaluationDTO dto);

    List<AppraisalScoreRecord> getScoreRecords(Long templateId, Long employeeId);

    void generateResult(Long templateId, Long employeeId);

    void generateAllResults(Long templateId);

    void confirmResult(Long templateId);
}
