package com.itextpdf.dito.manager.config;

import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class TemplateConfig {

    /**
     * Bean for linking template resources within PDF generation process.
     *
     * @return template asset retriever component
     */
    @Bean
    public TemplateAssetRetriever templateAssetRetriever() {
        return new TemplateAssetRetriever() {
            @Override
            public InputStream getResourceAsStream(String s) throws IOException {
                //TODO implement logic when TEMPLATE-RESOURCE link is implemented
                return null;
            }

            @Override
            public boolean isUriSupported(String s) {
                //TODO check if external resources are supported
                return false;
            }
        };
    }
}
