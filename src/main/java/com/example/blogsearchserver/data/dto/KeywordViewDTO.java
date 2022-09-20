package com.example.blogsearchserver.data.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class KeywordViewDTO {

    private String keyword;
    private long views;
}
