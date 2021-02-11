package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.exception.resource.InvalidResourceContentException;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import org.springframework.stereotype.Component;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class ResourceStyleSheetContentValidatorImpl implements ContentValidator {

    private final CSSOMParser parser = new CSSOMParser(new SACParserCSS3());

    public ResourceStyleSheetContentValidatorImpl() {
        parser.setErrorHandler(new CSSExceptionErrorHandler());
    }

    @Override
    public boolean isValid(final byte[] content) {
        try (final InputStreamReader inputStream = new InputStreamReader(new ByteArrayInputStream(content))) {
            final InputSource source = new InputSource(inputStream);
            parser.parseStyleSheet(source, null, null);
            return true;
        } catch (IOException | CSSException e) {
            throw new InvalidResourceContentException(e);
        }
    }

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.STYLESHEET;
    }

    private static class CSSExceptionErrorHandler implements ErrorHandler {

        @Override
        public void warning(CSSParseException exception) {
        }

        @Override
        public void error(CSSParseException exception) {
            throw new CSSException(exception);
        }

        @Override
        public void fatalError(CSSParseException exception) {
            throw new CSSException(exception);
        }
    }

}
