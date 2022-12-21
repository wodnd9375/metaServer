package com.kt.geniestore.meta.apkmanagement.Exception;

import com.kt.geniestore.meta.apkmanagement.common.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {
    // Exception : 통합 바코드 중복 생성
    @ExceptionHandler(DuplicateAppException.class)
    protected ResponseEntity<CommonResponse> handleDuplicateApp(DuplicateAppException e) {
        HttpStatus status = HttpStatus.OK;
        final CommonResponse response = new CommonResponse(status.value() +"", e.getMessage());
        return ResponseEntity.status(status).body(response);
    }
}
