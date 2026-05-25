package com.performance.appraisal.mapper;

import com.performance.appraisal.entity.EmpEmployee;
import com.performance.appraisal.dto.EmployeeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    EmpEmployee selectById(@Param("id") Long id);

    EmpEmployee selectByEmpNo(@Param("empNo") String empNo);

    List<EmployeeDTO> selectList(@Param("empName") String empName,
                                  @Param("deptId") Long deptId,
                                  @Param("roleCode") String roleCode);

    int insert(EmpEmployee employee);

    int update(EmpEmployee employee);

    int deleteById(@Param("id") Long id);

    List<EmpEmployee> selectSubordinates(@Param("supervisorId") Long supervisorId);

    EmployeeDTO selectDetailById(@Param("id") Long id);
}
