package com.performance.appraisal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceRecordDTO {

    private Long id;

    private Long employeeId;

    private LocalDate attendanceDate;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    private String status;

    private String remark;
}