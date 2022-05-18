package kz.iitu.orken.medical_managament_system.service.impl;

import kz.iitu.orken.medical_managament_system.Exception.NotFoundException;
import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExcelService {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public ByteArrayOutputStream exportTreatment(List<Treatment> treatmentList) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        Sheet sheets;
        Sheet sheet;

        sheets = workbook.createSheet("sheet");
        sheet = workbook.getSheetAt(0);

        if (treatmentList.isEmpty()) {
            throw new NotFoundException("Could not extract excel of treatments");
        }

        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle styleTitle = setStyle(workbook, thin, black, style);
        int rowIndex = 0;
        setTitle(sheet, sheets, styleTitle, rowIndex,
                Arrays.asList(("#;" +
                        "description;" +
                        "createdDate;" +
                        "endDate;" +
                        "price;" +
                        "disease;" +
                        "doctor;" +
                        "patient").split(";")));
        List<List<String>> data = treatmentList.stream().map(x -> {
            List<String> list = new ArrayList<>();
            list.add(String.valueOf(x.getId()));
            list.add(x.getDescription());
            list.add(x.getCreatedDate() != null ? dateFormat.format(x.getCreatedDate()) : "TBD");
            list.add(x.getEndDate() != null ? dateFormat.format(x.getEndDate()) : "TBD");
            list.add(String.valueOf(x.getPrice()));
            list.add(x.getDisease().getName());
            list.add(x.getDoctor().getUsername());
            list.add(x.getPatient().getUsername());
            return list;
        }).collect(Collectors.toList());
        addData(sheet, sheets, style, data, rowIndex);
        return getFile(workbook);
    }

    public ByteArrayOutputStream exportDisease(List<Disease> diseaseList) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        Sheet sheets;
        Sheet sheet;

        sheets = workbook.createSheet("sheet");
        sheet = workbook.getSheetAt(0);

        if (diseaseList.isEmpty()) {
            throw new NotFoundException("Could not extract excel of diseases");
        }

        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle styleTitle = setStyle(workbook, thin, black, style);
        int rowIndex = 0;
        setTitle(sheet, sheets, styleTitle, rowIndex,
                Arrays.asList(("#;" +
                        "name;" +
                        "Medicines used against").split(";")));
        List<List<String>> data = diseaseList.stream().map(x -> {
            List<String> list = new ArrayList<>();
            list.add(String.valueOf(x.getId()));
            list.add(x.getName());
            list.add(x.getMedicines().stream().map(Medicine::getName).collect(Collectors.joining(",")));
            return list;
        }).collect(Collectors.toList());
        addData(sheet, sheets, style, data, rowIndex);
        return getFile(workbook);
    }

    public ByteArrayOutputStream exportMedicine(List<Medicine> medicineList) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        Sheet sheets;
        Sheet sheet;

        sheets = workbook.createSheet("sheet");
        sheet = workbook.getSheetAt(0);

        if (medicineList.isEmpty()) {
            throw new NotFoundException("Could not extract excel of medicines");
        }

        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle styleTitle = setStyle(workbook, thin, black, style);
        int rowIndex = 0;
        setTitle(sheet, sheets, styleTitle, rowIndex,
                Arrays.asList(("#;" +
                        "name;" +
                        "price;" +
                        "diseases it cures").split(";")));
        List<List<String>> data = medicineList.stream().map(x -> {
            List<String> list = new ArrayList<>();
            list.add(String.valueOf(x.getId()));
            list.add(x.getName());
            list.add(String.valueOf(x.getPrice()));
            list.add(x.getDiseases().stream().map(Disease::getName).collect(Collectors.joining(",")));
            return list;
        }).collect(Collectors.toList());
        addData(sheet, sheets, style, data, rowIndex);
        return getFile(workbook);
    }

    public ByteArrayOutputStream exportUsers(List<User> users) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFCellStyle style = workbook.createCellStyle();
        Sheet sheets;
        Sheet sheet;

        sheets = workbook.createSheet("Users");
        sheet = workbook.getSheetAt(0);

        if (users.isEmpty()) {
            throw new NotFoundException("Could not extract excel of users");
        }

        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle styleTitle = setStyle(workbook, thin, black, style);
        int rowIndex = 0;

        setTitle(sheet, sheets, styleTitle, rowIndex,
                Arrays.asList(("#;" +
                        "firstName;" +
                        "lastName;" +
                        "email;" +
                        "username;" +
                        "password;" +
                        "roles").split(";")));

        List<List<String>> data = users.stream().map(x -> {
            List<String> list = new ArrayList<>();
            list.add(String.valueOf(x.getId()));
            list.add(x.getFirstName());
            list.add(x.getLastName());
            list.add(x.getEmail());
            list.add(x.getUsername());
            list.add(x.getPassword());
            list.add(x.getRoles().stream().map(Role::getName).collect(Collectors.joining(",")));
            return list;
        }).collect(Collectors.toList());
        addData(sheet, sheets, style, data, rowIndex);
        return getFile(workbook);
    }

    private ByteArrayOutputStream getFile(Workbook workbook) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            workbook.write(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }

    private void addData(Sheet sheet, Sheet sheets, XSSFCellStyle style, List<List<String>> data, int rowIndex) {
        for (List<String> d : data) {
            sheets.createRow(++rowIndex);
            insertToRow(sheet, sheets, rowIndex, d, style);
        }
        for (int i = 0; i < 21; i++) {
            if (i == 9 || i == 10 || i == 17) {
                sheets.setColumnWidth(9, 20000);
                sheets.setColumnWidth(10, 20000);
                sheets.setColumnWidth(17, 20000);
                continue;
            }
            sheets.autoSizeColumn(i);
        }
    }

    private void setTitle(Sheet sheet, Sheet sheets, XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
        sheets.createRow(rowIndex);
        insertToRow(sheet, sheets, rowIndex, title, styleTitle);
    }

    private void insertToRow(Sheet sheet, Sheet sheets, int row, List<String> cellValues, CellStyle cellStyle) {
        int cellIndex = 0;
        for (String cellValue : cellValues) {
            setCellValue(sheet, sheets, row, cellIndex++, cellValue, cellStyle);
        }
    }

    private void setCellValue(Sheet sheet, Sheet sheets, int rowIndex, int cellIndex, String cellValue, CellStyle cellStyle) {
        sheets.getRow(rowIndex).createCell(cellIndex).setCellValue(getString(cellValue));
        sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(cellStyle);
    }

    private String getString(String nullable) {
        if (nullable == null) return "";
        return nullable;
    }

    private XSSFCellStyle setStyle(XSSFWorkbook workbook, BorderStyle thin, short black, XSSFCellStyle style) {
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
        style.setBorderTop(thin);
        style.setBorderBottom(thin);
        style.setBorderRight(thin);
        style.setBorderLeft(thin);
        style.setTopBorderColor(black);
        style.setRightBorderColor(black);
        style.setBottomBorderColor(black);
        style.setLeftBorderColor(black);
        style.getFont().setBold(true);
        BorderStyle tittle = BorderStyle.MEDIUM;

        XSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setColor(black);
        titleFont.setFontHeight(10);

        XSSFCellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setWrapText(true);
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        styleTitle.setBorderTop(tittle);
        styleTitle.setBorderBottom(tittle);
        styleTitle.setBorderRight(tittle);
        styleTitle.setBorderLeft(tittle);
        styleTitle.setTopBorderColor(black);
        styleTitle.setRightBorderColor(black);
        styleTitle.setBottomBorderColor(black);
        styleTitle.setLeftBorderColor(black);
        styleTitle.setFont(titleFont);
        return styleTitle;
    }
}
