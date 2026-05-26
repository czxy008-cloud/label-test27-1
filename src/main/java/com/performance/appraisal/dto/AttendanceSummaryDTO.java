package com.performance.appraisal.dto;

import lombok.Data;

@Data
public class AttendanceSummaryDTO {

    private Integer totalDays;

    private Integer normalDays;

    private Integer lateCount;

    private Integer earlyLeaveCount;

    private Integer absentCount;

    private Integer leaveCount;

    private Integer exceptionTotal;
}