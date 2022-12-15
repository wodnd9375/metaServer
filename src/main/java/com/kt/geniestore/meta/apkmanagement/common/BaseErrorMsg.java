package com.kt.geniestore.meta.apkmanagement.common;

public enum BaseErrorMsg {
    // Common
    OK(200, 200, "Success"),
    FailToUpload(200, 400, "Fail to upload");

    private int statusCode;
    private int errorCode;
    private String errorMsg;

    BaseErrorMsg(final int statusCode, final int errorCode, final String errorMsg) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public String toString() {
        return this.errorMsg;
    }
}

