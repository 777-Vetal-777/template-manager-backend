package com.itextpdf.dito.manager.component.template.dtm;

import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.service.template.TemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DtmTemplateExportServiceTest extends AbstractDtmExtractorTest {
    @Autowired
    private TemplateService templateService;

    @Test
    void shouldExportStandardTemplate() throws Exception {
        final String templateName = "new-template";
        final TemplateEntity templateEntity = templateService.get(templateName);
        assertNotNull(templateEntity.getUuid());
        mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(templateName)))
                .andExpect(status().isOk())
                .andExpect(zipMatch(hasItem("meta.json"), hasItem("templates/" + templateEntity.getLatestFile().getUuid() + ".html")));
    }

    @Test
    void shouldExportLatestVersionOfTemplate() throws Exception {
        final String templateName = "new-template";
        final TemplateEntity templateEntity = templateService.get(templateName);
        assertNotNull(templateEntity.getUuid());
        mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(templateName))
                .queryParam("versions", "LATEST"))
                .andExpect(status().isOk())
                .andExpect(zipMatch(hasItem("meta.json"), hasItem("templates/" + templateEntity.getLatestFile().getUuid() + ".html")));
    }

    @Test
    void shouldExportCompositionTemplate() throws Exception {
        final String templateName = "composition_template2";
        final TemplateEntity templateEntity = templateService.get(templateName);
        assertNotNull(templateEntity.getUuid());
        mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(templateName))
                .queryParam("versions", "ALL"))
                .andExpect(status().isOk())
                .andExpect(zipMatch(hasItem("meta.json"), hasItem("templates/" + templateEntity.getLatestFile().getUuid() + ".html")));
    }

    @Test
    void shouldExportCompositionTemplateWithoutDependencies() throws Exception {
        final String templateName = "composition_template2";
        final TemplateEntity templateEntity = templateService.get(templateName);
        assertNotNull(templateEntity.getUuid());
        mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(templateName))
                .queryParam("versions", "LATEST")
                .queryParam("exportDependencies", "false"))
                .andExpect(status().isOk())
                .andExpect(zipMatch(hasItem("meta.json"), hasItem("templates/" + templateEntity.getLatestFile().getUuid() + ".html")));
    }

}
