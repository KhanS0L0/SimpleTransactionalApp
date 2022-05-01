package com.example.service.simpleService;

import com.example.domain.customer.Customer;
import com.example.domain.order.Order;
import com.example.exception.NoRollbackException;
import com.example.exception.StoreException;
import com.example.repository.customer.CustomerRepository;
import com.example.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoreServiceImpl implements StoreService {
	
	private final CustomerRepository customerRepository;
	
	private final OrderRepository orderRepository;

	@Autowired
	public StoreServiceImpl(CustomerRepository customerRepository,
							OrderRepository orderRepository
	) {
		this.customerRepository = customerRepository;
		this.orderRepository = orderRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, String> show() {
		List<Customer> customers = customerRepository.findAll();
		List<Order> orders = orderRepository.findAll();
		Map<String, String> result = new HashMap<>();
		if(customers.size() != 0 && orders.size() != 0){
			customers.forEach(c -> {
				result.put("userid: " + c.getId().toString(), c.getName());
			});

			orders.forEach(o -> {
				result.put("order id: " + o.getId().toString(), o.getQuantity().toString());
			});
		}
		return result;
	}

	@Override
	@Transactional
	public void store(Customer customer, Order order) {
		customerRepository.save(customer);
		orderRepository.save(order);
	}

	@Override
	@Transactional(rollbackFor = StoreException.class)
	public void storeWithStoreException(Customer customer, Order order) throws StoreException {
		customerRepository.save(customer);
		if(true) throw new StoreException();
		orderRepository.save(order);
	}

	@Override
	@Transactional(noRollbackFor = NoRollbackException.class, rollbackFor = StoreException.class)
	public void storeWithNoRollbackException(Customer customer, Order order) throws NoRollbackException {
		customerRepository.save(customer);
		if(true) throw new NoRollbackException();
		orderRepository.save(order);
	}

}
