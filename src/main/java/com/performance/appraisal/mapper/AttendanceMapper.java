package com.performance.appraisal.mapper;

import com.performance.appraisal.dto.AttendanceRecordDTO;
import com.performance.appraisal.dto.AttendanceSummaryDTO;
import com.performance.appraisal.entity.EmpAttendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceMapper {

    EmpAttendance selectById(@Param("id") Long id);

    List<AttendanceRecordDTO> selectByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<AttendanceRecordDTO> selectExceptionByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    AttendanceSummaryDTO selectSummaryByEmployeeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    int insert(EmpAttendance attendance);

    int update(EmpAttendance attendance);
}