package com.asseco.assecotask.contactType.controller;

import com.asseco.assecotask.contactType.entity.ContactType;
import com.asseco.assecotask.contactType.service.ContactTypeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/contactTypes")
public class ContactTypeController {

    private final ContactTypeService contactTypeService;

    public ContactTypeController(ContactTypeService contactTypeService) {
        this.contactTypeService = contactTypeService;
    }

    @GetMapping
    @Operation(summary = "Get all available contact types", description = "Returns all specified types of contact type")
    public List<ContactType> getAll() {
        return contactTypeService.getAll();
    }

    @PostMapping
    @Operation(summary = "Create new contact type", description = "Creates new form of a contact type and inserts into database")
    public String createContactType(@RequestParam String contactType) {
        contactTypeService.createContactType(contactType);
        return "Succes";
    }

}
