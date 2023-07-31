package com.asseco.assecotask.appUser.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.pl.PESEL;

@Data
public class UserRegistrationRequest {

    @NotBlank(message = "Name cannot be empty !")
    private String name;
    @NotBlank(message = "Last name cannot be empty !")
    private String lastName;
    @PESEL(message = "Personal identification number is incorrect !")
    private String personalIdentificationNumber;

}
