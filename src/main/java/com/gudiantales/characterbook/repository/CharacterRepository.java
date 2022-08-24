package com.gudiantales.characterbook.repository;

import com.gudiantales.characterbook.character.CharacterStatus;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CharacterRepository {

    private final EntityManager em;

    public void save(CharacterStatus characterStatus) {
        if (characterStatus.getId() == null) {
            log.info("getId NULL");
            log.info(characterStatus.getPropertyType().toString());
            em.persist(characterStatus);
        }else{
            log.info("getId NOT NULL");
            log.info(characterStatus.toString());
            em.merge(characterStatus);
        }
    }

    public CharacterStatus findOne(Long id) {
        return em.find(CharacterStatus.class, id);
    }

    public List<CharacterStatus> findALL(){
        return em.createQuery("select c from CharacterStatus c", CharacterStatus.class).getResultList();
    }

}
