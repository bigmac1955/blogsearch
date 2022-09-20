package com.example.blogsearchserver.data.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@ToString(exclude = "documents")
public class BlogResponseDTO {

    private Integer totalElements;
    private Integer pageSize;
    private Integer pageNumber;

    private boolean lastItem;
    private String fromService;
    private List<BlogResponseDocumentDTO> documents;
}
