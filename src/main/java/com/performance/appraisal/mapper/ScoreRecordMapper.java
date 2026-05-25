package com.performance.appraisal.mapper;

import com.performance.appraisal.entity.AppraisalScoreRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScoreRecordMapper {

    AppraisalScoreRecord selectById(@Param("id") Long id);

    List<AppraisalScoreRecord> selectByTemplateAndEmployee(@Param("templateId") Long templateId,
                                                            @Param("employeeId") Long employeeId);

    AppraisalScoreRecord selectByTemplateEmployeeAndIndicator(@Param("templateId") Long templateId,
                                                               @Param("employeeId") Long employeeId,
                                                               @Param("indicatorId") Long indicatorId);

    int insert(AppraisalScoreRecord record);

    int update(AppraisalScoreRecord record);

    int updateSelfScore(AppraisalScoreRecord record);

    int updateSupervisorScore(AppraisalScoreRecord record);

    int updateFinalScore(@Param("id") Long id,
                          @Param("finalScore") java.math.BigDecimal finalScore);

    int deleteByTemplateAndEmployee(@Param("templateId") Long templateId,
                                     @Param("employeeId") Long employeeId);

    List<AppraisalScoreRecord> selectByTemplateId(@Param("templateId") Long templateId);
}
