package com.performance.appraisal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.performance.appraisal.common.ResultCode;
import com.performance.appraisal.dto.*;
import com.performance.appraisal.entity.AppraisalIndicator;
import com.performance.appraisal.entity.AppraisalResult;
import com.performance.appraisal.entity.AppraisalScoreRecord;
import com.performance.appraisal.entity.AppraisalTemplate;
import com.performance.appraisal.entity.EmpEmployee;
import com.performance.appraisal.mapper.IndicatorMapper;
import com.performance.appraisal.mapper.ResultMapper;
import com.performance.appraisal.mapper.ScoreRecordMapper;
import com.performance.appraisal.mapper.TemplateMapper;
import com.performance.appraisal.service.AttendanceService;
import com.performance.appraisal.service.EmployeeService;
import com.performance.appraisal.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResultServiceImpl implements ResultService {

    @Autowired
    private ResultMapper resultMapper;

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ScoreRecordMapper scoreRecordMapper;

    @Autowired
    private IndicatorMapper indicatorMapper;

    @Autowired
    private AttendanceService attendanceService;

    @Override
    public ResultSummaryDTO getDetailById(Long id) {
        ResultSummaryDTO dto = resultMapper.selectDetailById(id);
        if (dto == null) {
            throw new RuntimeException(ResultCode.RESULT_NOT_FOUND.getMessage());
        }
        return dto;
    }

    @Override
    public ResultSummaryDTO getByTemplateAndEmployee(Long templateId, Long employeeId) {
        AppraisalResult result = resultMapper.selectByTemplateAndEmployee(templateId, employeeId);
        if (result == null) {
            return null;
        }
        return resultMapper.selectDetailById(result.getId());
    }

    @Override
    public List<ResultSummaryDTO> listSummary(Long templateId, Long deptId, String appraisalGrade, String resultStatus) {
        return resultMapper.selectSummaryList(templateId, deptId, appraisalGrade, resultStatus);
    }

    @Override
    public List<ResultSummaryDTO> listPublished(Long templateId, Long deptId) {
        return resultMapper.selectPublishedList(templateId, deptId);
    }

    @Override
    public List<TrendDTO> getTrend(Long employeeId, String startCycle, String endCycle) {
        return resultMapper.selectTrendByEmployee(employeeId, startCycle, endCycle);
    }

    @Override
    public SupervisorEvaluationDetailDTO getSupervisorEvaluationDetail(Long templateId, Long employeeId) {
        AppraisalTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }

        EmployeeDTO employeeDetail = employeeService.getDetailById(employeeId);
        if (employeeDetail == null) {
            throw new RuntimeException(ResultCode.EMPLOYEE_NOT_FOUND.getMessage());
        }

        AppraisalResult appraisalResult = resultMapper.selectByTemplateAndEmployee(templateId, employeeId);

        SupervisorEvaluationDetailDTO dto = new SupervisorEvaluationDetailDTO();
        dto.setTemplateId(templateId);
        dto.setTemplateName(template.getTemplateName());
        dto.setCycleValue(template.getCycleValue());

        dto.setEmployeeId(employeeId);
        dto.setEmpNo(employeeDetail.getEmpNo());
        dto.setEmpName(employeeDetail.getEmpName());
        dto.setDeptId(employeeDetail.getDeptId());
        dto.setDeptName(employeeDetail.getDeptName());
        dto.setPosition(employeeDetail.getPosition());

        if (appraisalResult != null) {
            dto.setSelfTotalScore(appraisalResult.getSelfTotalScore());
            dto.setSupervisorTotalScore(appraisalResult.getSupervisorTotalScore());
            dto.setFinalTotalScore(appraisalResult.getFinalTotalScore());
            dto.setAppraisalGrade(appraisalResult.getAppraisalGrade());
            dto.setOverallComment(appraisalResult.getOverallComment());
        }

        List<AppraisalScoreRecord> scoreRecords = scoreRecordMapper.selectByTemplateAndEmployee(templateId, employeeId);
        List<SupervisorEvaluationDetailDTO.ScoreRecordDetailDTO> scoreRecordDetails = new ArrayList<>();
        for (AppraisalScoreRecord record : scoreRecords) {
            AppraisalIndicator indicator = indicatorMapper.selectById(record.getIndicatorId());
            if (indicator == null) {
                continue;
            }
            SupervisorEvaluationDetailDTO.ScoreRecordDetailDTO detail = new SupervisorEvaluationDetailDTO.ScoreRecordDetailDTO();
            detail.setIndicatorId(indicator.getId());
            detail.setIndicatorName(indicator.getIndicatorName());
            detail.setIndicatorType(indicator.getIndicatorType());
            detail.setWeight(indicator.getWeight());
            detail.setMaxScore(indicator.getMaxScore());
            detail.setSelfScore(record.getSelfScore());
            detail.setSelfComment(record.getSelfComment());
            detail.setSupervisorScore(record.getSupervisorScore());
            detail.setSupervisorComment(record.getSupervisorComment());
            detail.setFinalScore(record.getFinalScore());
            scoreRecordDetails.add(detail);
        }
        dto.setScoreRecords(scoreRecordDetails);

        LocalDate[] dateRange = calculateCycleDateRange(template.getAppraisalCycle(), template.getCycleValue());
        if (dateRange != null) {
            AttendanceSummaryDTO attendanceSummary = attendanceService.getAttendanceSummary(
                    employeeId, dateRange[0], dateRange[1]);
            dto.setAttendanceSummary(attendanceSummary);

            List<AttendanceRecordDTO> attendanceRecords = attendanceService.getAttendanceExceptions(
                    employeeId, dateRange[0], dateRange[1]);
            dto.setAttendanceRecords(attendanceRecords);
        }

        return dto;
    }

    private LocalDate[] calculateCycleDateRange(String appraisalCycle, String cycleValue) {
        if (appraisalCycle == null || cycleValue == null) {
            return null;
        }

        try {
            switch (appraisalCycle) {
                case "monthly":
                    YearMonth yearMonth = YearMonth.parse(cycleValue);
                    return new LocalDate[]{yearMonth.atDay(1), yearMonth.atEndOfMonth()};
                case "quarterly":
                    String[] parts = cycleValue.split("-Q");
                    if (parts.length == 2) {
                        int year = Integer.parseInt(parts[0]);
                        int quarter = Integer.parseInt(parts[1]);
                        int startMonth = (quarter - 1) * 3 + 1;
                        int endMonth = startMonth + 2;
                        LocalDate start = LocalDate.of(year, startMonth, 1);
                        LocalDate end = LocalDate.of(year, endMonth, start.lengthOfMonth());
                        return new LocalDate[]{start, end};
                    }
                    break;
                case "yearly":
                    int year = Integer.parseInt(cycleValue);
                    return new LocalDate[]{LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31)};
                default:
                    break;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    @Transactional
    public void confirmResult(Long id) {
        AppraisalResult result = resultMapper.selectById(id);
        if (result == null) {
            throw new RuntimeException(ResultCode.RESULT_NOT_FOUND.getMessage());
        }
        if (result.getConfirmStatus() != null && result.getConfirmStatus() != 0) {
            throw new RuntimeException(ResultCode.ALREADY_CONFIRMED.getMessage());
        }
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (!result.getEmployeeId().equals(currentUserId)) {
            throw new RuntimeException(ResultCode.FORBIDDEN.getMessage());
        }
        result.setConfirmStatus(1);
        result.setConfirmTime(LocalDateTime.now());
        resultMapper.update(result);
    }

    @Override
    @Transactional
    public void appeal(Long id, String reason) {
        AppraisalResult result = resultMapper.selectById(id);
        if (result == null) {
            throw new RuntimeException(ResultCode.RESULT_NOT_FOUND.getMessage());
        }
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (!result.getEmployeeId().equals(currentUserId)) {
            throw new RuntimeException(ResultCode.FORBIDDEN.getMessage());
        }
        result.setConfirmStatus(2);
        resultMapper.update(result);

        com.performance.appraisal.entity.AppraisalAppeal appeal = new com.performance.appraisal.entity.AppraisalAppeal();
        appeal.setResultId(id);
        appeal.setAppellantId(currentUserId);
        appeal.setAppealReason(reason);
        appeal.setAppealStatus("pending");

        try {
            java.lang.reflect.Method method = resultMapper.getClass().getMethod("insertAppeal",
                    com.performance.appraisal.entity.AppraisalAppeal.class);
            method.invoke(resultMapper, appeal);
        } catch (Exception ignored) {
        }
    }

    @Override
    @Transactional
    public void publish(Long templateId) {
        AppraisalTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }
        List<ResultSummaryDTO> results = resultMapper.selectSummaryList(templateId, null, null, "confirmed");
        for (ResultSummaryDTO dto : results) {
            AppraisalResult result = resultMapper.selectByTemplateAndEmployee(templateId, dto.getEmployeeId());
            if (result != null) {
                result.setResultStatus("published");
                result.setPublishStartTime(LocalDateTime.now());
                resultMapper.update(result);
            }
        }
    }
}
