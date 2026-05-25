package com.performance.appraisal.service.impl;

import com.performance.appraisal.common.ResultCode;
import com.performance.appraisal.dto.IndicatorDTO;
import com.performance.appraisal.dto.TemplateDTO;
import com.performance.appraisal.entity.AppraisalIndicator;
import com.performance.appraisal.entity.AppraisalTemplate;
import com.performance.appraisal.mapper.IndicatorMapper;
import com.performance.appraisal.mapper.TemplateMapper;
import com.performance.appraisal.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private IndicatorMapper indicatorMapper;

    @Override
    public AppraisalTemplate getById(Long id) {
        AppraisalTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }
        return template;
    }

    @Override
    public List<TemplateDTO> list(String templateName, String templateStatus, String appraisalCycle) {
        return templateMapper.selectList(templateName, templateStatus, appraisalCycle);
    }

    @Override
    @Transactional
    public void create(TemplateDTO dto) {
        AppraisalTemplate template = new AppraisalTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setAppraisalCycle(dto.getAppraisalCycle());
        template.setCycleValue(dto.getCycleValue());
        template.setApplicableDeptId(dto.getApplicableDeptId());
        template.setSelfWeight(dto.getSelfWeight());
        template.setSelfStartTime(dto.getSelfStartTime());
        template.setSelfEndTime(dto.getSelfEndTime());
        template.setSupervisorStartTime(dto.getSupervisorStartTime());
        template.setSupervisorEndTime(dto.getSupervisorEndTime());
        template.setPublishStartTime(dto.getPublishStartTime());
        template.setPublishEndTime(dto.getPublishEndTime());
        template.setTemplateStatus("draft");
        template.setDescription(dto.getDescription());
        template.setCreatedBy(1L);
        templateMapper.insert(template);

        if (dto.getIndicators() != null && !dto.getIndicators().isEmpty()) {
            saveIndicators(template.getId(), dto.getIndicators());
        }
    }

    @Override
    @Transactional
    public void update(TemplateDTO dto) {
        AppraisalTemplate template = templateMapper.selectById(dto.getId());
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }
        if (dto.getTemplateName() != null) {
            template.setTemplateName(dto.getTemplateName());
        }
        if (dto.getAppraisalCycle() != null) {
            template.setAppraisalCycle(dto.getAppraisalCycle());
        }
        if (dto.getCycleValue() != null) {
            template.setCycleValue(dto.getCycleValue());
        }
        if (dto.getApplicableDeptId() != null) {
            template.setApplicableDeptId(dto.getApplicableDeptId());
        }
        if (dto.getSelfWeight() != null) {
            template.setSelfWeight(dto.getSelfWeight());
        }
        if (dto.getSelfStartTime() != null) {
            template.setSelfStartTime(dto.getSelfStartTime());
        }
        if (dto.getSelfEndTime() != null) {
            template.setSelfEndTime(dto.getSelfEndTime());
        }
        if (dto.getSupervisorStartTime() != null) {
            template.setSupervisorStartTime(dto.getSupervisorStartTime());
        }
        if (dto.getSupervisorEndTime() != null) {
            template.setSupervisorEndTime(dto.getSupervisorEndTime());
        }
        if (dto.getPublishStartTime() != null) {
            template.setPublishStartTime(dto.getPublishStartTime());
        }
        if (dto.getPublishEndTime() != null) {
            template.setPublishEndTime(dto.getPublishEndTime());
        }
        if (dto.getDescription() != null) {
            template.setDescription(dto.getDescription());
        }
        templateMapper.update(template);

        if (dto.getIndicators() != null) {
            indicatorMapper.deleteByTemplateId(dto.getId());
            saveIndicators(dto.getId(), dto.getIndicators());
        }
    }

    @Override
    public void delete(Long id) {
        AppraisalTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }
        templateMapper.deleteById(id);
        indicatorMapper.deleteByTemplateId(id);
    }

    @Override
    public void activate(Long id) {
        AppraisalTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }
        template.setTemplateStatus("active");
        templateMapper.update(template);
    }

    @Override
    public void close(Long id) {
        AppraisalTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new RuntimeException(ResultCode.TEMPLATE_NOT_FOUND.getMessage());
        }
        template.setTemplateStatus("closed");
        templateMapper.update(template);
    }

    @Override
    public List<AppraisalTemplate> listActiveTemplates() {
        return templateMapper.selectActiveTemplates();
    }

    @Override
    public List<IndicatorDTO> getIndicatorTree(Long templateId) {
        return indicatorMapper.selectTreeByTemplateId(templateId);
    }

    @Override
    public void saveIndicators(Long templateId, List<IndicatorDTO> indicators) {
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (IndicatorDTO indicator : indicators) {
            totalWeight = totalWeight.add(indicator.getWeight() != null ? indicator.getWeight() : BigDecimal.ZERO);
        }
        if (totalWeight.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new RuntimeException(ResultCode.INDICATOR_WEIGHT_ERROR.getMessage());
        }

        for (IndicatorDTO dto : indicators) {
            AppraisalIndicator indicator = new AppraisalIndicator();
            indicator.setTemplateId(templateId);
            indicator.setParentId(null);
            indicator.setIndicatorName(dto.getIndicatorName());
            indicator.setIndicatorCode(dto.getIndicatorCode());
            indicator.setIndicatorType(dto.getIndicatorType());
            indicator.setWeight(dto.getWeight());
            indicator.setMaxScore(dto.getMaxScore() != null ? dto.getMaxScore() : BigDecimal.valueOf(100));
            indicator.setScoringCriteria(dto.getScoringCriteria());
            indicator.setDescription(dto.getDescription());
            indicator.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
            indicator.setStatus(1);
            indicatorMapper.insert(indicator);

            if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
                BigDecimal childTotalWeight = BigDecimal.ZERO;
                for (IndicatorDTO child : dto.getChildren()) {
                    childTotalWeight = childTotalWeight.add(child.getWeight() != null ? child.getWeight() : BigDecimal.ZERO);
                }
                if (childTotalWeight.compareTo(BigDecimal.valueOf(100)) != 0) {
                    throw new RuntimeException(ResultCode.INDICATOR_WEIGHT_ERROR.getMessage());
                }
                for (IndicatorDTO child : dto.getChildren()) {
                    AppraisalIndicator childIndicator = new AppraisalIndicator();
                    childIndicator.setTemplateId(templateId);
                    childIndicator.setParentId(indicator.getId());
                    childIndicator.setIndicatorName(child.getIndicatorName());
                    childIndicator.setIndicatorCode(child.getIndicatorCode());
                    childIndicator.setIndicatorType(child.getIndicatorType());
                    childIndicator.setWeight(child.getWeight());
                    childIndicator.setMaxScore(child.getMaxScore() != null ? child.getMaxScore() : BigDecimal.valueOf(100));
                    childIndicator.setScoringCriteria(child.getScoringCriteria());
                    childIndicator.setDescription(child.getDescription());
                    childIndicator.setSortOrder(child.getSortOrder() != null ? child.getSortOrder() : 0);
                    childIndicator.setStatus(1);
                    indicatorMapper.insert(childIndicator);
                }
            }
        }
    }
}
