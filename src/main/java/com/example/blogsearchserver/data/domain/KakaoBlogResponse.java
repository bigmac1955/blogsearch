package com.example.blogsearchserver.data.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class KakaoBlogResponse {

    private KakaoBlogMeta meta;
    private List<KakaoBlogDocument> documents;
    private String fromService = "Kakao";
}
