package com.asseco.assecotask.appUser.repostiory;

import com.asseco.assecotask.appUser.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {


    @Query("SELECT a FROM AppUser a")
    Page<AppUser> findAllPageable(Pageable pageable);

    @Query("SELECT a.id FROM AppUser a")
    Page<Long> findAllUserIdsPageable(Pageable pageable);

    @Query("SELECT a FROM AppUser a " +
            "LEFT JOIN FETCH a.contactInfos contactInfo " +
            "LEFT JOIN FETCH contactInfo.contactType " +
            "WHERE a.id IN :userIds " +
            "ORDER BY a.id ASC")
    List<AppUser> findUsersWithContacts(List<Long> userIds);

    @Query("SELECT a FROM AppUser a WHERE a.personalIdentificationNumber = ?1")
    Optional<AppUser> findByIdentificationNumberWithoutContacts(String identificationNumber);

    @Query("SELECT a FROM AppUser a " +
            "LEFT JOIN FETCH a.contactInfos contactInfo " +
            "LEFT JOIN FETCH contactInfo.contactType " +
            "WHERE a.personalIdentificationNumber = ?1")
    Optional<AppUser> findByIdentificationNumberWithContacts(String identificationNumber);


}
