package com.gudiantales.characterbook.service;

import com.gudiantales.characterbook.character.CharacterStatus;
import com.gudiantales.characterbook.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CharacterService {
    private final CharacterRepository characterRepository;

    @Transactional
    public  void saveCharacter(CharacterStatus characterStatus) {
        characterRepository.save(characterStatus);
        log.debug("save compliate");
    }

    public List<CharacterStatus> findCharacters() {
        return characterRepository.findALL();
    }

    public CharacterStatus findOne(Long id) {
        return characterRepository.findOne(id);
    }
}
