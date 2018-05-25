package GxPtProxy.Service;

import GxPtProxy.Bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import javax.servlet.http.HttpSession;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    public boolean exists(String key){
        return redisTemplate.hasKey(key);
    }
    public void delete(String key) {
        if(exists(key))
            redisTemplate.delete(key);
    }
    public boolean set(String key,Object object) {
        boolean flag = false;
        try{
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, object);
            flag = true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }
    public Object get(String key){
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }
}
