package com.asseco.assecotask.appUser.controller;

import com.asseco.assecotask.appUser.dto.AppUserDto;
import com.asseco.assecotask.appUser.dto.UserRegistrationRequest;
import com.asseco.assecotask.appUser.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("with-contacts")
    @Operation(summary = "Get all users with contact info types", description = "Returns all users with their specified contact types info")
    public Page<AppUserDto> getAllUsersWithContactTypes(@RequestParam(required = false) Integer pageNumber,
                                                        @RequestParam(required = false) Integer pageSize) {
        return appUserService.getAllUsersWithContactTypes(pageNumber, pageSize);
    }

    @GetMapping("without-contacts")
    @Operation(summary = "Get all users without contact info types", description = "Returns all users without their specified contact types info")
    public Page<AppUserDto> getAllUsersWithOutContactTypes(@RequestParam(required = false) Integer pageNumber) {
        return appUserService.getAllUsersWithOutContactTypes(pageNumber);
    }

    @GetMapping(value = "{personalIdentificationNumber}/without-contacts")
    @Operation(summary = "Get single user without contact info types", description = "Returns single user without their specified contact types info")
    public AppUserDto getSpecifiedUserWithOutContactTypes(@PathVariable String personalIdentificationNumber) {
        return appUserService.getSpecifiedUserWithOutContactTypes(personalIdentificationNumber);
    }

    @GetMapping(value = "{personalIdentificationNumber}/with-contacts")
    @Operation(summary = "Get single user with contact info types", description = "Returns single user with their specified contact types info")
    public AppUserDto getSpecifiedUserWithContactTypes(@PathVariable String personalIdentificationNumber) {
        return appUserService.getSpecifiedUserWithContactTypes(personalIdentificationNumber);
    }

    @PostMapping
    @Operation(summary = "Create new user", description = "Creates new user and inserts into a database if validation is positive")
    public String createUser(@Valid @RequestBody UserRegistrationRequest appUser) {
        appUserService.insertNewUser(appUser);
        return "Success";
    }

    @PostMapping("contactInfo")
    @Operation(summary = "Create new contact info type for user", description = "Creates new specified contact info type for a specified user")
    public String insertNewContactInfo(@RequestParam String personalIdentificationNumber,
                                       @RequestParam Long contactTypeId,
                                       @RequestParam String contactValue) {
        return appUserService.insertNewContactInfo(personalIdentificationNumber, contactTypeId, contactValue);
    }

    @DeleteMapping("contactInfo")
    @Operation(summary = "Delete contact info type of an user", description = "Removes specified contact info type for specified user")
    public String deleteContactInfo(@RequestParam String personalIdentificationNumber,
                                    @RequestParam Long contactTypeId) {
        return appUserService.deleteContactInfo(personalIdentificationNumber, contactTypeId);
    }
}
