package com.kt.geniestore.meta.apkmanagement.common.response;

import com.kt.geniestore.meta.apkmanagement.common.BaseErrorMsg;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse {
    private String resultCd;
    private String resultMsg;

    public CommonResponse() {
        this.resultCd = BaseErrorMsg.OK.getErrorCode();
        this.resultMsg = BaseErrorMsg.OK.getErrorMsg();
    }

    public CommonResponse(String cd, String msg) {
        this.resultCd = cd;
        this.resultMsg = msg;
    }
}
