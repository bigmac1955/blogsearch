package com.example.blogsearchserver.data.domain;

import lombok.Data;

@Data
public class KakaoBlogDocument {

    private String title;
    private String contents;
    private String url;
    private String blogname;
    private String thumbnail;
    private String datetime;
}