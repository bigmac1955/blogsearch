package com.example.blogsearchserver.connector;

import com.example.blogsearchserver.common.Constants;
import com.example.blogsearchserver.common.ServiceApiUriEnum;
import com.example.blogsearchserver.data.domain.NaverBlogResponse;
import com.example.blogsearchserver.data.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NaverConnector extends AbstractConnector implements BlogConnector{

    private static final String NAVER_BASE_URL = "https://openapi.naver.com";
    private static final String NAVER_CLIENT_ID = "sDggVURIDErMUOhU8oT5";
    private static final String NAVER_CLIENT_SECRET = "AUyQO3d1op";

    @Override
    public BlogResponseDTO findBlogByKeyword(BlogSearchRequestDTO requestDTO) {
        String sort = (Constants.SORT_TYPE_ACCURACY.equals(requestDTO.getSort())) ? "sim" : "date";

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("query", requestDTO.getQuery());
        queryParams.add("sort", sort);
        queryParams.add("start", String.valueOf(requestDTO.getPage()));
        queryParams.add("display", String.valueOf(requestDTO.getSize()));

        NaverBlogResponse result = getWebClient(NAVER_BASE_URL)
                .get()
                .uri(uriBuilder -> uriBuilder.path(ServiceApiUriEnum.NAVER_BLOG_SEARCH_URI.getUri()).queryParams(queryParams).build())
                .header("X-Naver-Client-Id", NAVER_CLIENT_ID)
                .header("X-Naver-Client-Secret", NAVER_CLIENT_SECRET)
                .retrieve()
                .bodyToMono(NaverBlogResponse.class)
                .block();

        BlogResponseDTO dto = NaverBlogResponseMapper.MAPPER.toDto(result);
        List<BlogResponseDocumentDTO> items = NaverBlogDocumentMapper.MAPPER.toDto(result.getItems());


        items.forEach(item -> item.setDatetime(changeDateTimeFormat(item.getDatetime())));

        // 페이지번호 * 표시갯수 > 전체갯수 이면 lastItem true 로 준다
        boolean lastItem = result.getStart() * result.getDisplay() > result.getTotal();

        dto.setLastItem(lastItem);
        dto.setDocuments(items);
        dto.setPageSize(requestDTO.getSize());
        dto.setPageNumber(requestDTO.getPage());
        return dto;
    }

    /**
     *
     * @param oldDateStr (yyyyMMdd)
     * @return newDateStr ([YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz])
     */
    private String changeDateTimeFormat(String oldDateStr) {

        String yyyy = oldDateStr.substring(0,4);
        String MM = oldDateStr.substring(4,6);
        String dd = oldDateStr.substring(6,8);

        return new StringBuilder().append(yyyy).append("-").append(MM).append("-").append(dd).append("T").append("00:00:00.000+09:00").toString();
    }
}
