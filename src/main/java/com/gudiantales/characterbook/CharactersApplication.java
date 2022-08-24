package com.gudiantales.characterbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CharactersApplication {
    public static void main(String[] args) {

//        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
//        applicationContext.getBean("characterService", CharacterService.class);
//        applicationContext.getBean("characterRepository", CharacterRepository.class);

        SpringApplication.run(CharactersApplication.class, args);
    }

}
