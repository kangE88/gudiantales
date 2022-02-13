package com.gudiantales.characterbook.repository;

import com.gudiantales.characterbook.character.CharacterStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CharacterRepository {

    private final EntityManager em;

    public void save(CharacterStatus characterStatus) {
        if (characterStatus.getId() == null) {
            em.persist(characterStatus);
        }else{
            em.merge(characterStatus);
        }
    }

    public CharacterStatus findOne(Long id) {
        return em.find(CharacterStatus.class, id);
    }

    public List<CharacterStatus> findALl(){
        return em.createQuery("select c from CharacterStatus c", CharacterStatus.class).getResultList();
    }

}
