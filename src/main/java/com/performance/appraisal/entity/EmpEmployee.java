package com.performance.appraisal.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmpEmployee {

    private Long id;

    private String empNo;

    private String empName;

    private String password;

    private Integer gender;

    private String phone;

    private String email;

    private Long deptId;

    private String position;

    private String jobLevel;

    private LocalDate hireDate;

    private Integer empType;

    private Long supervisorId;

    private String roleCode;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;
}
