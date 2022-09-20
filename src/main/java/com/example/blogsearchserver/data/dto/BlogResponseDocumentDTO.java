package com.example.blogsearchserver.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BlogResponseDocumentDTO {

    private String title;
    private String contents;
    private String url;
    private String blogname;
    private String thumbnail;
    private String datetime;
}
