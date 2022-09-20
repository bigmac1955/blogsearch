package com.example.blogsearchserver.service;

import com.example.blogsearchserver.common.BlogSearchServerException;
import com.example.blogsearchserver.common.Constants;
import com.example.blogsearchserver.common.ErrorCodeEnum;
import com.example.blogsearchserver.connector.KakaoConnector;
import com.example.blogsearchserver.data.dto.BlogResponseDTO;
import com.example.blogsearchserver.data.dto.BlogSearchRequestDTO;
import com.example.blogsearchserver.data.dto.KeywordViewDTO;
import com.example.blogsearchserver.data.dto.KeywordViewMapper;
import com.example.blogsearchserver.data.entity.KeywordView;
import com.example.blogsearchserver.data.repository.KeywordViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SearchService {

    private final KakaoConnector kakaoConnector;
    private final KeywordViewRepository keywordViewRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${redis_key}")
    private String REDIS_KEY;

    /**
     * Kakao 서버 호출하여 블로그 검색 후 결과를 리턴
     * Kakao 서버 장애 판단시 Naver 서버로 호출 경로를 변경하여 호출
     * 검색에 성공하면 keyword 를 score 값 증가시켜서 redis 에 저장한다
     *
     * @throws BlogSearchServerException
     */
    public BlogResponseDTO searchByKeyword(BlogSearchRequestDTO requestDTO) throws BlogSearchServerException {

        checkParamsValid(requestDTO);
        String keyword = requestDTO.getQuery();

        BlogResponseDTO dto = kakaoConnector.findBlogByKeyword(requestDTO);

        // 검색에 성공하면 keyword 를 redis 에 저장
        redisTemplate.opsForZSet().incrementScore(REDIS_KEY, keyword, 1);

        log.info("searchByKeyWord completed | result : {}", dto);
        return dto;
    }

    /**
     * 파라미터 validation 체크
     * @param requestDTO
     *      page : 페이지 번호 1~50 (default 1)
     *      size : 한 페이지에 보여질 문서 수 1~50 (default 10)
     *      sort : 정렬 방식 accuracy (default) / recency
     *
     * @throws BlogSearchServerException
     */
    private static void checkParamsValid(BlogSearchRequestDTO requestDTO) throws BlogSearchServerException {
        if (requestDTO.getPage() < 1 || requestDTO.getPage() > 50) {
            throw new BlogSearchServerException(ErrorCodeEnum.INVALID_PARAMETER, null);
        }

        if (requestDTO.getSize() < 1 || requestDTO.getSize() > 50) {
            throw new BlogSearchServerException(ErrorCodeEnum.INVALID_PARAMETER, null);
        }

        if (!Constants.SORT_TYPE_ACCURACY.equals(requestDTO.getSort()) && !Constants.SORT_TYPE_RECENCY.equals(requestDTO.getSort())) {
            throw new BlogSearchServerException(ErrorCodeEnum.INVALID_PARAMETER, null);
        }
    }

    /**
     * 조회수 내림차순으로 인기검색어 목록 제공 (최대 10개)
     * @return
     *     [리스트]
     *     keyword : 검색어
     *     views : 검색횟수
     */
    public List<KeywordViewDTO> searchKeywordViewList() {

        List<KeywordView> result = keywordViewRepository.findTop10ByOrderByViewsDesc().orElse(null);
        return KeywordViewMapper.MAPPER.toDto(result);
    }

    /**
     * 동시성 테스트 코드에서 조회용으로 사용
     */
    public Long getViewCnt(String keyword) {
        return keywordViewRepository.findByKeyword(keyword).getViews();
    }
}
