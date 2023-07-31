package com.asseco.assecotask.fileGenerator.controller;

import com.asseco.assecotask.fileGenerator.service.FileGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/files/generate-users-list")
public class FileGeneratorController {

    private final FileGeneratorService fileGeneratorService;

    public FileGeneratorController(FileGeneratorService fileGeneratorService) {
        this.fileGeneratorService = fileGeneratorService;
    }

    @GetMapping
    @Operation(summary = "Extract user list to a file", description = "Fetches user list from a database and extracts to an excel file")
    public ResponseEntity<byte[]> generateExcelUserList(@RequestParam(required = false) Integer pageSize) {
        return fileGeneratorService.generateExcelUserList(pageSize);
    }
}