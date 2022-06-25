package com.gudiantales.characterbook.example.Optional;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter @Setter
public class Progress {
    private Duration studyDuration;

    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private boolean finished;
}