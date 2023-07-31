package com.asseco.assecotask.contactType.service;

import com.asseco.assecotask.contactType.entity.ContactType;
import com.asseco.assecotask.contactType.repository.ContactTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactTypeService {

    private final ContactTypeRepository contactTypeRepository;

    public ContactTypeService(ContactTypeRepository contactTypeRepository) {
        this.contactTypeRepository = contactTypeRepository;
    }

    public List<ContactType> getAll() {
        return contactTypeRepository.findAll();
    }

    public void createContactType(String contactType) {

        if (contactTypeRepository.findContactTypeByName(contactType.toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("This contact type  already exist !");
        }

        ContactType contactTypeBuild = ContactType.builder().contactTypeName(contactType).build();
        contactTypeRepository.save(contactTypeBuild);

    }

    public ContactType getContactTypeById(Long contactTypeId) {
        return contactTypeRepository.findContactTypeById(contactTypeId).orElseThrow(() -> new IllegalArgumentException("This contact type doesn't exist !"));
    }
}
