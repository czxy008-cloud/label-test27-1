package com.performance.appraisal.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.performance.appraisal.common.Result;
import com.performance.appraisal.dto.AttendanceRecordDTO;
import com.performance.appraisal.dto.AttendanceSummaryDTO;
import com.performance.appraisal.dto.ResultSummaryDTO;
import com.performance.appraisal.dto.SupervisorEvaluationDetailDTO;
import com.performance.appraisal.dto.TrendDTO;
import com.performance.appraisal.service.AttendanceService;
import com.performance.appraisal.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/{id}")
    public Result<ResultSummaryDTO> getById(@PathVariable Long id) {
        return Result.success(resultService.getDetailById(id));
    }

    @GetMapping("/my-result")
    public Result<ResultSummaryDTO> getMyResult(@RequestParam Long templateId,
                                                @RequestParam Long employeeId) {
        return Result.success(resultService.getByTemplateAndEmployee(templateId, employeeId));
    }

    @SaCheckRole("admin")
    @GetMapping("/summary")
    public Result<List<ResultSummaryDTO>> listSummary(
            @RequestParam(required = false) Long templateId,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String appraisalGrade,
            @RequestParam(required = false) String resultStatus) {
        return Result.success(resultService.listSummary(templateId, deptId, appraisalGrade, resultStatus));
    }

    @GetMapping("/published")
    public Result<List<ResultSummaryDTO>> listPublished(
            @RequestParam(required = false) Long templateId,
            @RequestParam(required = false) Long deptId) {
        return Result.success(resultService.listPublished(templateId, deptId));
    }

    @GetMapping("/trend")
    public Result<List<TrendDTO>> getTrend(
            @RequestParam Long employeeId,
            @RequestParam(required = false) String startCycle,
            @RequestParam(required = false) String endCycle) {
        return Result.success(resultService.getTrend(employeeId, startCycle, endCycle));
    }

    @GetMapping("/supervisor-detail")
    public Result<SupervisorEvaluationDetailDTO> getSupervisorEvaluationDetail(
            @RequestParam Long templateId,
            @RequestParam Long employeeId) {
        return Result.success(resultService.getSupervisorEvaluationDetail(templateId, employeeId));
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirmResult(@PathVariable Long id) {
        resultService.confirmResult(id);
        return Result.success("确认成功", null);
    }

    @PostMapping("/{id}/appeal")
    public Result<Void> appeal(@PathVariable Long id,
                                @RequestParam String reason) {
        resultService.appeal(id, reason);
        return Result.success("申诉已提交", null);
    }

    @SaCheckRole("admin")
    @PostMapping("/publish")
    public Result<Void> publish(@RequestParam Long templateId) {
        resultService.publish(templateId);
        return Result.success("公示成功", null);
    }

    @GetMapping("/attendance/summary")
    public Result<AttendanceSummaryDTO> getAttendanceSummaryByCycle(
            @RequestParam Long employeeId,
            @RequestParam String appraisalCycle,
            @RequestParam String cycleValue) {
        return Result.success(attendanceService.getAttendanceSummaryByCycle(employeeId, appraisalCycle, cycleValue));
    }

    @GetMapping("/attendance/records")
    public Result<List<AttendanceRecordDTO>> getAttendanceRecordsByCycle(
            @RequestParam Long employeeId,
            @RequestParam String appraisalCycle,
            @RequestParam String cycleValue) {
        return Result.success(attendanceService.getAttendanceRecordsByCycle(employeeId, appraisalCycle, cycleValue));
    }

    @GetMapping("/attendance/exceptions")
    public Result<List<AttendanceRecordDTO>> getAttendanceExceptionsByCycle(
            @RequestParam Long employeeId,
            @RequestParam String appraisalCycle,
            @RequestParam String cycleValue) {
        return Result.success(attendanceService.getAttendanceExceptionsByCycle(employeeId, appraisalCycle, cycleValue));
    }
}
