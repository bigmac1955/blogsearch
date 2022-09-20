package com.example.blogsearchserver.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ServiceApiUriEnum {
    KAKAO_BLOG_SEARCH_URI("/v2/search/blog"),
    NAVER_BLOG_SEARCH_URI("/v1/search/blog.json");

    @Getter
    private String uri;
}
