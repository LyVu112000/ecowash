package vuly.thesis.ecowash.core.document.generator;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.document.converter.FileConversions;
import vuly.thesis.ecowash.core.entity.LaundryForm;
import vuly.thesis.ecowash.core.payload.dto.ReportDetailDtoV2;
import vuly.thesis.ecowash.core.payload.dto.ReportGeneralDto;
import vuly.thesis.ecowash.core.payload.dto.ReportGeneralLaundryFormDetailDto;
import vuly.thesis.ecowash.core.repository.core.ILaundryFormRepository;
import vuly.thesis.ecowash.core.util.DateTimeUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportGenerator {

    private final FileConversions fileConversions;
    private final ILaundryFormRepository laundryFormRepository;

    public File generateGeneralReportXlsxFile(String customerName, String productTypeName, String specialInstructions, Instant fromDate, Instant toDate,
                                 List<ReportGeneralDto> reportGeneralDtoList) throws IOException {
//        InputStream file = new ClassPathResource("documents/report/general/" + productTypeName + ".xlsx").getInputStream();
        InputStream file = new ClassPathResource("documents/report/general/general.xlsx").getInputStream();
        Workbook workbook = new XSSFWorkbook(file);

        if ("ordinary_product".equals(productTypeName) || "rewash".equals(productTypeName)) {
            this.initDataWithOrdinaryProductOrRewash(workbook, productTypeName, customerName, specialInstructions, fromDate, toDate, reportGeneralDtoList);
        } else {
            this.initDataWithSpecialProduct(workbook, customerName, specialInstructions, fromDate, toDate, reportGeneralDtoList);
        }

        return fileConversions.buildWorkbookFile("Report", workbook);
    }

    private void initDataWithOrdinaryProductOrRewash(Workbook workbook, String productTypeName, String customerName, String specialInstructions,
                                                     Instant fromDate, Instant toDate, List<ReportGeneralDto> reportGeneralDtoList) {
        Sheet sheet = workbook.getSheetAt(0);

        Row row3 = sheet.getRow(2);
        row3.getCell(2).setCellValue(customerName);
        row3.getCell(5).setCellValue(specialInstructions);

        Row row4 = sheet.getRow(3);
        row4.getCell(2).setCellValue(DateTimeUtil.formatToString(fromDate));
        row4.getCell(5).setCellValue(DateTimeUtil.formatToString(toDate));

        Row row5 = sheet.getRow(4);
        row5.getCell(2).setCellValue("ordinary_product".equals(productTypeName) ? "Thường" : "Rewash");

        Row row8 = sheet.getRow(7);
        Row row9 = sheet.getRow(8);
        final float DEFAULT_ROW_HEIGHT = row8.getHeightInPoints();
        CellStyle evenRowStyle = row8.getCell(0).getCellStyle();
        CellStyle oddRowStyle = row9.getCell(0).getCellStyle();
        CellStyle textEvenRowStyle = row8.getCell(1).getCellStyle();
        CellStyle textOddRowStyle = row9.getCell(1).getCellStyle();

        int curProductItemRowNumber = 7; // row 8
        int numericalOrder = 1;
        for (ReportGeneralDto reportDto : reportGeneralDtoList) {
            CellStyle defaultStyle = curProductItemRowNumber % 2 == 0 ? evenRowStyle : oddRowStyle;
            CellStyle textStyle = curProductItemRowNumber % 2 == 0 ? textEvenRowStyle : textOddRowStyle;
            Row productItemRow = sheet.createRow(curProductItemRowNumber);
            productItemRow.setHeightInPoints(DEFAULT_ROW_HEIGHT);

            this.addCell(productItemRow, 0, defaultStyle, String.valueOf(numericalOrder));
            this.addCell(productItemRow, 1, textStyle, reportDto.getProductName() + " (" + reportDto.getPieceTypeName() + ")");
//            this.addCell(productItemRow, 2, defaultStyle, reportDto.getNumberReceived(), true);
            this.addCellWithNumericFormat(workbook, productItemRow, 2, defaultStyle, reportDto.getNumberReceived());
            this.addCellWithNumericFormat(workbook, productItemRow, 3, defaultStyle, reportDto.getNumberDeliveryActual());
//            this.addCell(productItemRow, 4, defaultStyle, reportDto.getNumberReceivedOfRewash(), true);
            this.addCellWithNumericFormat(workbook, productItemRow, 4, defaultStyle, reportDto.getOpenNumberReceivedOfDebt());
            this.addCellWithNumericFormat(workbook, productItemRow, 5, defaultStyle, reportDto.getNumberReceivedOfDebt());
            this.addCellWithNumericFormat(workbook, productItemRow, 6, defaultStyle, reportDto.getOpenNumberReceivedOfDebt() + reportDto.getNumberReceivedOfDebt());

            numericalOrder++;
            curProductItemRowNumber++;
        }
    }

    private void initDataWithSpecialProduct(Workbook workbook, String customerName, String specialInstructions,
                                            Instant fromDate, Instant toDate, List<ReportGeneralDto> reportGeneralDtoList) {
        List<LaundryForm> laundryForms = laundryFormRepository.findByOrderByIdAsc();

        for (LaundryForm laundryForm : laundryForms) {
            Sheet sheet = workbook.cloneSheet(0);
            workbook.setSheetName(workbook.getSheetIndex(sheet), laundryForm.getName());

            Row row3 = sheet.getRow(2);
            row3.getCell(2).setCellValue(customerName);
            row3.getCell(5).setCellValue(specialInstructions);

            Row row4 = sheet.getRow(3);
            row4.getCell(2).setCellValue(DateTimeUtil.formatToString(fromDate));
            row4.getCell(5).setCellValue(DateTimeUtil.formatToString(toDate));

            Row row5 = sheet.getRow(4);
            row5.getCell(2).setCellValue("Đặc biệt");

            Row row8 = sheet.getRow(7);
            Row row9 = sheet.getRow(8);
            final float DEFAULT_ROW_HEIGHT = row8.getHeightInPoints();
            CellStyle evenRowStyle = row8.getCell(0).getCellStyle();
            CellStyle oddRowStyle = row9.getCell(0).getCellStyle();
            CellStyle debtStyleEvenRow = row8.getCell(5).getCellStyle();
            CellStyle debtStyleOddRow = row9.getCell(5).getCellStyle();

            int curProductItemRowNumber = 7; // row 8
            int numericalOrder = 1;
            for (ReportGeneralDto report : reportGeneralDtoList) {
                Optional<ReportGeneralLaundryFormDetailDto> rpGeneralLaundryFormDetailOpt = report.getReportGeneralLaundryFormDetailDtoList().stream()
                        .filter(e -> e.getLaundryFormValue().equals(laundryForm.getValue()))
                        .findFirst();
                if (rpGeneralLaundryFormDetailOpt.isPresent()) {
                    CellStyle defaultStyle = curProductItemRowNumber % 2 == 0 ? evenRowStyle : oddRowStyle;
                    CellStyle debtStyle = curProductItemRowNumber % 2 == 0 ? debtStyleEvenRow : debtStyleOddRow;
                    ReportGeneralLaundryFormDetailDto rpGeneralLaundryFormDetail = rpGeneralLaundryFormDetailOpt.get();
                    Row productItemRow = sheet.createRow(curProductItemRowNumber);
                    productItemRow.setHeightInPoints(DEFAULT_ROW_HEIGHT);
                    this.addCell(productItemRow, 0, defaultStyle, String.valueOf(numericalOrder));
                    this.addCell(productItemRow, 1, defaultStyle, report.getProductName() + " (" + report.getPieceTypeName() + ")");
                    this.addCellWithNumericFormat(workbook, productItemRow, 2, defaultStyle, rpGeneralLaundryFormDetail.getNumberReceived());
                    this.addCellWithNumericFormat(workbook, productItemRow, 3, defaultStyle, rpGeneralLaundryFormDetail.getNumberDeliveryActual());
//                    this.addCell(productItemRow, 4, defaultStyle, rpGeneralLaundryFormDetail.getNumberReceivedOfRewash(), true);
                    this.addCellWithNumericFormat(workbook, productItemRow, 4, debtStyle, rpGeneralLaundryFormDetail.getOpenNumberReceivedOfDebt());
                    this.addCellWithNumericFormat(workbook, productItemRow, 5, defaultStyle, rpGeneralLaundryFormDetail.getNumberReceivedOfDebt());
                    this.addCellWithNumericFormat(workbook, productItemRow, 6, debtStyle,
                            rpGeneralLaundryFormDetail.getOpenNumberReceivedOfDebt() + rpGeneralLaundryFormDetail.getNumberReceivedOfDebt());

                    numericalOrder++;
                    curProductItemRowNumber++;
                }
            }
        }

        workbook.removeSheetAt(0);
    }

    public File generateDetailReportXlsxFile(Boolean isDelivery, String customerName, String productItemName, String pieceTypeName,
                                               String laundryFormName, String specialInstructions, Instant fromDate, Instant toDate,
                                               List<ReportDetailDtoV2> reportDetailDtoList) throws IOException {
        InputStream file = new ClassPathResource("documents/report/detail/" + (isDelivery ? "delivery_receipt" : "received_receipt") + ".xlsx").getInputStream();
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        sheet.getRow(3).getCell(1).setCellValue(customerName);
        sheet.getRow(3).getCell(3).setCellValue(DateTimeUtil.formatToString(fromDate));
        sheet.getRow(3).getCell(5).setCellValue(DateTimeUtil.formatToString(toDate));
        sheet.getRow(4).getCell(1).setCellValue(productItemName + " (" + pieceTypeName + ")");
        sheet.getRow(4).getCell(3).setCellValue(laundryFormName);
        sheet.getRow(4).getCell(5).setCellValue(specialInstructions);

        CellStyle evenRowStyle = sheet.getRow(7).getCell(0).getCellStyle();
        CellStyle oddRowStyle = sheet.getRow(8).getCell(0).getCellStyle();
        sheet.removeRow(sheet.getRow(7));
        sheet.removeRow(sheet.getRow(8));

        if (isDelivery) {
            this.initDetailReportWithDeliveryReceipt(workbook, sheet, evenRowStyle, oddRowStyle, reportDetailDtoList);
        } else {
            this.initDetailReportWithReceivedReceipt(workbook, sheet, evenRowStyle, oddRowStyle, reportDetailDtoList);
        }

        return fileConversions.buildWorkbookFile("Report", workbook);
    }

    private void initDetailReportWithDeliveryReceipt(Workbook workbook, Sheet sheet, CellStyle evenRowStyle, CellStyle oddRowStyle,
                                                     List<ReportDetailDtoV2> reportDetailDtoList) {
        final float defaultRowHeight = 25f;
        int curRow = 7; // row 8
        int totalRowEachReportDetail = 0;
        Row row = null;
        CellStyle style = null;

        for (int i = 0; i < reportDetailDtoList.size(); i++) {
            style = i % 2 == 0 ? evenRowStyle : oddRowStyle;
            for (ReportDetailDtoV2.ReceiptDetailReport receipt : reportDetailDtoList.get(i).getReceipts()) {
                int totalSubReceipt = 0;
                for (ReportDetailDtoV2.SubReceiptDetailReport subReceipt : receipt.getSubReceipts()) {
                    row = sheet.createRow(curRow);
                    row.setHeightInPoints(defaultRowHeight);
                    this.addCell(row, 0, style, "");
                    this.addCell(row, 1, style, "");
                    this.addCell(row, 2, style, subReceipt.getDate());
                    this.addCell(row, 3, style, subReceipt.getIsRewash() != null && subReceipt.getIsRewash() ? subReceipt.getCode() + " (RW)" : subReceipt.getCode());
                    this.addCell(row, 4, style, subReceipt.getReferenceCode());
                    this.addCellWithNumericFormat(workbook, row, 5, style, subReceipt.getProductItemQuantity());
                    this.addCellWithNumericFormat(workbook, row, 6, style, subReceipt.getNumberDelivery());
                    totalSubReceipt++;
                    curRow++;
                    totalRowEachReportDetail++;
                }

                if (totalSubReceipt != 1) {
                    sheet.addMergedRegion(new CellRangeAddress(curRow - receipt.getSubReceipts().size(), curRow - 1, 1, 1));
                }
                sheet.getRow(curRow - receipt.getSubReceipts().size()).getCell(1).setCellValue(receipt.getCode());
            }

            Cell dateOfReceiptCell;
            if (totalRowEachReportDetail == 1) {
                dateOfReceiptCell = row.createCell(0);
            } else {
                sheet.addMergedRegion(new CellRangeAddress(curRow - totalRowEachReportDetail, curRow - 1, 0, 0));
                dateOfReceiptCell = sheet.getRow(curRow - totalRowEachReportDetail).createCell(0);
            }
            dateOfReceiptCell.setCellValue(reportDetailDtoList.get(i).getDate());
            dateOfReceiptCell.setCellStyle(style);
            totalRowEachReportDetail = 0;
        }
    }

    private void initDetailReportWithReceivedReceipt(Workbook workbook, Sheet sheet, CellStyle evenRowStyle, CellStyle oddRowStyle,
                                                     List<ReportDetailDtoV2> reportDetailDtoList) {
        final float defaultRowHeight = 25f;
        int curRow = 7; // row 8
        int totalRowEachReportDetail = 0;
        Row row = null;
        CellStyle style = null;

        for (int i = 0; i < reportDetailDtoList.size(); i++) {
            style = i % 2 == 0 ? evenRowStyle : oddRowStyle;
            for (ReportDetailDtoV2.ReceiptDetailReport receipt : reportDetailDtoList.get(i).getReceipts()) {
                if (receipt.getSubReceipts().isEmpty()) {
                    row = sheet.createRow(curRow);
                    row.setHeightInPoints(defaultRowHeight);
                    this.addCell(row, 0, style, "");
                    this.addCell(row, 1, style, receipt.getIsRewash() != null && receipt.getIsRewash() ? receipt.getCode() + " (RW)" : receipt.getCode());
                    this.addCell(row, 2, style, receipt.getReferenceCode());
                    this.addCellWithNumericFormat(workbook, row, 3, style, receipt.getProductItemQuantity());
                    this.addCell(row, 4, style, "");
                    this.addCell( row, 5, style, "");
                    this.addCellWithNumericFormat(workbook, row, 6, style, 0);
                    curRow++;
                    totalRowEachReportDetail++;
                } else {
                    int totalSubReceipt = 0;
                    for (ReportDetailDtoV2.SubReceiptDetailReport subReceipt : receipt.getSubReceipts()) {
                        row = sheet.createRow(curRow);
                        row.setHeightInPoints(defaultRowHeight);
                        this.addCell(row, 0, style, "");
                        this.addCell(row, 1, style, "");
                        this.addCell(row, 2, style, "");
                        this.addCellWithNumericFormat(workbook, row, 3, style, 0);
                        this.addCell(row, 4, style, subReceipt.getDate());
                        this.addCell(row, 5, style, subReceipt.getCode());
                        this.addCellWithNumericFormat(workbook, row, 6, style, subReceipt.getProductItemQuantity());
                        totalSubReceipt++;
                        curRow++;
                        totalRowEachReportDetail++;
                    }

                    if (totalSubReceipt != 1) {
                        sheet.addMergedRegion(new CellRangeAddress(curRow - receipt.getSubReceipts().size(), curRow - 1, 1, 1));
                        sheet.addMergedRegion(new CellRangeAddress(curRow - receipt.getSubReceipts().size(), curRow - 1, 2, 2));
                        sheet.addMergedRegion(new CellRangeAddress(curRow - receipt.getSubReceipts().size(), curRow - 1, 3, 3));
                    }

                    Row receiptRow = sheet.getRow(curRow - receipt.getSubReceipts().size());
                    receiptRow.getCell(1).setCellValue(receipt.getCode());
                    receiptRow.getCell(2).setCellValue(receipt.getReferenceCode());
                    receiptRow.getCell(3).setCellValue(receipt.getProductItemQuantity());
                }
            }

            Cell dateOfReceiptCell;
            if (totalRowEachReportDetail == 1) {
                dateOfReceiptCell = row.createCell(0);
            } else {
                sheet.addMergedRegion(new CellRangeAddress(curRow - totalRowEachReportDetail, curRow - 1, 0, 0));
                dateOfReceiptCell = sheet.getRow(curRow - totalRowEachReportDetail).createCell(0);
            }
            dateOfReceiptCell.setCellValue(reportDetailDtoList.get(i).getDate());
            dateOfReceiptCell.setCellStyle(style);
            totalRowEachReportDetail = 0;
        }
    }

    private Cell addCell(Row row, int columnIndex, CellStyle cellStyle, String value) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
        return cell;
    }

    private Cell addCellWithNumericFormat(Workbook wb, Row row, int columnIndex, CellStyle cellStyle, double value) {
        cellStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
        return cell;
    }

    private Cell addFormula(Workbook wb, Row row, int columnIndex, CellStyle cellStyle, String formula) {
        cellStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellFormula(formula);
        return cell;
    }
}
