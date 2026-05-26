package com.performance.appraisal.service.impl;

import com.performance.appraisal.common.ResultCode;
import com.performance.appraisal.dto.AttendanceRecordDTO;
import com.performance.appraisal.dto.AttendanceSummaryDTO;
import com.performance.appraisal.entity.EmpEmployee;
import com.performance.appraisal.mapper.AttendanceMapper;
import com.performance.appraisal.service.AttendanceService;
import com.performance.appraisal.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public AttendanceSummaryDTO getAttendanceSummary(Long employeeId, LocalDate startDate, LocalDate endDate) {
        validateEmployee(employeeId);
        AttendanceSummaryDTO summary = attendanceMapper.selectSummaryByEmployeeAndDateRange(employeeId, startDate, endDate);
        if (summary == null) {
            summary = new AttendanceSummaryDTO();
            summary.setTotalDays(0);
            summary.setNormalDays(0);
            summary.setLateCount(0);
            summary.setEarlyLeaveCount(0);
            summary.setAbsentCount(0);
            summary.setLeaveCount(0);
            summary.setExceptionTotal(0);
        }
        return summary;
    }

    @Override
    public AttendanceSummaryDTO getAttendanceSummaryByCycle(Long employeeId, String appraisalCycle, String cycleValue) {
        validateEmployee(employeeId);
        LocalDate[] dateRange = calculateCycleDateRange(appraisalCycle, cycleValue);
        if (dateRange == null) {
            return null;
        }
        return getAttendanceSummary(employeeId, dateRange[0], dateRange[1]);
    }

    @Override
    public List<AttendanceRecordDTO> getAttendanceRecords(Long employeeId, LocalDate startDate, LocalDate endDate) {
        validateEmployee(employeeId);
        List<AttendanceRecordDTO> records = attendanceMapper.selectByEmployeeAndDateRange(employeeId, startDate, endDate);
        return records != null ? records : Collections.emptyList();
    }

    @Override
    public List<AttendanceRecordDTO> getAttendanceRecordsByCycle(Long employeeId, String appraisalCycle, String cycleValue) {
        validateEmployee(employeeId);
        LocalDate[] dateRange = calculateCycleDateRange(appraisalCycle, cycleValue);
        if (dateRange == null) {
            return Collections.emptyList();
        }
        return getAttendanceRecords(employeeId, dateRange[0], dateRange[1]);
    }

    @Override
    public List<AttendanceRecordDTO> getAttendanceExceptions(Long employeeId, LocalDate startDate, LocalDate endDate) {
        validateEmployee(employeeId);
        List<AttendanceRecordDTO> records = attendanceMapper.selectExceptionByEmployeeAndDateRange(employeeId, startDate, endDate);
        return records != null ? records : Collections.emptyList();
    }

    @Override
    public List<AttendanceRecordDTO> getAttendanceExceptionsByCycle(Long employeeId, String appraisalCycle, String cycleValue) {
        validateEmployee(employeeId);
        LocalDate[] dateRange = calculateCycleDateRange(appraisalCycle, cycleValue);
        if (dateRange == null) {
            return Collections.emptyList();
        }
        return getAttendanceExceptions(employeeId, dateRange[0], dateRange[1]);
    }

    private void validateEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new RuntimeException(ResultCode.PARAM_ERROR.getMessage());
        }
        EmpEmployee employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new RuntimeException(ResultCode.EMPLOYEE_NOT_FOUND.getMessage());
        }
    }

    private LocalDate[] calculateCycleDateRange(String appraisalCycle, String cycleValue) {
        if (appraisalCycle == null || cycleValue == null) {
            return null;
        }

        try {
            switch (appraisalCycle) {
                case "monthly":
                    YearMonth yearMonth = YearMonth.parse(cycleValue);
                    return new LocalDate[]{yearMonth.atDay(1), yearMonth.atEndOfMonth()};
                case "quarterly":
                    String[] parts = cycleValue.split("-Q");
                    if (parts.length == 2) {
                        int year = Integer.parseInt(parts[0]);
                        int quarter = Integer.parseInt(parts[1]);
                        int startMonth = (quarter - 1) * 3 + 1;
                        int endMonth = startMonth + 2;
                        LocalDate start = LocalDate.of(year, startMonth, 1);
                        LocalDate end = LocalDate.of(year, endMonth, start.lengthOfMonth());
                        return new LocalDate[]{start, end};
                    }
                    break;
                case "half_year":
                    String[] hyParts = cycleValue.split("-H");
                    if (hyParts.length == 2) {
                        int year = Integer.parseInt(hyParts[0]);
                        int half = Integer.parseInt(hyParts[1]);
                        if (half == 1) {
                            return new LocalDate[]{LocalDate.of(year, 1, 1), LocalDate.of(year, 6, 30)};
                        } else if (half == 2) {
                            return new LocalDate[]{LocalDate.of(year, 7, 1), LocalDate.of(year, 12, 31)};
                        }
                    }
                    break;
                case "yearly":
                    int year = Integer.parseInt(cycleValue);
                    return new LocalDate[]{LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31)};
                default:
                    break;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}