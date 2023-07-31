package com.asseco.assecotask.appUser.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ContactInfoDTO {

    private String contactType;
    private String contactValue;

}
