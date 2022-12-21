package com.kt.geniestore.meta.apkmanagement.common;

import lombok.Getter;

@Getter
public enum BaseErrorMsg {
    OK(200, "200", "Success");

    private int statusCode;
    private String errorCode;
    private String errorMsg;

    BaseErrorMsg(final int statusCode, final String errorCode, final String errorMsg) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}