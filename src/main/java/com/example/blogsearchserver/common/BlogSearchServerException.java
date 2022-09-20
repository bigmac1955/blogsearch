package com.example.blogsearchserver.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class BlogSearchServerException extends Exception{

    private ErrorCodeEnum errorCode;
    private String serviceErrorInfo;
}
