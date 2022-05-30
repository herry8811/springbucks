package geektime.spring.springbucks.service;

import geektime.spring.springbucks.mapper.CoffeeMapper;
import geektime.spring.springbucks.mapper.CoffeeStateMapper;
import geektime.spring.springbucks.mapper.OrderCacheMapper;
import geektime.spring.springbucks.mapper.OrderMapper;
import geektime.spring.springbucks.model.Coffee;
import geektime.spring.springbucks.model.Order;
import geektime.spring.springbucks.model.OrderCache;
import geektime.spring.springbucks.model.OrderExample;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@Transactional
@MapperScan("geektime.spring.springbucks.mapper")
public class OrderService {
    @Autowired
    private CoffeeMapper CoffeeMapper;
    @Autowired
    private OrderMapper OrderMapper;
    @Autowired
    private OrderCacheMapper CacheMapper;
    @Autowired
    private RedisTemplate redisTemplate;



public Order createOrder(String customer, Coffee...coffee) {
        Order record = new Order()
                .withCustomer(customer)
                .withState(new Integer(0))
                .withCreateTime(new Date())
                .withUpdateTime(new Date());
        int saved = OrderMapper.insert(record);
        return record;
    }

    public boolean updateState(Order order, Integer state) {
        if (state.compareTo(order.getState()) <= 0) {
            log.warn("Wrong State order: {}, {}", state, order.getState());
            return false;
        }
        order.setState(state);
        OrderExample example = new OrderExample();
        example.createCriteria().andStateEqualTo(state);
        OrderMapper.updateByPrimaryKey(order);
        log.info("Updated Order: {}", order);
        return true;
    }

    public Optional<Order> findSimpleOrderFromCache(String name) {
        ValueOperations<String, Order> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(name);
        if(hasKey){
            Order record = operations.get(name);
            log.info("Order {} found in cache.", record);
            return Optional.of(record);
        } else {
            Optional<Order> raw = findOneOrder(name);
            raw.ifPresent(c -> {
                operations.set(name, raw.get(), 100, TimeUnit.SECONDS);
                log.info("Save Order {} to cache.", raw);
            });
            return raw;
        }
    }

    private Optional<Order> findOneOrder(String name) {
        OrderExample matcher = new OrderExample();
        matcher.createCriteria().andCustomerEqualTo(name);
        Optional<Order> order = Optional.ofNullable(OrderMapper.selectByExample(matcher).get(1));
        log.info("Order Found: {}", order);
        return order;
    }
}
