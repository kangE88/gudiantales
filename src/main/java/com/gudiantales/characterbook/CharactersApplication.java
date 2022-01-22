package com.gudiantales.characterbook;

import com.gudiantales.characterbook.character.CharacterStatus;
import com.gudiantales.characterbook.character.PropertyType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

//@SpringBootApplication
public class CharactersApplication {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        tx.begin();
        //code
        try{
            CharacterStatus characterStatus = new CharacterStatus();

            //characterStatus.setId(1L);
            characterStatus.setName("남기사");
            //characterStatus.setPropertyType(PropertyType.NONE);

            em.persist(characterStatus);

            CharacterStatus findCharacterStatus = em.find(CharacterStatus.class, 1L);
            System.out.println("findCharacter ::" + findCharacterStatus.getName());

            //수정
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember.Id >>" + findMember.getId());
//            System.out.println("findMember.name >>" + findMember.getName());
//
//            findMember.setName("kangA");

            //JPQL
//            List<Member> resultList = em.createQuery("select m from Member as m", Member.class).getResultList();
//
//            for (Member member : resultList) {
//                System.out.println("JpaMain.main member.name =" + member.getName());
//            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();


        //SpringApplication.run(CharacterbookApplication.class, args);
    }

}
