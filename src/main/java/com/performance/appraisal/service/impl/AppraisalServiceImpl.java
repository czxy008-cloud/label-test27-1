package com.performance.appraisal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.performance.appraisal.common.ResultCode;
import com.performance.appraisal.dto.SelfEvaluationDTO;
import com.performance.appraisal.dto.SupervisorEvaluationDTO;
import com.performance.appraisal.entity.AppraisalIndicator;
import com.performance.appraisal.entity.AppraisalResult;
import com.performance.appraisal.entity.AppraisalScoreRecord;
import com.performance.appraisal.entity.AppraisalTemplate;
import com.performance.appraisal.entity.EmpEmployee;
import com.performance.appraisal.mapper.IndicatorMapper;
import com.performance.appraisal.mapper.ResultMapper;
import com.performance.appraisal.mapper.ScoreRecordMapper;
import com.performance.appraisal.mapper.TemplateMapper;
import com.performance.appraisal.service.AppraisalService;
import com.performance.appraisal.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppraisalServiceImpl implements AppraisalService {

    @Autowired
    private ScoreRecordMapper scoreRecordMapper;

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private IndicatorMapper indicatorMapper;

    @Autowired
    private ResultMapper resultMapper;

    @Autowired
    private EmployeeService employeeService;

    @Override
    @Transactional
    public void submitSelfEvaluation(SelfEvaluationDTO dto) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        AppraisalTemplate template = templateMapper.selectById(dto.getTemplateId());
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }
        LocalDateTime now = LocalDateTime.now();
        if (template.getSelfStartTime() != null && now.isBefore(template.getSelfStartTime())) {
            throw new RuntimeException(ResultCode.NOT_IN_SELF_PERIOD.getMessage());
        }
        if (template.getSelfEndTime() != null && now.isAfter(template.getSelfEndTime())) {
            throw new RuntimeException(ResultCode.NOT_IN_SELF_PERIOD.getMessage());
        }

        for (SelfEvaluationDTO.ScoreItemDTO item : dto.getScoreItems()) {
            AppraisalScoreRecord record = scoreRecordMapper
                    .selectByTemplateEmployeeAndIndicator(dto.getTemplateId(), currentUserId, item.getIndicatorId());

            if (record == null) {
                record = new AppraisalScoreRecord();
                record.setTemplateId(dto.getTemplateId());
                record.setEmployeeId(currentUserId);
                record.setIndicatorId(item.getIndicatorId());
                record.setSelfScore(item.getSelfScore());
                record.setSelfComment(item.getSelfComment());
                record.setSelfSubmitTime(now);
                record.setScoreStatus("self_done");
                scoreRecordMapper.insert(record);
            } else {
                record.setSelfScore(item.getSelfScore());
                record.setSelfComment(item.getSelfComment());
                record.setSelfSubmitTime(now);
                record.setScoreStatus("self_done");
                scoreRecordMapper.updateSelfScore(record);
            }
        }
    }

    @Override
    @Transactional
    public void submitSupervisorEvaluation(SupervisorEvaluationDTO dto) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        AppraisalTemplate template = templateMapper.selectById(dto.getTemplateId());
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }
        LocalDateTime now = LocalDateTime.now();
        if (template.getSupervisorStartTime() != null && now.isBefore(template.getSupervisorStartTime())) {
            throw new RuntimeException(ResultCode.NOT_IN_SUPERVISOR_PERIOD.getMessage());
        }
        if (template.getSupervisorEndTime() != null && now.isAfter(template.getSupervisorEndTime())) {
            throw new RuntimeException(ResultCode.NOT_IN_SUPERVISOR_PERIOD.getMessage());
        }

        EmpEmployee employee = employeeService.getById(dto.getEmployeeId());
        if (employee.getSupervisorId() == null || !employee.getSupervisorId().equals(currentUserId)) {
            throw new RuntimeException(ResultCode.FORBIDDEN.getMessage());
        }

        for (SupervisorEvaluationDTO.ScoreItemDTO item : dto.getScoreItems()) {
            AppraisalScoreRecord record = scoreRecordMapper
                    .selectByTemplateEmployeeAndIndicator(dto.getTemplateId(), dto.getEmployeeId(), item.getIndicatorId());

            if (record == null) {
                record = new AppraisalScoreRecord();
                record.setTemplateId(dto.getTemplateId());
                record.setEmployeeId(dto.getEmployeeId());
                record.setIndicatorId(item.getIndicatorId());
                record.setSupervisorId(currentUserId);
                record.setSupervisorScore(item.getSupervisorScore());
                record.setSupervisorComment(item.getSupervisorComment());
                record.setSupervisorSubmitTime(now);
                record.setScoreStatus("supervisor_done");
                scoreRecordMapper.insert(record);
            } else {
                record.setSupervisorId(currentUserId);
                record.setSupervisorScore(item.getSupervisorScore());
                record.setSupervisorComment(item.getSupervisorComment());
                record.setSupervisorSubmitTime(now);
                record.setScoreStatus("supervisor_done");
                scoreRecordMapper.updateSupervisorScore(record);
            }
        }
    }

    @Override
    public List<AppraisalScoreRecord> getScoreRecords(Long templateId, Long employeeId) {
        return scoreRecordMapper.selectByTemplateAndEmployee(templateId, employeeId);
    }

    @Override
    @Transactional
    public void generateResult(Long templateId, Long employeeId) {
        AppraisalTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }

        List<AppraisalScoreRecord> records = scoreRecordMapper.selectByTemplateAndEmployee(templateId, employeeId);
        if (records.isEmpty()) {
            return;
        }

        BigDecimal selfWeight = template.getSelfWeight() != null ? template.getSelfWeight() : BigDecimal.valueOf(40);
        BigDecimal supervisorWeight = BigDecimal.valueOf(100).subtract(selfWeight);

        BigDecimal selfTotal = BigDecimal.ZERO;
        BigDecimal supervisorTotal = BigDecimal.ZERO;

        for (AppraisalScoreRecord record : records) {
            AppraisalIndicator indicator = indicatorMapper.selectById(record.getIndicatorId());
            if (indicator == null || indicator.getParentId() != null) {
                continue;
            }

            BigDecimal indicatorWeight = indicator.getWeight() != null ? indicator.getWeight() : BigDecimal.ZERO;
            indicatorWeight = indicatorWeight.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

            if (record.getSelfScore() != null) {
                selfTotal = selfTotal.add(record.getSelfScore().multiply(indicatorWeight));
            }
            if (record.getSupervisorScore() != null) {
                supervisorTotal = supervisorTotal.add(record.getSupervisorScore().multiply(indicatorWeight));
            }

            BigDecimal finalScore = BigDecimal.ZERO;
            if (record.getSelfScore() != null && record.getSupervisorScore() != null) {
                finalScore = record.getSelfScore()
                        .multiply(selfWeight.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                        .add(record.getSupervisorScore()
                                .multiply(supervisorWeight.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
            } else if (record.getSelfScore() != null) {
                finalScore = record.getSelfScore();
            } else if (record.getSupervisorScore() != null) {
                finalScore = record.getSupervisorScore();
            }
            scoreRecordMapper.updateFinalScore(record.getId(), finalScore);
        }

        selfTotal = selfTotal.setScale(2, RoundingMode.HALF_UP);
        supervisorTotal = supervisorTotal.setScale(2, RoundingMode.HALF_UP);

        BigDecimal finalTotal = selfTotal.multiply(selfWeight.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                .add(supervisorTotal.multiply(supervisorWeight.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                .setScale(2, RoundingMode.HALF_UP);

        AppraisalResult existing = resultMapper.selectByTemplateAndEmployee(templateId, employeeId);
        if (existing != null) {
            existing.setSelfTotalScore(selfTotal);
            existing.setSupervisorTotalScore(supervisorTotal);
            existing.setFinalTotalScore(finalTotal);
            existing.setAppraisalGrade(calculateGrade(finalTotal));
            existing.setResultStatus("confirmed");
            resultMapper.update(existing);
        } else {
            AppraisalResult result = new AppraisalResult();
            result.setTemplateId(templateId);
            result.setEmployeeId(employeeId);
            result.setDeptId(employeeService.getById(employeeId).getDeptId());
            result.setSelfTotalScore(selfTotal);
            result.setSupervisorTotalScore(supervisorTotal);
            result.setFinalTotalScore(finalTotal);
            result.setAppraisalGrade(calculateGrade(finalTotal));
            result.setResultStatus("confirmed");
            result.setConfirmStatus(0);
            resultMapper.insert(result);
        }
    }

    private String calculateGrade(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(90)) >= 0) return "S";
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) return "A";
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) return "B";
        if (score.compareTo(BigDecimal.valueOf(60)) >= 0) return "C";
        return "D";
    }

    @Override
    public void generateAllResults(Long templateId) {
        List<EmpEmployee> employees = employeeService.list(null, null, null)
                .stream()
                .map(dto -> {
                    EmpEmployee emp = new EmpEmployee();
                    emp.setId(dto.getId());
                    emp.setDeptId(dto.getDeptId());
                    return emp;
                })
                .toList();

        for (EmpEmployee emp : employees) {
            generateResult(templateId, emp.getId());
        }
    }

    @Override
    public void confirmResult(Long templateId) {
        AppraisalTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }
        template.setTemplateStatus("completed");
        templateMapper.update(template);
    }
}
