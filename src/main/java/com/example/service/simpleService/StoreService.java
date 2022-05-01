package com.example.service.simpleService;

import com.example.domain.customer.Customer;
import com.example.domain.order.Order;
import com.example.exception.NoRollbackException;
import com.example.exception.StoreException;

import java.util.Map;

public interface StoreService {

	Map<String, String> show();

	void store(Customer customer, Order order) throws Exception;
	
	void storeWithStoreException(Customer customer, Order order) throws StoreException;

	void storeWithNoRollbackException(Customer customer, Order order) throws NoRollbackException;

}
