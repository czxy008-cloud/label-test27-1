package com.performance.appraisal.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.performance.appraisal.common.Result;
import com.performance.appraisal.dto.EmployeeDTO;
import com.performance.appraisal.entity.EmpEmployee;
import com.performance.appraisal.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/{id}")
    public Result<EmployeeDTO> getById(@PathVariable Long id) {
        return Result.success(employeeService.getDetailById(id));
    }

    @GetMapping
    public Result<List<EmployeeDTO>> list(
            @RequestParam(required = false) String empName,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String roleCode) {
        return Result.success(employeeService.list(empName, deptId, roleCode));
    }

    @SaCheckRole("admin")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody EmployeeDTO dto) {
        employeeService.add(dto);
        return Result.success("添加成功", null);
    }

    @SaCheckRole("admin")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody EmployeeDTO dto) {
        dto.setId(id);
        employeeService.update(dto);
        return Result.success("更新成功", null);
    }

    @SaCheckRole("admin")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/subordinates")
    public Result<List<EmpEmployee>> listSubordinates(@RequestParam Long supervisorId) {
        return Result.success(employeeService.listSubordinates(supervisorId));
    }
}
