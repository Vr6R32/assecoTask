package com.asseco.assecotask.contactType.repository;

import com.asseco.assecotask.contactType.entity.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactTypeRepository extends JpaRepository<ContactType, Long> {

    @Query("select c from ContactType c where c.contactTypeName = ?1")
    Optional<ContactType> findContactTypeByName(String contactType);

    @Query("select c from ContactType c where c.id = ?1")
    Optional<ContactType> findContactTypeById(Long id);

}
