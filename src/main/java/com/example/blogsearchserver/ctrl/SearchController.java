package com.example.blogsearchserver.ctrl;

import com.example.blogsearchserver.common.BlogSearchServerException;
import com.example.blogsearchserver.common.Constants;
import com.example.blogsearchserver.data.dto.BlogResponseDTO;
import com.example.blogsearchserver.data.dto.BlogSearchRequestDTO;
import com.example.blogsearchserver.data.dto.KeywordViewDTO;
import com.example.blogsearchserver.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 키워드로 블로그 검색
     *
     * @param query : 검색 키워드
     * @param sort : 정렬 방식 accuracy (default) / recency
     * @param page : 페이지 번호 1~50 (default 1)
     * @param size : 한 페이지에 보여질 문서 수 1~50 (default 10)
     * @return
     *      totalElements : 총 검색 결과 수
     *      pageSize : 현재 페이지 크기
     *      pageNumber : 현재 페이지 번호
     *      lastItem : 마지막 페이지일 경우 true
     *      fromService : 사용된 검색 서비스 Kakao (default) / Naver (Kakao 장애시)
     *      documents[] :
     *                  title : 글 제목
     *                  contents : 글 요약
     *                  url : 글 url
     *                  blogname : 블로그이름
     *                  thumbnail : 미리보기 이미지 URL
     *                  datetime : 작성시간 [YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz] (Naver 의 경우 [YYYY]-[MM]-[DD]T00:00.000+09:00)
     *
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/search/keyword")
    public BlogResponseDTO searchByKeyword(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "sort", required = false, defaultValue = Constants.SORT_TYPE_ACCURACY) String sort,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) throws BlogSearchServerException {

        BlogSearchRequestDTO requestDTO = BlogSearchRequestDTO.builder()
                .query(query)
                .sort(sort)
                .page(page)
                .size(size)
                .build();

        return searchService.searchByKeyword(requestDTO);
    }

    /**
     * 인기 검색어 목록 조회 (최대 10개 목록)
     *
     * @return
     *     [리스트]
     *     keyword : 검색어
     *     views : 검색횟수
     *     (검색어가 없으면 [] 리턴)
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/search/keyword/rank")
    public List<KeywordViewDTO> searchKeywordViewList() {

        return searchService.searchKeywordViewList();
    }
}
