package com.gudiantales.characterbook.character;

import com.gudiantales.characterbook.common.CommonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class CharacterStatus extends CommonEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String weapon;

    private String nn;

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
