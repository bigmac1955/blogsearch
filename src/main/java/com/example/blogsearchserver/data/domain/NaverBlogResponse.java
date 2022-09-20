package com.example.blogsearchserver.data.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class NaverBlogResponse {

    Integer total;
    Integer start;
    Integer display;
    private List<NaverBlogDocument> items;
    private String fromService = "Naver";
}
