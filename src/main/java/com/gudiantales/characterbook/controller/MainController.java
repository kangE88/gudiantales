package com.gudiantales.characterbook.controller;

import com.gudiantales.characterbook.character.CharacterStatus;
import com.gudiantales.characterbook.character.PropertyType;
import com.gudiantales.characterbook.service.CharacterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManagerFactory;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MainController {
    private final CharacterService characterService;

    @GetMapping("/home")
    public String home(){
        log.info("main controller");
        CharacterStatus cs = new CharacterStatus();
        cs.setPropertyType(PropertyType.DARK);

        cs.setName("티니아");
        cs.setName("티니아2");
        cs.setPropertyType(PropertyType.EARTH);
        cs.setWeapon("사릉가");
//        EntityManagerFactory emf = new

        characterService.saveCharacter(cs); // 저장

        return "123Hello world!!!!!";

    }
}
