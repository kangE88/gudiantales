package com.gudiantales.characterbook.controller;

import com.gudiantales.characterbook.character.CharacterStatus;
import com.gudiantales.characterbook.character.PropertyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManagerFactory;

@RestController
@Slf4j
public class MainController {
    @GetMapping("/home")
    public String home(){
        log.info("main controller");
        CharacterStatus cs = new CharacterStatus();
        cs.setPropertyType(PropertyType.DARK);

        cs.setName("티니아");
        cs.setPropertyType(PropertyType.EARTH);
        cs.setWeapon("사릉가");
        //EntityManagerFactory emf = new


        return "123Hello world!!!!!";
    }
}
