package com.example.blogsearchserver.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity(name = "keywordView")
public class KeywordView {

    @Id
    private String keyword;

    private Long views = 1L;

    public KeywordView (){}

    public KeywordView (String keyword){
        this.keyword = keyword;
    }

    public KeywordView (String keyword, Long views){
        this.keyword = keyword;
        this.views = views;
    }
}
