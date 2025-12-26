package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ErrorResponseFabric {
    public static ResponseEntity<ErrorResponse> responseFabric(RuntimeException e, String userMessage,
                                                               HttpStatus status) {
        ErrorResponse response = new ErrorResponse();

        response.setMessage(e.getMessage());
        response.setLocalizedMessage(e.getLocalizedMessage());
        response.setUserMessage(userMessage);

        response.setHttpStatus(status.value() + " " + status.name());

        List<StackTraceElementDto> stackTrace = stackTraceFabric(e.getStackTrace());

        response.setStackTrace(stackTrace);

        if (e.getCause() != null) {
            ThrowableInfo causeInfo = new ThrowableInfo();
            causeInfo.setMessage(e.getCause().getMessage());
            causeInfo.setLocalizedMessage(e.getCause().getLocalizedMessage());
            causeInfo.setStackTrace(stackTraceFabric(e.getCause().getStackTrace()));
            response.setCause(causeInfo);
        }

        return new ResponseEntity<>(response, status);
    }

    public static List<StackTraceElementDto> stackTraceFabric(StackTraceElement[] stes) {
        return Arrays.stream(stes)
                .map(ste -> {
                    StackTraceElementDto dto = new StackTraceElementDto();
                    dto.setMethodName(ste.getMethodName());
                    dto.setFileName(ste.getFileName());
                    dto.setLineNumber(ste.getLineNumber());
                    dto.setClassName(ste.getClassName());
                    dto.setNativeMethod(ste.isNativeMethod());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
