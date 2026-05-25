package com.performance.appraisal.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeDTO {

    private Long id;

    @NotBlank(message = "员工工号不能为空")
    private String empNo;

    @NotBlank(message = "员工姓名不能为空")
    private String empName;

    private Integer gender;

    private String phone;

    private String email;

    @NotNull(message = "所属部门ID不能为空")
    private Long deptId;

    private String position;

    private String jobLevel;

    private LocalDate hireDate;

    private Integer empType;

    private Long supervisorId;

    private String roleCode;

    private String deptName;

    private String supervisorName;
}
