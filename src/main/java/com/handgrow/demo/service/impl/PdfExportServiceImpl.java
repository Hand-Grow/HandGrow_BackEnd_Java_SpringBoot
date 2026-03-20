package com.handgrow.demo.service.impl;

import com.handgrow.demo.service.PdfExportService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class PdfExportServiceImpl implements PdfExportService {

    private static final Logger log = LoggerFactory.getLogger(PdfExportServiceImpl.class);

    private final TemplateEngine templateEngine;

    @Override
    public byte[] generateContractPdf(
            String cooperativeName,
            String enterpriseName,
            String value,
            String terms,
            String enterpriseSignatoryName,
            String enterpriseSignDate) {
        Context context = new Context();
        context.setVariable("cooperativeName", cooperativeName);
        context.setVariable("enterpriseName", enterpriseName);
        context.setVariable("totalValue", value);
        context.setVariable("aiTerms", terms);
        context.setVariable("enterpriseSignatoryName", enterpriseSignatoryName != null ? enterpriseSignatoryName : "");
        context.setVariable("enterpriseSignDate", enterpriseSignDate != null ? enterpriseSignDate : "");

        String htmlContent = templateEngine.process("contract", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            // Resolve a proper base URI for relative resources (fonts, images) in the templates folder
            String baseUri = null;
            try {
                URL templatesUrl = getClass().getResource("/templates/");
                if (templatesUrl != null) {
                    baseUri = templatesUrl.toExternalForm();
                }
            } catch (Exception ignore) {
                // fallback to null
            }
            if (baseUri == null) {
                baseUri = ""; // let openhtmltopdf resolve relative paths from current working dir
            }
            log.debug("Using baseUri='{}' for PDF rendering", baseUri);
            builder.withHtmlContent(htmlContent, baseUri);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            // Add original exception message to make debugging easier
            log.error("Error generating PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo PDF hợp đồng! " + e.getMessage(), e);
        }
    }
}
