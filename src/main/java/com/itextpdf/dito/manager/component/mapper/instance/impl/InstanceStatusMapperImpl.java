package com.itextpdf.dito.manager.component.mapper.instance.impl;

import com.itextpdf.dito.manager.component.mapper.instance.InstanceStatusMapper;
import com.itextpdf.dito.manager.dto.instance.InstanceSummaryStatusDTO;
import com.itextpdf.dito.manager.model.instance.InstanceSummaryStatusModel;
import org.springframework.stereotype.Component;

@Component
public class InstanceStatusMapperImpl implements InstanceStatusMapper {

    @Override
    public InstanceSummaryStatusDTO map(final InstanceSummaryStatusModel data) {
        final InstanceSummaryStatusDTO status = new InstanceSummaryStatusDTO();
        status.setCount(data.getCount());
        status.setNeedAttention(data.getNeedAttention());
        return status;
    }

}
