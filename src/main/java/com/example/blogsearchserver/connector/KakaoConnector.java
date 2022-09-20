package com.example.blogsearchserver.connector;

import com.example.blogsearchserver.common.ServiceApiUriEnum;
import com.example.blogsearchserver.data.domain.KakaoBlogResponse;
import com.example.blogsearchserver.data.dto.BlogResponseDTO;
import com.example.blogsearchserver.data.dto.BlogSearchRequestDTO;
import com.example.blogsearchserver.data.dto.KakaoBlogResponseMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoConnector implements BlogConnector {

    private static final String KAKAO_BASE_URL = "https://dapi.kakao.com";
    private static final String KAKAO_AUTH_KEY = "21b6ad7b93e4f8a72e1c9400db47636a";

    private static final String CIRCUITBREAKER_NAME = "blog";
    private static final String CIRCUITBREAKER_FALLBACLK = "searchByKeywordFallback";

    private final NaverConnector naverConnector;

    /**
     * Kakao 서버 호출하여 블로그 검색 후 결과를 리턴
     * Kakao 서버 장애 판단시 Naver 서버로 호출 경로를 변경하여 호출
     * 장애 판단 기준 : 10회 호출 중 50% 이상 실패하였을때 (최소 호출 5회)
     * 경로 변경 후 10초 후 다시 Kakao 서버로 경로 변경 후 대기 -> 1회 호출 실패시 다시 Naver 서버로 호출
     * application.yaml 파일의 resilience4j 설정 참고
     */
    @Override
    @CircuitBreaker(name = CIRCUITBREAKER_NAME, fallbackMethod = CIRCUITBREAKER_FALLBACLK)
    public BlogResponseDTO findBlogByKeyword(BlogSearchRequestDTO requestDTO) {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("query", requestDTO.getQuery());
        queryParams.add("sort", requestDTO.getSort());
        queryParams.add("page", String.valueOf(requestDTO.getPage()));
        queryParams.add("size", String.valueOf(requestDTO.getSize()));

        KakaoBlogResponse result = WebClient.create(KAKAO_BASE_URL)
                .get()
                .uri(uriBuilder -> uriBuilder.path(ServiceApiUriEnum.KAKAO_BLOG_SEARCH_URI.getUri()).queryParams(queryParams).build())
                .header("Authorization", "KakaoAK " + KAKAO_AUTH_KEY)
                .retrieve()
                .bodyToMono(KakaoBlogResponse.class)
                .block();

        BlogResponseDTO dto = KakaoBlogResponseMapper.MAPPER.toDto(result);
        dto.setLastItem(result.getMeta().getIs_end());
        dto.setTotalElements(result.getMeta().getPageable_count());
        dto.setPageSize(requestDTO.getSize());
        dto.setPageNumber(requestDTO.getPage());

        return dto;
    }

    /**
     * Kakao 서버 장애 발생 시 naver 서버로 호출경로 변경 위한 fallback 메서드
     * 
     */
    private BlogResponseDTO searchByKeywordFallback(BlogSearchRequestDTO requestDTO, Throwable t) throws Throwable {
        log.info("Fallback :  {}", t.getClass());
        if (t instanceof CallNotPermittedException){
            return naverConnector.findBlogByKeyword(requestDTO);
        } else if (t instanceof WebClientResponseException) {
            log.error("API server error :  {}", t.getMessage());
        }
        throw t;
    }
}
