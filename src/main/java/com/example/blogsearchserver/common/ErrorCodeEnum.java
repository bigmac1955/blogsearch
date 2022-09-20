package com.example.blogsearchserver.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST,"1001", "InvalidArgument Error"),
    CONNECTOR_SERVICE_ERROR(HttpStatus.BAD_REQUEST, "5000", "Connector Service Error"),
    ETC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"9000", "기타 서버 에러"),
            ;

    private HttpStatus httpStatus;
    private String codeNum;
    private String description;
}
