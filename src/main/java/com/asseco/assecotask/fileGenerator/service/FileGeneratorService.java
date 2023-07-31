package com.asseco.assecotask.fileGenerator.service;

import com.asseco.assecotask.annotation.ForExcelImport;
import com.asseco.assecotask.appUser.dto.AppUserDto;
import com.asseco.assecotask.appUser.dto.ContactInfoDTO;
import com.asseco.assecotask.appUser.service.AppUserService;
import com.asseco.assecotask.contactType.service.ContactTypeService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class FileGeneratorService {

    public static final int DEFAULT_PAGE_SIZE = 1000;
    public static final int HEADER_TITLE_AND_CREATION_TIME_ROW = 0;
    public static final int USER_PARAMETERS_NAME_ROW = 2;
    public static final int GAP_BETWEEN_PARAMETERS_AND_DATA = 1;
    public static final int GAP_BETWEEN_USER_DATA_AND_CONTACTINFO = 1;
    private final AppUserService appUserService;

    private final ContactTypeService contactTypeService;

    public FileGeneratorService(AppUserService appUserService, ContactTypeService contactTypeService) {
        this.appUserService = appUserService;
        this.contactTypeService = contactTypeService;
    }

    public ResponseEntity<byte[]> generateExcelUserList(Integer pageSize) {

        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        try (Workbook workbook = new XSSFWorkbook()) {

            String creationTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd 'T' HH.mm.ss"));
            String sheetName = "User List";
            String prefix = " at - ";
            String fileName = sheetName + prefix + creationTime;

            Sheet sheet = workbook.createSheet(sheetName);

            Row headerTitle = sheet.createRow(HEADER_TITLE_AND_CREATION_TIME_ROW);
            headerTitle.createCell(0).setCellValue(fileName);

            Row parametersNameRow = sheet.createRow(USER_PARAMETERS_NAME_ROW);
            Field[] userFields = getUserFields();
            List<Field> markedUserFields = new ArrayList<>();
            boolean extractWithContacts = false;

            for (Field userField : userFields) {
                userField.setAccessible(true);

                if (userField.isAnnotationPresent(ForExcelImport.class) && userField.getName().equals("contactInfoList")) {
                    extractWithContacts = true;
                    markedUserFields.add(userField);
                }

                if (userField.isAnnotationPresent(ForExcelImport.class) && !userField.getType().equals(List.class)) {
                    parametersNameRow.createCell(markedUserFields.size()).setCellValue(userField.getName());
                    markedUserFields.add(userField);
                }
            }

            int numberOfAvailableContactTypes = contactTypeService.getAll().size();

            int columnIndex = markedUserFields.size() + GAP_BETWEEN_USER_DATA_AND_CONTACTINFO - 1;

            if (extractWithContacts) {
                for (int i = 0; i < numberOfAvailableContactTypes; i++) {
                    parametersNameRow.createCell(columnIndex).setCellValue("contact info type " + (i + 1));
                    parametersNameRow.createCell(columnIndex + 1).setCellValue("contact info value " + (i + 1));
                    columnIndex += 2;
                }
            }

            List<AppUserDto> allUsers = new LinkedList<>();
            int pageNumber = 0;
            Page<AppUserDto> userListPage;

            do {
                userListPage = appUserService.getAllUsersWithContactTypes(pageNumber, pageSize);
                List<AppUserDto> userList = userListPage.getContent();
                allUsers.addAll(userList);
                pageNumber++;
            } while (userListPage.hasNext());

            int userParametersValueRow = USER_PARAMETERS_NAME_ROW + GAP_BETWEEN_PARAMETERS_AND_DATA + 1;

            for (AppUserDto user : allUsers) {
                Row parametersValueRow = sheet.createRow(userParametersValueRow);

                for (int i = 0; i < markedUserFields.size(); i++) {
                    Field field = markedUserFields.get(i);
                    field.setAccessible(true);
                    try {
                        String value = field.get(user).toString();
                        if (field.isAnnotationPresent(ForExcelImport.class) && !markedUserFields.get(i).getType().equals(List.class)) {
                            parametersValueRow.createCell(i).setCellValue(value != null ? value : "");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                if (extractWithContacts) {
                    List<ContactInfoDTO> contactInfoList = user.getContactInfoList();

                    int contactInfoIndex = 0;

                    for (ContactInfoDTO contactInfo : contactInfoList) {
                        int contactInfoStartCol = markedUserFields.size() + GAP_BETWEEN_USER_DATA_AND_CONTACTINFO -1 + (contactInfoIndex * 2);
                        parametersValueRow.createCell(contactInfoStartCol).setCellValue(contactInfo.getContactType());
                        parametersValueRow.createCell(contactInfoStartCol + 1).setCellValue(contactInfo.getContactValue());
                        contactInfoIndex++;
                        if (contactInfoIndex >= numberOfAvailableContactTypes) {
                            break;
                        }
                    }
                }

                userParametersValueRow++;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] fileContent = outputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("Document of an App User List", fileName + ".xlsx");

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Field[] getUserFields() {
        try {
            Class<?> appUserDtoClass = Class.forName("com.asseco.assecotask.appUser.dto.AppUserDto");
            return appUserDtoClass.getDeclaredFields();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
