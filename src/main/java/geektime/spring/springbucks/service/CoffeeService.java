package geektime.spring.springbucks.service;

import geektime.spring.springbucks.mapper.CoffeeMapper;
import geektime.spring.springbucks.mapper.CoffeeMapper;
import geektime.spring.springbucks.model.Coffee;
import geektime.spring.springbucks.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

//import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Slf4j
@Service
public class CoffeeService {
//    @Autowired
//    private CoffeeRepository coffeeRepository;
//    @Autowired
//    private CoffeeMapper CofferMapper;

//    public int createOrder(String customer, Coffee...coffee) {
//        Order record = new Order()
//                .withCustomer("espresso")
//                .withState(1)
//                .withCreateTime(new Date())
//                .withUpdateTime(new Date());
//        int saved = OrderMapper.insert(record);
//        return saved;
//    }

//    public Optional<Coffee> findOneCoffee(String name) {
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", exact().ignoreCase());
//        Optional<Coffee> coffee = coffeeRepository.findOne(
//                Example.of(Coffee.builder().name(name).build(), matcher));
//        log.info("Coffee Found: {}", coffee);
//        return coffee;
//    }
}
