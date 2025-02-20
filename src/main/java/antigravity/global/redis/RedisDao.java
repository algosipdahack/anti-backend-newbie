package antigravity.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Set;

import static org.springframework.cache.interceptor.SimpleKeyGenerator.generateKey;

@RequiredArgsConstructor
@Repository
public class RedisDao {
    // String값을 key, value로 사용하는 경우 사용
    private final StringRedisTemplate stringRedisTemplate;
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }
    public boolean lock(String key) {
        return stringRedisTemplate
                .opsForValue()
                //setnx 명령어 사용 - key(key) value("lock")
                .setIfAbsent(String.valueOf(generateKey(key)), "lock", Duration.ofMillis(3_000));
    }
    public Boolean unlock(String key) {
        return stringRedisTemplate.delete((String) generateKey(key));
    }
    public Set<String> getKeys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }
    public void setData(String key, String value) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    public String setDataExpire(String key, String value, Duration duration) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = duration;
        valueOperations.set(key, value, expireDuration);
        return value;
    }
    public Long incrementData(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.increment(key);
    }

    public void deleteData(String key) {
        stringRedisTemplate.delete(key);
    }
}
