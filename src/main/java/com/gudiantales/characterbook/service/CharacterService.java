package com.gudiantales.characterbook.service;

import com.gudiantales.characterbook.character.CharacterStatus;
import com.gudiantales.characterbook.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CharacterService {
    private final CharacterRepository characterRepository;

    @Transactional
    public  void saveCharacter(CharacterStatus characterStatus) {
        characterRepository.save(characterStatus);
    }

    public List<CharacterStatus> findCharacters() {
        return characterRepository.findALl();
    }

    public CharacterStatus findOne(Long id) {
        return characterRepository.findOne(id);
    }
}
