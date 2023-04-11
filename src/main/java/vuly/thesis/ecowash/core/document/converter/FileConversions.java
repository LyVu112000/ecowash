package vuly.thesis.ecowash.core.document.converter;

import com.lowagie.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;
import vuly.thesis.ecowash.core.util.VNCharacterUtils;

import java.io.*;
import java.util.UUID;

@Slf4j
@Service
public class FileConversions {

    public File generatePdfFromHtml(String html, String filePrefix) throws IOException, com.lowagie.text.DocumentException {
        File tmpFile = File.createTempFile(filePrefix + UUID.randomUUID(), ".pdf");

        OutputStream outputStream = new FileOutputStream(tmpFile);
        ITextRenderer renderer = new ITextRenderer();
//        renderer.getFontResolver().addFont("fonts/times-new-roman-bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        renderer.getFontResolver().addFont("fonts/times-new-roman-regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.getFontResolver().addFont("fonts/Roboto-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.getFontResolver().addFont("fonts/Roboto-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        renderer.setDocumentFromString(html);
//        renderer.setDocumentFromString(html,  new ClassPathResource("/META-INF/pdfTemplates/").getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        return tmpFile;
    }

    public File buildWorkbookFile(String name, Workbook workbook) throws IOException {
        File file = File.createTempFile(VNCharacterUtils.convertToCodeStyle(name), ".xlsx");
        String fileLocation = file.getAbsolutePath();
        log.info("save to path " + fileLocation);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            log.info("FileNotFoundException", e);
        }
        return file;
    }
}
