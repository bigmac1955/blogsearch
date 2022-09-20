package com.example.blogsearchserver.common;

import lombok.*;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorInfo {

    @NonNull
    private String errorCode;
    @NonNull
    private String errorString;

    private String serviceErrorInfo;

}
