package com.performance.appraisal.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class EmpAttendance {

    private Long id;

    private Long employeeId;

    private LocalDate attendanceDate;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    private String status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}