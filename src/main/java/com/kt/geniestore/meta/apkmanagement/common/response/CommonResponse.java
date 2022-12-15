package com.kt.geniestore.meta.apkmanagement.common.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kt.geniestore.meta.apkmanagement.common.BaseErrorMsg;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse {
    private int resultCd;
    private String resultMsg;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String trxid;

    public CommonResponse() {
        this.resultCd = BaseErrorMsg.OK.getErrorCode();
        this.resultMsg = BaseErrorMsg.OK.getErrorMsg();
    }

    public CommonResponse(int cd, String msg) {
        this.resultCd = cd;
        this.resultMsg = msg;
    }
}
