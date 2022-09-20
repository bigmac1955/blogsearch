package com.example.blogsearchserver.connector;

import com.example.blogsearchserver.data.dto.BlogResponseDTO;
import com.example.blogsearchserver.data.dto.BlogSearchRequestDTO;

public interface BlogConnector {

    BlogResponseDTO findBlogByKeyword(BlogSearchRequestDTO requestDTO);
}
