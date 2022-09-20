package com.example.blogsearchserver.connector;

import com.example.blogsearchserver.common.Constants;
import com.example.blogsearchserver.data.dto.BlogResponseDTO;
import com.example.blogsearchserver.data.dto.BlogSearchRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = KakaoConnector.class)
public class KakaoConnectorTest {

    @Autowired
    private KakaoConnector kakaoConnector;

    @MockBean
    private NaverConnector naverConnector;

    private String keyword = "노정욱";
    private String sort = Constants.SORT_TYPE_ACCURACY;
    private Integer page = 1;
    private Integer size = 1;
    private String fromService = "Kakao";

    @Test
    @DisplayName("카카오 검색 API 테스트")
    public void testFindBlogByKeyword(){

        //given
        BlogSearchRequestDTO requestDTO = BlogSearchRequestDTO.builder()
                .query(keyword)
                .sort(sort)
                .page(page)
                .size(size)
                .build();

        //when
        BlogResponseDTO responseDTO = kakaoConnector.findBlogByKeyword(requestDTO);

        //then
        assertThat(responseDTO.getFromService()).isEqualTo(fromService);
    }
}
