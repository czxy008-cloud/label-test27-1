package com.performance.appraisal.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysDepartment {

    private Long id;

    private String deptName;

    private String deptCode;

    private Long parentId;

    private Long managerId;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;
}
