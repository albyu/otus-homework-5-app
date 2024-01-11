package ru.boldyrev.otus.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionNotFoundException  extends Exception {
    @Getter
    private final String message;
}
