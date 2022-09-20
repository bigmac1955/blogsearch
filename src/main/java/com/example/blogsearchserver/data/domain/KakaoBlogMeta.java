package com.example.blogsearchserver.data.domain;

import lombok.Data;

@Data
public class KakaoBlogMeta {

    private Integer total_count;
    private Integer pageable_count;
    private Boolean is_end;
}
