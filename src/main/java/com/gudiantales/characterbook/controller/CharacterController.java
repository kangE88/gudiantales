package com.gudiantales.characterbook.controller;

import com.gudiantales.characterbook.character.CharacterStatus;
import com.gudiantales.characterbook.service.CharacterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class CharacterController {
    private CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping("/character/addCharacter")
    public String addCharacter(Model model) {
        log.info("Character controller");
        model.addAttribute("characterStatus", new CharacterStatus());
        return "character/addCharacter";
    }
    @PostMapping("/character/addCharacter")
    public String addCharacter(CharacterStatus form) {

        CharacterStatus characterStatus = new CharacterStatus();
        characterStatus.setName(form.getName());
        characterStatus.setWeapon(form.getWeapon());
        characterStatus.setPropertyType(form.getPropertyType());

        characterService.saveCharacter(characterStatus);

        return "index";
    }
}