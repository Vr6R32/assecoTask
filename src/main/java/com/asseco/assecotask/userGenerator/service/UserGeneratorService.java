package com.asseco.assecotask.userGenerator.service;

import com.asseco.assecotask.appUser.entity.AppUser;
import com.asseco.assecotask.appUser.entity.ContactInfo;
import com.asseco.assecotask.appUser.repostiory.AppUserRepository;
import com.asseco.assecotask.contactType.entity.ContactType;
import com.asseco.assecotask.contactType.repository.ContactTypeRepository;
import com.asseco.assecotask.userGenerator.util.PersonalNumberGenerator;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserGeneratorService {

    private final AppUserRepository appUserRepository;
    private final ContactTypeRepository contactTypeRepository;
    private Faker faker = null;
    private Random random = null;

    public UserGeneratorService(AppUserRepository appUserRepository, ContactTypeRepository contactTypeRepository) {
        this.appUserRepository = appUserRepository;
        this.contactTypeRepository = contactTypeRepository;
    }

    public void generateUsers(int numberToGenerate) {

        long start = System.currentTimeMillis();

        faker = new Faker();
        random = new Random();
        PersonalNumberGenerator personalNumberGenerator = new PersonalNumberGenerator();

        List<AppUser> appUserList = new LinkedList<>();
        List<ContactType> contactTypesList = contactTypeRepository.findAll();


        for (int i = 1; i <= numberToGenerate; i++) {

            Set<ContactInfo> contactInfoList = new HashSet<>();

            int randomInt = getRandomIntInRange(2, 4);

            for (int j = 0; j < randomInt; j++) {
                ContactInfo randomContactInfo = generateRandomContactInfo(contactTypesList);
                contactInfoList.add(randomContactInfo);
            }

            String name = faker.name().firstName();
            String lastName = faker.name().lastName();

            AppUser user = AppUser.builder()
                    .name(name)
                    .lastName(lastName)
                    .personalIdentificationNumber(personalNumberGenerator.generatePersonalIdentificationNumber())
                    .contactInfos(contactInfoList)
                    .creationTime(LocalDateTime.now())
                    .build();

            for (ContactInfo contactInfo : contactInfoList) {
                contactInfo.setAppUser(user);
            }
            appUserList.add(user);
        }
        appUserRepository.saveAll(appUserList);

        long end = System.currentTimeMillis();
        long elapsedTimeMs = end - start;
        double elapsedTimeSec = elapsedTimeMs / 1000.0;
        System.out.println(elapsedTimeSec + "s");
    }


    private int getRandomIntInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private ContactInfo generateRandomContactInfo(List<ContactType> contactTypesList) {
        ContactInfo randomContactInfo;

        int randomIndex = random.nextInt(contactTypesList.size());
        ContactType randomContactType = contactTypesList.get(randomIndex);

        if (randomContactType.getContactTypeName().contains("address")) {
            randomContactInfo = ContactInfo.builder().contactInfoValue(faker.address().fullAddress()).contactType(randomContactType).build();
        } else if (randomContactType.getContactTypeName().contains("phone")) {
            randomContactInfo = ContactInfo.builder().contactInfoValue(String.valueOf(faker.phoneNumber().cellPhone())).contactType(randomContactType).build();
        } else if (randomContactType.getContactTypeName().contains("email")) {
            randomContactInfo = ContactInfo.builder().contactInfoValue(String.valueOf(faker.internet().emailAddress())).contactType(randomContactType).build();
        } else {
            randomContactInfo = ContactInfo.builder().contactInfoValue(String.valueOf(faker.internet().ipV4Address())).contactType(randomContactType).build();
        }
        return randomContactInfo;
    }
}
