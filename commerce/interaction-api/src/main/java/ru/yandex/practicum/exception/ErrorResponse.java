package ru.yandex.practicum.exception;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private ThrowableInfo cause;
    private List<StackTraceElementDto> stackTrace;
    private String httpStatus;
    private String userMessage;
    private String message;
    private List<ThrowableInfo> suppressed;
    private String localizedMessage;
}
