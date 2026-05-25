package com.performance.appraisal.mapper;

import com.performance.appraisal.entity.AppraisalResult;
import com.performance.appraisal.dto.ResultSummaryDTO;
import com.performance.appraisal.dto.TrendDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResultMapper {

    AppraisalResult selectById(@Param("id") Long id);

    AppraisalResult selectByTemplateAndEmployee(@Param("templateId") Long templateId,
                                                 @Param("employeeId") Long employeeId);

    List<ResultSummaryDTO> selectSummaryList(@Param("templateId") Long templateId,
                                              @Param("deptId") Long deptId,
                                              @Param("appraisalGrade") String appraisalGrade,
                                              @Param("resultStatus") String resultStatus);

    List<ResultSummaryDTO> selectPublishedList(@Param("templateId") Long templateId,
                                                @Param("deptId") Long deptId);

    int insert(AppraisalResult result);

    int update(AppraisalResult result);

    int deleteByTemplateId(@Param("templateId") Long templateId);

    List<TrendDTO> selectTrendByEmployee(@Param("employeeId") Long employeeId,
                                          @Param("startCycle") String startCycle,
                                          @Param("endCycle") String endCycle);

    ResultSummaryDTO selectDetailById(@Param("id") Long id);
}
