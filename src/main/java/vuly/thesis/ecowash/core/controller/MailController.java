package vuly.thesis.ecowash.core.controller;

import lombok.RequiredArgsConstructor;
import org.dom4j.DocumentException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vuly.thesis.ecowash.core.document.generator.DeliveryReceiptGenerator;
import vuly.thesis.ecowash.core.document.generator.ReceivedReceiptGenerator;
import vuly.thesis.ecowash.core.repository.core.IDeliveryReceiptRepository;
import vuly.thesis.ecowash.core.service.DeliveryReceiptService;
import vuly.thesis.ecowash.core.service.ReceivedReceiptService;
import vuly.thesis.ecowash.core.service.mail.MailService;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;
    private final ReceivedReceiptGenerator receivedReceiptGenerator;
    private final ReceivedReceiptService receivedReceiptService;
    private final DeliveryReceiptGenerator deliveryReceiptGenerator;
    private final DeliveryReceiptService deliveryReceiptService;
    private final IDeliveryReceiptRepository iDeliveryReceiptRepository;

    @PostMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public Object sendEmail() throws MessagingException, DocumentException, IOException {
        String content = "";
//        mailService.send(List.of("minhhuy243@gmail.com"), "Test send Email", content);
//        File file = new File("C:\\Users\\Huy\\Desktop\\App KH.pdf");
//        File file = receivedReceiptGenerator.generatePdfFile();
//        mailService.sendWithAttachments(List.of("minhhuy243@gmail.com"), "Test send Email", content, null, List.of(file));
//        ReceivedReceipt receivedReceipt = receivedReceiptService.findById(385L);
//        ReceivedReceipt receivedReceipt = receivedReceiptService.findById(545L); // yeu cau dac biet
//        DeliveryReceipt deliveryReceipt = iDeliveryReceiptRepository.findById(308L).get(); // hang thuong
//        DeliveryReceipt deliveryReceipt = iDeliveryReceiptRepository.findById(319L).get(); // hang dac biet
//        mailService.sendWithAttachments(List.of("minhhuy243@gmail.com"), "ECOWASH HCMC thông báo" +
//                        " tiếp nhận phiếu hàng mới", content, "Phiếu giao hàng.pdf",
//                List.of(deliveryReceiptGenerator.generatePdfFile(deliveryReceipt)));
        //receivedReceiptService.sendViaEmail(receivedReceipt);
        return null;
    }
}
