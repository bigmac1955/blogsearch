package com.example.blogsearchserver.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * 스케쥴러에 의한 EmbeddedRedis to DB 동작 관련 테스트 입니다.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RedisToDBTest {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("redis 사용시 멀티쓰레드 상황을 가정하여 race condition 발생하지 않는것을 테스트")
    public void testRedisZSetWithoutRaceCondition() throws InterruptedException {

        ExecutorService service = Executors.newFixedThreadPool(10);
        String key = "keyword";
        String keyword = "노정욱";
        CountDownLatch latch = new CountDownLatch(100);
        for (int i=0; i < 100; i++) {
            service.execute(() -> {
                try {
                    redisTemplate.opsForZSet().incrementScore(key, keyword,1);
                } catch (Exception e){
                    e.printStackTrace();
                }
                latch.countDown();
            });
        }
        latch.await();

        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        assertThat(zSetOperations.score(key, keyword)).isEqualTo(100L);
    }

    @Test
    @DisplayName("redis 사용시 ZSet 생성 후 삭제 테스트")
    public void testRedisZSetCreateDelete() {

        String key = "keyword";
        String keyword = "노정욱";
        redisTemplate.opsForZSet().incrementScore(key, keyword,1);

        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        assertThat(zSetOperations.score(key, keyword)).isEqualTo(1L);

        zSetOperations.remove(key,keyword);
        assertThat(zSetOperations.score(key, keyword)).isNull();
    }
}