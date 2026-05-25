package com.performance.appraisal.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.performance.appraisal.common.Result;
import com.performance.appraisal.dto.SelfEvaluationDTO;
import com.performance.appraisal.dto.SupervisorEvaluationDTO;
import com.performance.appraisal.entity.AppraisalScoreRecord;
import com.performance.appraisal.service.AppraisalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appraisal")
public class AppraisalController {

    @Autowired
    private AppraisalService appraisalService;

    @PostMapping("/self-evaluation")
    public Result<Void> submitSelfEvaluation(@Valid @RequestBody SelfEvaluationDTO dto) {
        appraisalService.submitSelfEvaluation(dto);
        return Result.success("自评提交成功", null);
    }

    @PostMapping("/supervisor-evaluation")
    public Result<Void> submitSupervisorEvaluation(@Valid @RequestBody SupervisorEvaluationDTO dto) {
        appraisalService.submitSupervisorEvaluation(dto);
        return Result.success("评分提交成功", null);
    }

    @GetMapping("/scores")
    public Result<List<AppraisalScoreRecord>> getScoreRecords(
            @RequestParam Long templateId,
            @RequestParam Long employeeId) {
        return Result.success(appraisalService.getScoreRecords(templateId, employeeId));
    }

    @SaCheckRole("admin")
    @PostMapping("/result/generate")
    public Result<Void> generateResult(@RequestParam Long templateId,
                                        @RequestParam Long employeeId) {
        appraisalService.generateResult(templateId, employeeId);
        return Result.success("结果生成成功", null);
    }

    @SaCheckRole("admin")
    @PostMapping("/result/generate-all")
    public Result<Void> generateAllResults(@RequestParam Long templateId) {
        appraisalService.generateAllResults(templateId);
        return Result.success("批量生成成功", null);
    }

    @SaCheckRole("admin")
    @PutMapping("/result/confirm")
    public Result<Void> confirmResult(@RequestParam Long templateId) {
        appraisalService.confirmResult(templateId);
        return Result.success("结果已确认", null);
    }
}
