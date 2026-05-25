package com.performance.appraisal.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.performance.appraisal.common.Result;
import com.performance.appraisal.dto.IndicatorDTO;
import com.performance.appraisal.dto.TemplateDTO;
import com.performance.appraisal.entity.AppraisalTemplate;
import com.performance.appraisal.service.TemplateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping("/{id}")
    public Result<AppraisalTemplate> getById(@PathVariable Long id) {
        return Result.success(templateService.getById(id));
    }

    @GetMapping
    public Result<List<TemplateDTO>> list(
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) String templateStatus,
            @RequestParam(required = false) String appraisalCycle) {
        return Result.success(templateService.list(templateName, templateStatus, appraisalCycle));
    }

    @SaCheckRole("admin")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody TemplateDTO dto) {
        templateService.create(dto);
        return Result.success("创建成功", null);
    }

    @SaCheckRole("admin")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TemplateDTO dto) {
        dto.setId(id);
        templateService.update(dto);
        return Result.success("更新成功", null);
    }

    @SaCheckRole("admin")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return Result.success("删除成功", null);
    }

    @SaCheckRole("admin")
    @PutMapping("/{id}/activate")
    public Result<Void> activate(@PathVariable Long id) {
        templateService.activate(id);
        return Result.success("已启用", null);
    }

    @SaCheckRole("admin")
    @PutMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id) {
        templateService.close(id);
        return Result.success("已关闭", null);
    }

    @GetMapping("/active")
    public Result<List<AppraisalTemplate>> listActive() {
        return Result.success(templateService.listActiveTemplates());
    }

    @GetMapping("/{id}/indicators")
    public Result<List<IndicatorDTO>> getIndicatorTree(@PathVariable Long id) {
        return Result.success(templateService.getIndicatorTree(id));
    }

    @SaCheckRole("admin")
    @PostMapping("/{id}/indicators")
    public Result<Void> saveIndicators(@PathVariable Long id,
                                        @RequestBody List<IndicatorDTO> indicators) {
        templateService.saveIndicators(id, indicators);
        return Result.success("保存成功", null);
    }
}
