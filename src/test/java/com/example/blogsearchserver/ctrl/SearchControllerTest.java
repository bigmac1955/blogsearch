package com.example.blogsearchserver.ctrl;

import com.example.blogsearchserver.common.Constants;
import com.example.blogsearchserver.data.dto.BlogResponseDTO;
import com.example.blogsearchserver.data.dto.BlogSearchRequestDTO;
import com.example.blogsearchserver.data.dto.KeywordViewDTO;
import com.example.blogsearchserver.service.SearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SearchController.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Autowired
    private ObjectMapper objectMapper;

    private String fromService = "Kakao";
    private String keyword = "노정욱";
    private String sort = Constants.SORT_TYPE_ACCURACY;
    private Integer page = 1;
    private Integer size = 1;

    @Test
    @DisplayName("컨트롤러 테스트 /api/v1/search/keyword")
    public void testSearchByKeyword() throws Exception {

        //given
        BlogSearchRequestDTO requestDTO = BlogSearchRequestDTO.builder()
                .query(keyword)
                .sort(sort)
                .page(page)
                .size(size)
                .build();

        given(searchService.searchByKeyword(requestDTO))
                .willReturn(BlogResponseDTO.builder()
                        .fromService(fromService)
                        .build());

        //when && then
        mockMvc.perform(get("/api/v1/search/keyword")
                        .param("query", keyword)
                        .param("sort", sort)
                        .param("page", page.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(BlogResponseDTO.builder()
                        .fromService(fromService)
                        .build())));

        //then
        then(searchService)
                .should(only())
                .searchByKeyword(requestDTO);
    }

    @Test
    @DisplayName("컨트롤러 테스트 /api/v1/search/keyword/rank")
    public void testSearchKeywordViewList() throws Exception {

        //given
        given(searchService.searchKeywordViewList())
                .willReturn(Collections.singletonList(KeywordViewDTO.builder()
                        .keyword(keyword)
                        .views(1)
                        .build()));

        //when && then
        mockMvc.perform(get("/api/v1/search/keyword/rank"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(KeywordViewDTO.builder()
                        .keyword(keyword)
                        .views(1)
                        .build()))));

        //then
        then(searchService)
                .should(only())
                .searchKeywordViewList();

    }
}
