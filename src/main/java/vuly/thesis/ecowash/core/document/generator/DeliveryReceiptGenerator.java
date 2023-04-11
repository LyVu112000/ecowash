package vuly.thesis.ecowash.core.document.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import vuly.thesis.ecowash.core.document.converter.FileConversions;
import vuly.thesis.ecowash.core.entity.DeliveryReceipt;
import vuly.thesis.ecowash.core.util.EbstUserRequest;
import vuly.thesis.ecowash.core.util.ImageUtil;
import vuly.thesis.ecowash.core.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryReceiptGenerator {

    private final FileConversions fileConversions;
    private final EbstUserRequest ebstUserRequest;
    private final ObjectMapper objectMapper;

    public File generatePdfFile(DeliveryReceipt deliveryReceipt) throws DocumentException, IOException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.addDialect(new Java8TimeDialect());

        Map<String, Object> variables = new HashMap<>();

        String deliveryDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ebstUserRequest.currentZoneId())
                .format(deliveryReceipt.getDeliveryDate());

        Map<Long, String> receivedDateFormattedGroupByReceivedId = deliveryReceipt.getDeliveryLinkReceivedList().stream()
                .collect(Collectors.toMap(
                        i -> i.getReceivedReceipt().getId(),
                        i -> DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                                .withZone(ebstUserRequest.currentZoneId())
                                .format(i.getReceivedReceipt().getReceivedDate())
                        )
                );

        String signatureStaff = "";
        String signatureCustomer = "";
        if (!StringUtil.isEmpty(deliveryReceipt.getSignatureStaff())) {
            signatureStaff = objectMapper.readValue(deliveryReceipt.getSignatureStaff(), new TypeReference<Map<String,String>>(){})
                    .getOrDefault("signature_url", "");
        }
        if (!StringUtil.isEmpty(deliveryReceipt.getSignatureCustomer())) {
            signatureCustomer = objectMapper.readValue(deliveryReceipt.getSignatureCustomer(), new TypeReference<Map<String,String>>(){})
                    .getOrDefault("signature_url", "");
        }

        variables.put("dr", deliveryReceipt);
        variables.put("deliveryDate", deliveryDate);
        variables.put("receivedDateMap", receivedDateFormattedGroupByReceivedId);
        variables.put("signatureStaff", String.format("%s/receipt/signature/%s", ImageUtil.CDN_BASE_URL, signatureStaff));
        variables.put("signatureCustomer", String.format("%s/receipt/signature/%s", ImageUtil.CDN_BASE_URL, signatureCustomer));

        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process("templates/delivery-receipt/" + deliveryReceipt.getProductType().getValue(), context);
        return fileConversions.generatePdfFromHtml(html, deliveryReceipt.getCode());
    }
}
