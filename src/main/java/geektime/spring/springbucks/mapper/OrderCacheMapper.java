package geektime.spring.springbucks.mapper;

import geektime.spring.springbucks.model.OrderCache;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface OrderCacheMapper {
    Optional<OrderCache> findByName(String name);
}
