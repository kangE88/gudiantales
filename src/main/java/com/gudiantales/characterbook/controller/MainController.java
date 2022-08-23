package com.gudiantales.characterbook.controller;

import com.gudiantales.characterbook.character.CharacterStatus;
import com.gudiantales.characterbook.character.PropertyType;
import com.gudiantales.characterbook.example.Lambda;
import com.gudiantales.characterbook.service.CharacterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {
    private final CharacterService characterService;

    @GetMapping("/")
    public String index(){
        log.info("main controller");
//        CharacterStatus cs = new CharacterStatus();
//        cs.setName("티니아");
//        cs.setPropertyType(PropertyType.EARTH);
//        cs.setWeapon("사릉가");
//
//        characterService.saveCharacter(cs); // 저장

        return "index";

    }

/*    @GetMapping("/addCharacter")
    public String addCharacter(){
        CharacterStatus cs = new CharacterStatus();
        cs.setName("티니아");
        cs.setPropertyType(PropertyType.EARTH);
        cs.setWeapon("사릉가");

        characterService.saveCharacter(cs); // 저장

        return "character/addCharacter";
    }*/

    @GetMapping("/selectCharacter")
    public String selectCharacter(Model model) {
        List<CharacterStatus> characters = characterService.findCharacters();

        model.addAttribute("characters", characters);
        log.debug("characters::", characters);
        return "character/characterList";
    }


}
