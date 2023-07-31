package com.asseco.assecotask.appUser.dto;

import com.asseco.assecotask.annotation.ForExcelImport;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AppUserDto {

    @ForExcelImport
    private Long id;
    @ForExcelImport
    private String name;
    @ForExcelImport
    private String lastName;
    @ForExcelImport
    private String personalIdentificationNumber;
    @ForExcelImport
    private List<ContactInfoDTO> contactInfoList;

}
