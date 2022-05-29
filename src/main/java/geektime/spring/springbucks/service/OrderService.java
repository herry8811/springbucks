package geektime.spring.springbucks.service;

import geektime.spring.springbucks.mapper.CoffeeMapper;
import geektime.spring.springbucks.mapper.CoffeeStateMapper;
import geektime.spring.springbucks.mapper.OrderMapper;
import geektime.spring.springbucks.model.Coffee;
import geektime.spring.springbucks.model.Order;
import geektime.spring.springbucks.model.OrderExample;
import geektime.spring.springbucks.model.OrderState;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


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
    private CoffeeStateMapper CofferStateMapper;

//    public int createOrder(String customer, Coffee...coffee) {
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
//        OrderMapper.updateByExampleSelective(order,example);
        OrderMapper.updateByPrimaryKey(order);
        log.info("Updated Order: {}", order);
        return true;
    }
}
