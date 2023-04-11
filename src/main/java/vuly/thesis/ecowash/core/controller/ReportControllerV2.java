package vuly.thesis.ecowash.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vuly.thesis.ecowash.core.payload.dto.ReportDetailGeneralDto;
import vuly.thesis.ecowash.core.payload.dto.ReportGeneralDto;
import vuly.thesis.ecowash.core.payload.request.ReportGeneralSearchRequest;
import vuly.thesis.ecowash.core.payload.response.AppResponse;
import vuly.thesis.ecowash.core.service.ReportServiceV2;
import vuly.thesis.ecowash.core.util.MediaTypeUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/report/v2")
@RequiredArgsConstructor
public class ReportControllerV2 {
    private final ReportServiceV2 reportService;

    @GetMapping("/general")
    public ResponseEntity<?> getReceiptList(ReportGeneralSearchRequest request) {
        List<ReportGeneralDto> result = reportService.getReportGeneral(request);
        return ResponseEntity.ok(AppResponse.success(result));
    }
    @GetMapping("/detail/receipt/v2")
    public ResponseEntity<?> getReceiptDetailReportV2(ReportGeneralSearchRequest request) {
        ReportDetailGeneralDto result = reportService.getReceiptDetailReportV2(request);
        return ResponseEntity.ok(AppResponse.success(result));
    }

    @GetMapping("/files/export/general")
    public ResponseEntity<?> exportGeneralReportXlsxFile(ReportGeneralSearchRequest request) throws IOException {
        File reportFile = reportService.generateGeneralReport(request);
        byte[] data = Files.readAllBytes(reportFile.toPath());
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + reportFile.getName())
                .contentType(MediaTypeUtils.getMediaTypeForFile(reportFile))
                .contentLength(data.length)
                .body(resource);
    }

    @GetMapping("/files/export/detail")
    public ResponseEntity<?> exportDetailReportXlsxFile(ReportGeneralSearchRequest request) throws IOException {
        File reportFile = reportService.generateDetailReport(request);
        byte[] data = Files.readAllBytes(reportFile.toPath());
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + reportFile.getName())
                .contentType(MediaTypeUtils.getMediaTypeForFile(reportFile))
                .contentLength(data.length)
                .body(resource);
    }
}
