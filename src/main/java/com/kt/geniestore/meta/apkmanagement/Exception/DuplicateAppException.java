package com.kt.geniestore.meta.apkmanagement.Exception;

public class DuplicateAppException extends RuntimeException {
    private static final long serialVersionUID = 2036611901536356933L;

    public DuplicateAppException() {
        super("이미 등록된 앱입니다.");
    }
}
