package com.itextpdf.dito.manager.integration.editor.component.resource.impl;

import com.itextpdf.dito.editor.server.common.core.detector.TypeDetector;
import com.itextpdf.dito.editor.server.common.core.detector.image.ITextCoreBasedImageTypeDetector;
import com.itextpdf.dito.editor.server.common.core.detector.image.ImageType;
import com.itextpdf.dito.editor.server.common.core.stream.NamedStreamable;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.integration.editor.component.resource.ContentTypeHeaderMapper;
import org.apache.logging.log4j.core.util.FileUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public class ImageContentTypeHeaderMapperImpl implements ContentTypeHeaderMapper {

    private static final Map<ImageType, String> contentTypes = Map.of(ImageType.PNG, "image/png", ImageType.JPEG, "image/jpeg",
            ImageType.TIFF, "image/tiff", ImageType.GIFF, "image/gif", ImageType.SVG, "image/svg+xml");
    private static final TypeDetector<ImageType> DETECTOR = ITextCoreBasedImageTypeDetector.DEFAULT;

    @Override
    public String map(final String originalName, final byte[] data) {
        String result;

        final String extension = (originalName == null ? null : FileUtils.getFileExtension(new File(originalName)));
        if ("svg".equalsIgnoreCase(extension)) {
            result = contentTypes.get(ImageType.SVG);
        } else {
            try {
                final ImageType imageType = DETECTOR.detect(new ResourceFileEntityNamedStreamable(originalName, data));
                result = contentTypes.get(imageType);
            } catch (Exception e) {
                result = null;
            }
        }
        return result;
    }

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.IMAGE;
    }

    private static class ResourceFileEntityNamedStreamable implements NamedStreamable {

        private final String originalName;
        private final byte[] data;

        public ResourceFileEntityNamedStreamable(String originalName, byte[] data) {
            this.originalName = originalName;
            this.data = data;
        }

        @Override
        public String getName() {
            return originalName;
        }

        @Override
        public InputStream openStream() throws IOException {
            return new ByteArrayInputStream(data);
        }
    }

}
