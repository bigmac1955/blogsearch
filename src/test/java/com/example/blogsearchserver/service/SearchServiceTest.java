package com.example.blogsearchserver.service;

import com.example.blogsearchserver.common.BlogSearchServerException;
import com.example.blogsearchserver.common.Constants;
import com.example.blogsearchserver.connector.KakaoConnector;
import com.example.blogsearchserver.data.dto.BlogResponseDTO;
import com.example.blogsearchserver.data.dto.BlogSearchRequestDTO;
import com.example.blogsearchserver.data.dto.KeywordViewDTO;
import com.example.blogsearchserver.data.dto.KeywordViewMapper;
import com.example.blogsearchserver.data.entity.KeywordView;
import com.example.blogsearchserver.data.repository.KeywordViewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;


@SpringBootTest(classes = SearchService.class)
public class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @MockBean
    private KakaoConnector kakaoConnector;

    @MockBean
    private KeywordViewRepository keywordViewRepository;

    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations zSetOperations;

    private String keyword = "노정욱";
    private String sort = Constants.SORT_TYPE_ACCURACY;
    private Integer page = 1;
    private Integer size = 1;
    private String fromService = "Kakao";

    @Test
    @DisplayName("키워드로 검색 테스트")
    public void testSearchByKeyword() throws BlogSearchServerException {

        //given
        BlogSearchRequestDTO requestDTO = BlogSearchRequestDTO.builder()
                .query(keyword)
                .sort(sort)
                .page(page)
                .size(size)
                .build();

        BlogResponseDTO responseDTO = BlogResponseDTO.builder()
                .totalElements(0)
                .pageNumber(1)
                .pageSize(1)
                .lastItem(true)
                .fromService(fromService)
                .build();

        given(kakaoConnector.findBlogByKeyword(requestDTO)).willReturn(responseDTO);
        given(redisTemplate.opsForZSet()).willReturn(zSetOperations); // do nothing

        //when
        BlogResponseDTO result = searchService.searchByKeyword(requestDTO);

        //then
        assertThat(result).isEqualTo(responseDTO);
        then(kakaoConnector).should(only()).findBlogByKeyword(requestDTO);
    }

    @Test
    @DisplayName("파라미터 validation 테스트 - Sort Type invalid exception 테스트")
    public void testCheckParamsSortTypeInvalid() {

        // 허용되는 Sort Type : "accuracy", "recency" | 테스트 Sort Type : "invalid_something"
        //given
        BlogSearchRequestDTO requestDTO = BlogSearchRequestDTO.builder()
                .query(keyword)
                .sort("invalid_something")
                .page(page)
                .size(size)
                .build();

        //when 
        // reflection 에서 에러 발생시 UndeclaredThrowableException 으로 넘어옴
        // 해당 private 메서드 내에서 exception 이 발생하는지가 중요하므로 exception 발생 여부만 체크
        UndeclaredThrowableException exception =
                assertThrows(UndeclaredThrowableException.class,
                        () -> ReflectionTestUtils.invokeMethod(searchService, "checkParamsValid", requestDTO));

        //then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("파라미터 validation 테스트 - Page invalid exception 테스트")
    public void testCheckParamsPageInvalid() {

        // 허용되는 page : 1~50 | 테스트 page : -1
        //given
        BlogSearchRequestDTO requestDTO = BlogSearchRequestDTO.builder()
                .query(keyword)
                .sort(sort)
                .page(-1)
                .size(size)
                .build();

        UndeclaredThrowableException exception;

        //when
        exception = assertThrows(UndeclaredThrowableException.class,
                        () -> ReflectionTestUtils.invokeMethod(searchService, "checkParamsValid", requestDTO));

        //then
        assertInstanceOf(RuntimeException.class, exception);

        // 허용되는 page : 1~50 | 테스트 page : 500
        //given
        BlogSearchRequestDTO requestDTO2 = BlogSearchRequestDTO.builder()
                .query(keyword)
                .sort(sort)
                .page(500)
                .size(size)
                .build();

        //when
        exception = assertThrows(UndeclaredThrowableException.class,
                        () -> ReflectionTestUtils.invokeMethod(searchService, "checkParamsValid", requestDTO2));

        //then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("파라미터 validation 테스트 - Size invalid exception 테스트")
    public void testCheckParamsSizeInvalid() {

        // 허용되는 size : 1~50 | 테스트 size : -1
        //given
        BlogSearchRequestDTO requestDTO = BlogSearchRequestDTO.builder()
                .query(keyword)
                .sort(sort)
                .page(page)
                .size(-1)
                .build();

        UndeclaredThrowableException exception;

        //when
        exception = assertThrows(UndeclaredThrowableException.class,
                () -> ReflectionTestUtils.invokeMethod(searchService, "checkParamsValid", requestDTO));

        //then
        assertInstanceOf(RuntimeException.class, exception);

        // 허용되는 page : 1~50 | 테스트 size : 500
        //given
        BlogSearchRequestDTO requestDTO2 = BlogSearchRequestDTO.builder()
                .query(keyword)
                .sort(sort)
                .page(page)
                .size(500)
                .build();

        //when
        exception = assertThrows(UndeclaredThrowableException.class,
                () -> ReflectionTestUtils.invokeMethod(searchService, "checkParamsValid", requestDTO2));

        //then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("검색어 순위 테스트")
    public void testSearchKeywordViewList() {

        //given
        List<KeywordView> keywordViews = new ArrayList<>();
        KeywordView keywordView = new KeywordView();
        keywordView.setKeyword(keyword);
        keywordView.setViews(1L);
        keywordViews.add(keywordView);

        given(keywordViewRepository.findTop10ByOrderByViewsDesc())
                .willReturn(Optional.of(keywordViews));

        //when
        List<KeywordViewDTO> result = searchService.searchKeywordViewList();

        //then
        assertThat(result).isEqualTo(KeywordViewMapper.MAPPER.toDto(keywordViews));
        then(keywordViewRepository).should(only()).findTop10ByOrderByViewsDesc();
    }

    @Test
    @DisplayName("검색어 검색횟수 테스트")
    public void testGetViewCnt() {

        //given
        KeywordView keywordView = new KeywordView();
        keywordView.setKeyword(keyword);
        keywordView.setViews(1L);

        given(keywordViewRepository.findByKeyword(keyword))
                .willReturn(keywordView);

        //when
        Long viewCnt = searchService.getViewCnt(keyword);

        //then
        assertThat(viewCnt).isEqualTo(keywordView.getViews());
        then(keywordViewRepository).should(only()).findByKeyword(keyword);
    }
}
