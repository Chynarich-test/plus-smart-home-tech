package ru.yandex.practicum.exception;

import lombok.Data;

import java.util.List;

@Data
public class ThrowableInfo {
    private List<StackTraceElementDto> stackTrace;
    private String message;
    private String localizedMessage;
}
