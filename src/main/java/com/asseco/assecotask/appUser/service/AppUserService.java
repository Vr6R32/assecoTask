package com.asseco.assecotask.appUser.service;

import com.asseco.assecotask.appUser.dto.AppUserDto;
import com.asseco.assecotask.appUser.dto.ContactInfoDTO;
import com.asseco.assecotask.appUser.dto.UserRegistrationRequest;
import com.asseco.assecotask.appUser.entity.AppUser;
import com.asseco.assecotask.appUser.entity.ContactInfo;
import com.asseco.assecotask.appUser.repostiory.AppUserRepository;
import com.asseco.assecotask.appUser.repostiory.ContactInfoRepository;
import com.asseco.assecotask.contactType.entity.ContactType;
import com.asseco.assecotask.contactType.service.ContactTypeService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    public static final int PAGE_SIZE = 20;

    private final AppUserRepository appUserRepository;

    private final ContactTypeService contactTypeService;

    private final ContactInfoRepository contactInfoRepository;

    public AppUserService(AppUserRepository appUserRepository, ContactTypeService contactTypeService, ContactInfoRepository contactInfoRepository) {
        this.appUserRepository = appUserRepository;
        this.contactTypeService = contactTypeService;
        this.contactInfoRepository = contactInfoRepository;
    }


    public void insertNewUser(UserRegistrationRequest request) {

        String personalIdentificationNumber = request.getPersonalIdentificationNumber();
        Optional<AppUser> existingUser = appUserRepository.findByIdentificationNumberWithoutContacts(personalIdentificationNumber);

        if (existingUser.isPresent()) {
            throw new IllegalStateException("User with this personal identification number already exist !");
        }

        String personalIdentificationNumberStr = String.valueOf(request.getPersonalIdentificationNumber());

        if (personalIdentificationNumberStr.length() != 11) {
            throw new IllegalStateException("Personal identification number must have exactly 11 digits !");
        }

        AppUser user = AppUser.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .personalIdentificationNumber(personalIdentificationNumber)
                .creationTime(LocalDateTime.now())
                .build();

        appUserRepository.save(user);
    }

    public String deleteContactInfo(String personalIdentificationNumber, Long contactTypeId) {
        AppUser user = getAppUserWithContacts(personalIdentificationNumber);
        Set<ContactInfo> contactInfos = user.getContactInfos();
        for (ContactInfo contactInfo : contactInfos) {
            if (contactInfo.getId().equals(contactTypeId)) {
                deleteContactFromUser(user, contactInfo);
                contactInfoRepository.delete(contactInfo);
                return "Deleted !";
            }
        }
        throw new NoSuchElementException("This contact type doesn't exist !");
    }

    public String insertNewContactInfo(String personalIdentificationNumber, Long contactTypeId, String contactValue) {
        AppUser user = getAppUserWithContacts(personalIdentificationNumber);

        ContactType contactTypeByName = contactTypeService.getContactTypeById(contactTypeId);
        if (user.getContactInfos() == null) {
            user.setContactInfos(new HashSet<>());
        }

        Set<ContactType> contactTypes = new HashSet<>();

        Set<ContactInfo> contactInfos = user.getContactInfos();

        for (ContactInfo contactInfo : contactInfos) {
            ContactType contactType = contactInfo.getContactType();
            contactTypes.add(contactType);
        }

        if (contactTypes.contains(contactTypeByName)) {
            throw new IllegalArgumentException("This contact type is already inserted !");
        }

        ContactInfo newContactInfo = ContactInfo.builder()
                .contactType(contactTypeByName)
                .contactInfoValue(contactValue)
                .build();

        insertNewContactToUser(user, newContactInfo);

        contactInfoRepository.save(newContactInfo);

        return "Success !";
    }

    public Page<AppUserDto> getAllUsersWithContactTypes(Integer pageNumber, Integer pageSize) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        Pageable pageable = PageRequest.of(getPage(pageNumber), pageSize, sortById);

        Page<Long> userIdsPage = appUserRepository.findAllUserIdsPageable(pageable);

        List<Long> userIds = userIdsPage.getContent();

        List<AppUser> usersWithContacts = appUserRepository.findUsersWithContacts(userIds);

        List<AppUserDto> usersWithContactsDto = usersWithContacts.stream()
                .map(this::mapToDtoWithRelations)
                .collect(Collectors.toList());

        return new PageImpl<>(usersWithContactsDto, pageable, userIdsPage.getTotalElements());
    }

    public Page<AppUserDto> getAllUsersWithOutContactTypes(Integer pageNumber) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Page<AppUser> allPageable = appUserRepository.findAllPageable(PageRequest.of(getPage(pageNumber), PAGE_SIZE, sortById));
        return allPageable.map(this::mapToDtoWithOutRelations);
    }

    public AppUserDto getSpecifiedUserWithOutContactTypes(String personalIdentificationNumber) {
        AppUser user = getAppUserWithOutContacts(personalIdentificationNumber);
        return mapToDtoWithOutRelations(user);
    }

    public AppUserDto getSpecifiedUserWithContactTypes(String personalIdentificationNumber) {
        AppUser user = getAppUserWithContacts(personalIdentificationNumber);
        return mapToDtoWithRelations(user);
    }

    private AppUserDto mapToDtoWithOutRelations(AppUser user) {
        return AppUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .personalIdentificationNumber(user.getPersonalIdentificationNumber())
                .build();
    }

    public AppUserDto mapToDtoWithRelations(AppUser user) {
        return AppUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .personalIdentificationNumber(user.getPersonalIdentificationNumber())
                .contactInfoList(mapToContactInfoDTO(user.getContactInfos()))
                .build();
    }


    public List<ContactInfoDTO> mapToContactInfoDTO(Set<ContactInfo> contactInfoList) {
        List<ContactInfoDTO> contactInfoDTOList = new LinkedList<>();

        for (ContactInfo contactInfo : contactInfoList) {
            ContactInfoDTO contactInfoDTO = ContactInfoDTO.builder()
                    .contactType(contactInfo.getContactType().getContactTypeName())
                    .contactValue(contactInfo.getContactInfoValue())
                    .build();

            contactInfoDTOList.add(contactInfoDTO);
        }
        return contactInfoDTOList;
    }


    private AppUser getAppUserWithContacts(String personalIdentificationNumber) {
        return appUserRepository.findByIdentificationNumberWithContacts(personalIdentificationNumber)
                .orElseThrow(() -> new NoSuchElementException("User with this personal identification number doesn't exist !"));
    }

    private AppUser getAppUserWithOutContacts(String personalIdentificationNumber) {
        return appUserRepository.findByIdentificationNumberWithoutContacts(personalIdentificationNumber)
                .orElseThrow(() -> new NoSuchElementException("User with this personal identification number doesn't exist !"));
    }

    private int getPage(Integer pageNumber) {
        if (pageNumber == null) {
            pageNumber = 0;
        }
        return Math.max(pageNumber, 0);
    }


    private void insertNewContactToUser(AppUser user, ContactInfo contactInfo) {
        user.getContactInfos().add(contactInfo);
        contactInfo.setAppUser(user);
    }

    private void deleteContactFromUser(AppUser user, ContactInfo contactInfo) {
        user.getContactInfos().remove(contactInfo);
        contactInfo.setAppUser(null);
    }

}
