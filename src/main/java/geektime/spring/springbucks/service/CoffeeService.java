package geektime.spring.springbucks.service;

import geektime.spring.springbucks.mapper.*;
import geektime.spring.springbucks.mapper.CoffeeMapper;
import geektime.spring.springbucks.model.Order;
import geektime.spring.springbucks.model.OrderCache;
import geektime.spring.springbucks.model.OrderExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.Optional;

//import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Slf4j
@Service
public class CoffeeService {

    @Autowired
    private CoffeeMapper coffeeMapper;
    @Autowired
    private OrderMapper orderMapper;

//    public List<Coffee> findAllCoffee() {
//        return coffeeMapper.findAll();
//    }

}
