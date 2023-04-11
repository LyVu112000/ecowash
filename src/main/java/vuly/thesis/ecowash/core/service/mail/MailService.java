package vuly.thesis.ecowash.core.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.exception.AppException;
import vuly.thesis.ecowash.core.util.StringUtil;
import vuly.thesis.ecowash.core.validation.EmailValidation;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final EmailValidation emailValidation;
    private final JavaMailSender javaMailSender;

    public void sendWithAttachments(List<String> emails, String subject, String content, @Nullable String fileName, List<File> files) throws MessagingException {
        this.validateEmail(emails);

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg,true,"UTF-8");
        helper.setTo(emails.toArray(String[]::new));
        helper.setSubject(subject);
        helper.setText(content, true);

        if(files != null && !files.isEmpty()) {
            for (File file : files) {
                helper.addAttachment(StringUtil.isEmpty(fileName) ? file.getName() : fileName, file);
            }
        }

        javaMailSender.send(msg);
    }

    public void send(List<String> emails, String subject, String content) throws MessagingException {
        this.validateEmail(emails);

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg,"UTF-8");
        helper.setTo(emails.toArray(String[]::new));
        helper.setSubject(subject);
        helper.setText(content, true);

        javaMailSender.send(msg);
    }

    private void validateEmail(List<String> emails) {
        for(String email : emails) {
            if(!emailValidation.isValid(email)) {
                throw new AppException(4021, emails);
            }
        }
    }
}
