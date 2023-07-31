package com.asseco.assecotask.userGenerator.controller;

import com.asseco.assecotask.userGenerator.service.UserGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UserGeneratorController {
    private final UserGeneratorService userGeneratorService;

    public UserGeneratorController(UserGeneratorService userGeneratorService) {
        this.userGeneratorService = userGeneratorService;
    }

    @PostMapping("generate-users")
    @Operation(summary = "Generate any number of users", description = "Generates a specified number of random users and inserts to a database")
    public void generateUsers(@RequestParam Integer numberToGenerate) {
        userGeneratorService.generateUsers(numberToGenerate);
    }
}
