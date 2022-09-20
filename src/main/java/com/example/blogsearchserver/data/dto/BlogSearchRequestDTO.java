package com.example.blogsearchserver.data.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class BlogSearchRequestDTO {

    /**
     * 검색을 원하는 질의어
     */
    String query;

    /**
     * 결과 정렬 방식
     */
    String sort;

    /**
     * 결과 페이지 번호 (1~50) 기본값 1
     */
    Integer page;

    /**
     * 한 페이지에 보여질 문서 수 (1~50) 기본값 10
     */
    Integer size;
}
