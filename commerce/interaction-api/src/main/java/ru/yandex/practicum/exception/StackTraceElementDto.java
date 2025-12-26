package ru.yandex.practicum.exception;

import lombok.Data;

@Data
public class StackTraceElementDto {
    private String classLoaderName;
    private String moduleName;
    private String moduleVersion;
    private String methodName;
    private String fileName;
    private Integer lineNumber;
    private String className;
    private boolean nativeMethod;
}
