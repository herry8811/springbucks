package geektime.spring.springbucks;

import geektime.spring.springbucks.mapper.CoffeeMapper;
import geektime.spring.springbucks.mapper.OrderMapper;
import geektime.spring.springbucks.model.Coffee;
import geektime.spring.springbucks.model.Order;
import geektime.spring.springbucks.model.OrderExample;
import geektime.spring.springbucks.model.OrderState;
import geektime.spring.springbucks.service.CoffeeService;
import geektime.spring.springbucks.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableTransactionManagement
@SpringBootApplication
@MapperScan("geektime.spring.springbucks.mapper")
@RestController
public class SpringBucksApplication implements ApplicationRunner {

	@Autowired
	private CoffeeMapper coffeeMapper;
	@Autowired
	private OrderMapper OrderMapper;
	@Autowired
	private OrderService orderService;
	@Autowired
	private RedisTemplate redisTemplate;


	public static void main(String[] args) {
		SpringApplication.run(SpringBucksApplication.class, args);
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws Exception {

//		generateArtifacts();

		log.info("class begin {} !!!!!");

		coffeeMapper.delete();
		log.info("Delete {} Coffee: {}");

		Coffee c = Coffee.builder().name("espresso")
				.price(Money.of(CurrencyUnit.of("CNY"), 20.0)).build();
		int count = coffeeMapper.insert(c);
		log.info("Save {} Coffee: {}", count, c);

//		rollback test
//		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

		c = Coffee.builder().name("latte")
				.price(Money.of(CurrencyUnit.of("CNY"), 25.0)).build();
		count = coffeeMapper.insert(c);
		log.info("Save {} Coffee: {}", count, c);

		c = coffeeMapper.findById(c.getId());
		log.info("Find Coffee: {}", c);

		c = coffeeMapper.findByName("latte");
		log.info("Find Coffee By Name Before: {}", c);

		coffeeMapper.update(5000, "latte");
		log.info("Update Coffee By Name: {}", c);

		c = coffeeMapper.findByName("latte");
		log.info("Find Coffee By Name Before: {}", c);

		Optional<Coffee> latte = Optional.ofNullable(coffeeMapper.findByName("latte"));
		log.info("Find Coffee By Name: {}", latte);
		if (latte.isPresent()) {
			Order order = orderService.createOrder("Li Lei", latte.get());
			log.info("Update INIT to PAID: {}", orderService.updateState(order, 1));
			order = orderService.createOrder("Zhang San", latte.get());
			log.info("Update INIT to PAID: {}", orderService.updateState(order, 1));
			order = orderService.createOrder("Zhao Si", latte.get());
			log.info("Update INIT to PAID: {}", orderService.updateState(order, 1));
		}

		Optional<Coffee> espresso = Optional.ofNullable(coffeeMapper.findByName("espresso"));
		log.info("Find Coffee By Name: {}", espresso);
		if (espresso.isPresent()) {
			Order order = orderService.createOrder("Wang er", espresso.get());
			log.info("Update INIT to PAID: {}", orderService.updateState(order,1));
			order = orderService.createOrder("Sun wu", espresso.get());
			log.info("Update INIT to PAID: {}", orderService.updateState(order, 1));
		}

		OrderExample example = new OrderExample();
		example.createCriteria().andStateEqualTo(1);
		OrderMapper.selectByExampleWithRowbounds(example,new RowBounds(2, 3))
				.forEach(l -> log.info("Page(1) Order {}", l));
		OrderMapper.selectByExampleWithRowbounds(example,new RowBounds(1, 3))
				.forEach(l -> log.info("Page(2) Order {}", l));
		log.info("1===================");

		OrderMapper.selectByExampleWithRowbounds(example,new RowBounds(1, 0))
				.forEach(l -> log.info("Page(1) Order {}", l));
		log.info("2===================");

//		add all to redis
		ValueOperations<String, Order> operations = redisTemplate.opsForValue();
		OrderExample orderExample = new OrderExample();
		orderExample.createCriteria().andCustomerIsNotNull();
		OrderMapper.selectByExample(orderExample).forEach(
				m -> operations.set(m.getCustomer(), m, 1000,TimeUnit.SECONDS)
		);

		Optional<Order> o = orderService.findSimpleOrderFromCache("Zhao Si");
		log.info("Order {}", o);

		for (int i = 0; i < 5; i++) {
			o = orderService.findSimpleOrderFromCache("Li Lei");
		}

		log.info("Value from Redis: {}", o);
	}

	private void generateArtifacts() throws Exception{
		List<String> warnings = new ArrayList<>();
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(
				this.getClass().getResourceAsStream("/generatorConfig.xml"));
		DefaultShellCallback callback = new DefaultShellCallback(true);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
	}

	@RequestMapping("/hello")
	public String hello(){
		return "Hello Beijing";
	}
}

