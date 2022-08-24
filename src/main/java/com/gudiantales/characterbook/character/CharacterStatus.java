package com.gudiantales.characterbook.character;

import com.gudiantales.characterbook.common.CommonEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter @Setter
@ToString
@Table(name = "Character_Status")
public class CharacterStatus extends CommonEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String weapon;

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdDate;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastModifiedDate;
//
//    @Lob
//    private String description;

}
