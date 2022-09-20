package com.example.blogsearchserver.data.domain;

import lombok.Data;

@Data
public class NaverBlogDocument {

    private String title;
    private String description;
    private String bloggername;
    private String bloggerlink;
    private String postdate;
}