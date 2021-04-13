package com.itextpdf.dito.manager.component.mapper.instance;

import com.itextpdf.dito.manager.dto.instance.InstanceSummaryStatusDTO;
import com.itextpdf.dito.manager.model.instance.InstanceSummaryStatusModel;

public interface InstanceStatusMapper {

    InstanceSummaryStatusDTO map(InstanceSummaryStatusModel data);

}
