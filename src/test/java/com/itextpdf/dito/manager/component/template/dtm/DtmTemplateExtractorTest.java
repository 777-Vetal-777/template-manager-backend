package com.itextpdf.dito.manager.component.template.dtm;

import com.itextpdf.dito.manager.component.template.dtm.model.DtmModelExtractor;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmResourceDescriptorModel;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DtmTemplateExtractorTest extends AbstractDtmExtractorTest {

    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private DtmModelExtractor extractor;

    @ParameterizedTest
    @CsvSource({
            "some-template",
            "another-footer-template",
            "some-template-with-data-collection",
            "new-template"
    })
    void shouldExtractStandardTemplate(String templateName) throws Exception {
        final TemplateEntity templateEntity = templateRepository.findByName(templateName).orElseThrow();
        final DtmFileExportContext context = new DtmFileExportContext(templateEntity, true);
        final DtmFileDescriptorModel model = new DtmFileDescriptorModel("DTM2", "test-version");
        extractor.extract(context, model);
        assertEquals(1, model.getTemplates().size());
        model.getTemplates().forEach(dtmTemplateDescriptorModel -> {
            assertEquals(1, dtmTemplateDescriptorModel.getVersions().size());
            assertEquals(0, dtmTemplateDescriptorModel.getVersions().get(0).getUsedIn().size());
        });

        System.out.println("----------------");
        System.out.println(objectMapper.writeValueAsString(model));
        System.out.println("----------------");
    }

    @Test
    void shouldExtractCompositionTemplate() throws Exception {
        final TemplateEntity templateEntity = templateRepository.findByName("composite-template").orElseThrow();
        final DtmFileExportContext context = new DtmFileExportContext(templateEntity, true);
        final DtmFileDescriptorModel model = new DtmFileDescriptorModel("DTM2", "test-version");
        extractor.extract(context, model);
        assertEquals(4, model.getTemplates().size());
        model.getTemplates().forEach(dtmTemplateDescriptorModel -> {
            assertEquals(1, dtmTemplateDescriptorModel.getVersions().size());
            final int expected = TemplateTypeEnum.COMPOSITION.equals(dtmTemplateDescriptorModel.getType()) ? 0 : 1;
            assertEquals(expected, dtmTemplateDescriptorModel.getVersions().get(0).getUsedIn().size());
        });

        System.out.println("----------------");
        System.out.println(objectMapper.writeValueAsString(model));
        System.out.println("----------------");
    }

    @Test
    void shouldExtractTemplateWithResources() throws Exception {
        final TemplateEntity templateEntity = templateRepository.findByName("template-example").orElseThrow();
        final DtmFileExportContext context = new DtmFileExportContext(templateEntity, true);
        final DtmFileDescriptorModel model = new DtmFileDescriptorModel("DTM2", "test-version");
        extractor.extract(context, model);
        assertEquals(1, model.getTemplates().size());
        model.getTemplates().forEach(dtmTemplateDescriptorModel -> {
            assertEquals(1, dtmTemplateDescriptorModel.getVersions().size());
            assertEquals(0, dtmTemplateDescriptorModel.getVersions().get(0).getUsedIn().size());
        });
        assertEquals(1, model.getResources().size());
        model.getResources().forEach(dtmResourceDescriptorModel -> {
            assertEquals(1, dtmResourceDescriptorModel.getVersions().size());
            assertEquals(1, dtmResourceDescriptorModel.getVersions().get(0).getUsedIn().size());
        });

        System.out.println("----------------");
        System.out.println(objectMapper.writeValueAsString(model));
        System.out.println("----------------");
    }

    @Test
    void shouldExtractCompositionTemplateWithResources() throws Exception {
        final TemplateEntity templateEntity = templateRepository.findByName("composition_template2").orElseThrow();
        final DtmFileExportContext context = new DtmFileExportContext(templateEntity, true);
        final DtmFileDescriptorModel model = new DtmFileDescriptorModel("DTM2", "test-version");
        extractor.extract(context, model);
        assertEquals(1, model.getDataSamples().size());
        model.getDataSamples().forEach(dataSampleDescriptorModel -> {
            assertEquals(2, dataSampleDescriptorModel.getVersions().size());
        });
        assertEquals(1, model.getDataCollections().size());
        model.getDataCollections().forEach(dataCollectionDescriptorModel -> {
            assertEquals(1, dataCollectionDescriptorModel.getVersions().size());
            assertEquals(1, dataCollectionDescriptorModel.getSamples().size());
            assertEquals(2, dataCollectionDescriptorModel.getVersions().get(0).getUsedIn().size());
        });
        assertEquals(5, model.getTemplates().size());
        model.getTemplates().forEach(dtmTemplateDescriptorModel -> {
            assertEquals(1, dtmTemplateDescriptorModel.getVersions().size());
            final int expected = TemplateTypeEnum.COMPOSITION.equals(dtmTemplateDescriptorModel.getType()) ? 0 : 1;
            assertEquals(expected, dtmTemplateDescriptorModel.getVersions().get(0).getUsedIn().size());
        });
        assertEquals(1, model.getResources().size());
        model.getResources().forEach(dtmResourceDescriptorModel -> {
            assertEquals(1, dtmResourceDescriptorModel.getVersions().size());
            assertEquals(1, dtmResourceDescriptorModel.getVersions().get(0).getUsedIn().size());
        });

        System.out.println("----------------");
        System.out.println(objectMapper.writeValueAsString(model));
        System.out.println("----------------");
    }

    @Test
    void shouldExtractTemplateWithAllResourcesTypes() throws Exception {
        final TemplateEntity templateEntity = templateRepository.findByName("new-template").orElseThrow();
        final DtmFileExportContext context = new DtmFileExportContext(templateEntity, true);
        final DtmFileDescriptorModel model = new DtmFileDescriptorModel("DTM2", "test-version");
        extractor.extract(context, model);
        assertEquals(1, model.getTemplates().size());
        model.getTemplates().forEach(dtmTemplateDescriptorModel -> {
            assertEquals(1, dtmTemplateDescriptorModel.getVersions().size());
            assertEquals(0, dtmTemplateDescriptorModel.getVersions().get(0).getUsedIn().size());
        });
        assertEquals(3, model.getResources().size());
        assertThat(model.getResources().stream().map(DtmResourceDescriptorModel::getType).map(Object::toString).collect(Collectors.toList()), containsInAnyOrder("STYLESHEET", "IMAGE", "FONT"));
        model.getResources().forEach(dtmResourceDescriptorModel -> {
            assertEquals(1, dtmResourceDescriptorModel.getVersions().size());
            assertEquals(1, dtmResourceDescriptorModel.getVersions().get(0).getUsedIn().size());
            if (Objects.equals(ResourceTypeEnum.FONT, dtmResourceDescriptorModel.getType())) {
                assertNotNull(dtmResourceDescriptorModel.getVersions().get(0).getFontFaces());
            }
        });

        System.out.println("----------------");
        System.out.println(objectMapper.writeValueAsString(model));
        System.out.println("----------------");
    }

}
