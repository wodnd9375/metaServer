package com.kt.geniestore.meta.apkmanagement.common;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomException extends Exception {
    private int errorStatus;
    private String errorMessage;

    public CustomException(String errorMessage, int errorStatus) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorStatus = errorStatus;
    }
}
