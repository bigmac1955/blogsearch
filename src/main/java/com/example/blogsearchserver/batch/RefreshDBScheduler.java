package com.example.blogsearchserver.batch;

import com.example.blogsearchserver.data.entity.KeywordView;
import com.example.blogsearchserver.data.repository.KeywordViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshDBScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final KeywordViewRepository keywordViewRepository;

    @Value("${redis_key}")
    private String REDIS_KEY;

    /**
     * 주기적으로 실행되어 redis 에 남아있는 값들을 h2 DB 로 옮기기 위한 스케쥴러
     * 실행 주기는 application.yaml 파일의 refresh.db.schedule.millisecond 에 설정
     */
    @Scheduled(fixedDelayString = "${refresh.db.schedule.millisecond}")
    public void refreshDBUsingRedis() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        Set<ZSetOperations.TypedTuple<String>> redisData = zSetOperations.rangeWithScores(REDIS_KEY, 0, -1);

        for (ZSetOperations.TypedTuple<String> tuple : redisData) {
            String keyword = tuple.getValue();
            Long views = tuple.getScore().longValue();

            log.info("RefreshDBScheduler doing job... keyword : {} | views : {}", keyword, views);
            KeywordView keywordView = keywordViewRepository.findByKeyword(keyword);
            if (keywordView == null) {
                KeywordView newItem = new KeywordView(keyword, views);
                keywordViewRepository.save(newItem);
            } else {
                keywordView.setViews(keywordView.getViews() + views);
                keywordViewRepository.save(keywordView);
            }
            zSetOperations.remove(REDIS_KEY, keyword);
        }
    }
}
