package com.performance.appraisal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.performance.appraisal.common.ResultCode;
import com.performance.appraisal.dto.ResultSummaryDTO;
import com.performance.appraisal.dto.TrendDTO;
import com.performance.appraisal.entity.AppraisalResult;
import com.performance.appraisal.entity.AppraisalTemplate;
import com.performance.appraisal.entity.EmpEmployee;
import com.performance.appraisal.mapper.ResultMapper;
import com.performance.appraisal.mapper.TemplateMapper;
import com.performance.appraisal.service.EmployeeService;
import com.performance.appraisal.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResultServiceImpl implements ResultService {

    @Autowired
    private ResultMapper resultMapper;

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private EmployeeService employeeService;

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
