package com.performance.appraisal.service;

import com.performance.appraisal.dto.IndicatorDTO;
import com.performance.appraisal.dto.TemplateDTO;
import com.performance.appraisal.entity.AppraisalTemplate;

import java.util.List;

public interface TemplateService {

    AppraisalTemplate getById(Long id);

    List<TemplateDTO> list(String templateName, String templateStatus, String appraisalCycle);

    void create(TemplateDTO dto);

    void update(TemplateDTO dto);

    void delete(Long id);

    void activate(Long id);

    void close(Long id);

    List<AppraisalTemplate> listActiveTemplates();

    List<IndicatorDTO> getIndicatorTree(Long templateId);

    void saveIndicators(Long templateId, List<IndicatorDTO> indicators);
}
