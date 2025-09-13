package org.example.campuspark.global.redis;

import lombok.RequiredArgsConstructor;
import org.example.campuspark.global.exception.BusinessException;
import org.example.campuspark.global.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @SuppressWarnings("unchecked")
    public List<Long> getNearbyParkingSpaceIds(Long userId) {
        String redisKey = "nearby_parks:" + userId;
        Object rawValue = redisTemplate.opsForValue().get(redisKey);

        if (rawValue == null) {
            throw new BusinessException(ErrorCode.NO_NEARBY_PARKING_SPACES_FOUND);
        }

        if (rawValue instanceof List<?> rawList) {
            return rawList.stream()
                    .map(item -> ((Number) item).longValue())
                    .collect(Collectors.toList());
        }

        throw new BusinessException(ErrorCode.NO_NEARBY_PARKING_SPACES_FOUND);
    }
}
