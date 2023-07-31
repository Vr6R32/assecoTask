package com.asseco.assecotask.appUser.entity;

import com.asseco.assecotask.contactType.entity.ContactType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "app_user_contacts")
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "contactType_id", foreignKey = @ForeignKey(name = "FK_contactType"))
    private ContactType contactType;
    private String contactInfoValue;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", foreignKey = @ForeignKey(name = "FK_appUser"))
    @JsonBackReference
    private AppUser appUser;

    @Override
    public String toString() {
        return "ContactInfo{" +
                "id=" + id +
                ", contactType=" + contactType +
                ", contactInfoValue='" + contactInfoValue + '\'' +
                ", appUserId=" + appUser.getId() +
                '}';
    }
}