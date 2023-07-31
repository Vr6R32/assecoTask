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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class AppUserServiceTest {
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private ContactTypeService contactTypeService;
    @Mock
    private ContactInfoRepository contactInfoRepository;
    @Captor
    private ArgumentCaptor<AppUser> appUserArgumentCaptor;
    private AppUserService underTest;

    @BeforeEach
    @Deprecated
    public void setup() {
        MockitoAnnotations.initMocks(this);
        underTest = new AppUserService(appUserRepository, contactTypeService, contactInfoRepository);
    }

    @Test
    @Deprecated
    public void shouldSaveNewUserHappyPath() {

        //GIVEN
        String name = "Mockey";
        String lastName = "Mock";
        String personalIdentificationNumber = "11111111111";

        AppUser user = AppUser.builder().name(name).lastName(lastName).personalIdentificationNumber(personalIdentificationNumber).build();

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setPersonalIdentificationNumber(personalIdentificationNumber);
        request.setName(name);
        request.setLastName(lastName);

        //WHEN
        underTest.insertNewUser(request);
        given(appUserRepository.findByIdentificationNumberWithoutContacts(personalIdentificationNumber))
                .willReturn(Optional.empty());

        //THEN
        then(appUserRepository).should().save(appUserArgumentCaptor.capture());
        verify(appUserRepository).findByIdentificationNumberWithoutContacts(personalIdentificationNumber);


        assertThat(appUserArgumentCaptor.getValue()).isInstanceOf(AppUser.class);
        assertThat(appUserArgumentCaptor.getValue()).isEqualToIgnoringGivenFields(user, "creationTime");
    }

    @Test
    @Deprecated
    public void shouldNotSaveNewUserBecausePersonalIdentificationNumberAlreadyExist() {

        //GIVEN
        String name = "Mockey";
        String lastName = "Mock";
        String newUserPersonalIdentificationNumber = "11111111111";
        String existingUserPersonalIdentificationNumber = "11111111111";

        AppUser existingUser = AppUser.builder().name("name").lastName("lastName").personalIdentificationNumber(existingUserPersonalIdentificationNumber).build();

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setPersonalIdentificationNumber(newUserPersonalIdentificationNumber);
        request.setName(name);
        request.setLastName(lastName);

        //WHEN
        //THEN

        given(appUserRepository.findByIdentificationNumberWithoutContacts(newUserPersonalIdentificationNumber))
                .willReturn(Optional.of(existingUser));


        assertThatThrownBy(() -> underTest.insertNewUser(request)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User with this personal identification number already exists !");
    }

    @Test
    public void shouldNotSaveNewUserBecauseOfWrongPersonalIdentificationNumber() {

        //GIVEN
        String name = "Mockey";
        String lastName = "Mock";
        String userPersonalIdentificationNumber = "123";

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setPersonalIdentificationNumber(userPersonalIdentificationNumber);
        request.setName(name);
        request.setLastName(lastName);

        //WHEN
        when(appUserRepository.findByIdentificationNumberWithoutContacts(userPersonalIdentificationNumber))
                .thenReturn(Optional.empty());
        //THEN
        assertThatThrownBy(() -> underTest.insertNewUser(request)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Personal identification number must have exactly 11 digits !");
    }

    @Test
    public void shouldMapUserToDTOWithContactTypes() {

        // GIVEN
        String name = "Mockey";
        String lastName = "Mock";
        String userPersonalIdentificationNumber = "11111111111";

        ContactType contactType = ContactType.builder().contactTypeName("email").build();
        ContactInfo contactInfo = ContactInfo.builder().contactType(contactType).contactInfoValue("test@test.pl").build();

        AppUser user = AppUser.builder()
                .name(name)
                .lastName(lastName)
                .personalIdentificationNumber(userPersonalIdentificationNumber)
                .contactInfos(Set.of(contactInfo))
                .build();

        // WHEN
        AppUserDto result = underTest.mapToDtoWithRelations(user);

        // THEN
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(lastName, result.getLastName());
        assertEquals(userPersonalIdentificationNumber, result.getPersonalIdentificationNumber());
        assertThat(result.getContactInfoList()).isNotEmpty();
        assertThat(result).isInstanceOf(AppUserDto.class);
    }


    @Test
    public void shouldMapContactInfoToDTO() {

        // GIVEN

        ContactType contactType = ContactType.builder().contactTypeName("email").build();
        ContactInfo contactInfo = ContactInfo.builder().contactType(contactType).contactInfoValue("test@test.pl").build();
        Set<ContactInfo> contactInfoSet = Set.of(contactInfo);

        // WHEN
        // THEN
        assertThat(underTest.mapToContactInfoDTO(contactInfoSet).get(0)).isInstanceOf(ContactInfoDTO.class);
    }
}
