package com.performance.appraisal.service;

import com.performance.appraisal.dto.AttendanceRecordDTO;
import com.performance.appraisal.dto.AttendanceSummaryDTO;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceSummaryDTO getAttendanceSummary(Long employeeId, LocalDate startDate, LocalDate endDate);

    AttendanceSummaryDTO getAttendanceSummaryByCycle(Long employeeId, String appraisalCycle, String cycleValue);

    List<AttendanceRecordDTO> getAttendanceRecords(Long employeeId, LocalDate startDate, LocalDate endDate);

    List<AttendanceRecordDTO> getAttendanceRecordsByCycle(Long employeeId, String appraisalCycle, String cycleValue);

    List<AttendanceRecordDTO> getAttendanceExceptions(Long employeeId, LocalDate startDate, LocalDate endDate);

    List<AttendanceRecordDTO> getAttendanceExceptionsByCycle(Long employeeId, String appraisalCycle, String cycleValue);
}