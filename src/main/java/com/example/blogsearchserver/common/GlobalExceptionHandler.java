package com.example.blogsearchserver.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 처리되지 않는 모든 에러 -> ErrorCodeEnum.ETC_ERROR 로 처리
     */
    @ExceptionHandler({Exception.class})
    public ErrorInfo handleAllDefaultException(final Exception ex, HttpServletResponse res) {

        ex.printStackTrace();
        res.setStatus(ErrorCodeEnum.ETC_ERROR.getHttpStatus().value());
        return new ErrorInfo(ErrorCodeEnum.ETC_ERROR.getCodeNum(), ErrorCodeEnum.ETC_ERROR.getDescription());
    }

    /**
     * webClient 에러시 -> ErrorCodeEnum.CONNECTOR_SERVICE_ERROR 로 처리
     */
    @ExceptionHandler({WebClientResponseException.class})
    public ErrorInfo handleIllegalStateException(final WebClientResponseException ex, HttpServletResponse res) {

        log.error(ex.getMessage());
        res.setStatus(ErrorCodeEnum.CONNECTOR_SERVICE_ERROR.getHttpStatus().value());
        return new ErrorInfo(ErrorCodeEnum.CONNECTOR_SERVICE_ERROR.getCodeNum(), ErrorCodeEnum.CONNECTOR_SERVICE_ERROR.getDescription(), ex.getMessage());
    }

    /**
     * 이미 정의된 에러 처리
     */
    @ExceptionHandler({BlogSearchServerException.class})
    public ErrorInfo handleBlogSearchServerException(final BlogSearchServerException ex, HttpServletResponse res){

        log.error(ex.getErrorCode().getCodeNum(), ex.getErrorCode().getDescription(), ex.getServiceErrorInfo());
        res.setStatus(ex.getErrorCode().getHttpStatus().value());
        return new ErrorInfo(ex.getErrorCode().getCodeNum(), ex.getErrorCode().getDescription(), ex.getServiceErrorInfo());
    }
}
