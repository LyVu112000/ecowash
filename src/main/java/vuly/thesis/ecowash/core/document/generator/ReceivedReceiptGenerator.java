package vuly.thesis.ecowash.core.document.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import vuly.thesis.ecowash.core.document.converter.FileConversions;
import vuly.thesis.ecowash.core.entity.ReceivedReceipt;
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
public class ReceivedReceiptGenerator {

    private final FileConversions fileConversions;
    private final EbstUserRequest ebstUserRequest;
    private final ObjectMapper objectMapper;

    public File generatePdfFile(ReceivedReceipt receivedReceipt) throws DocumentException, IOException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Map<String, Object> variables = new HashMap<>();

        String receivedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ebstUserRequest.currentZoneId())
                .format(receivedReceipt.getReceivedDate());

        String signatureStaff = "";
        String signatureCustomer = "";
        if (!StringUtil.isEmpty(receivedReceipt.getSignatureStaff())) {
            signatureStaff = objectMapper.readValue(receivedReceipt.getSignatureStaff(), new TypeReference<Map<String,String>>(){})
                    .getOrDefault("signature_url", "");
        }
        if (!StringUtil.isEmpty(receivedReceipt.getSignatureCustomer())) {
            signatureCustomer = objectMapper.readValue(receivedReceipt.getSignatureCustomer(), new TypeReference<Map<String,String>>(){})
                    .getOrDefault("signature_url", "");
        }

        String specialInstructions = receivedReceipt.getSpecialInstructionOfReceipts()
                .stream()
                .map(s -> s.getSpecialInstruction().getName())
                .collect(Collectors.joining(", "));

        variables.put("rr", receivedReceipt);
        variables.put("receivedDate", receivedDate);
        variables.put("signatureStaff", String.format("%s/receipt/signature/%s", ImageUtil.CDN_BASE_URL, signatureStaff));
        variables.put("signatureCustomer", String.format("%s/receipt/signature/%s", ImageUtil.CDN_BASE_URL, signatureCustomer));
        variables.put("specialInstructions", specialInstructions);

        Context context = new Context();
        context.setVariables(variables);

        String html = templateEngine.process("templates/received-receipt/" + receivedReceipt.getProductType().getValue(), context);
        return fileConversions.generatePdfFromHtml(html, receivedReceipt.getCode());
    }
}
