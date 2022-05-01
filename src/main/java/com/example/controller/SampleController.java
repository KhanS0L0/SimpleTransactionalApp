package com.example.controller;

import com.example.domain.customer.Customer;
import com.example.domain.order.Order;
import com.example.dto.IdDTO;
import com.example.dto.SampleDTO;
import com.example.service.nestedService.NestedService;
import com.example.service.simpleService.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/v1")
public class SampleController {
	
	private final StoreService storeService;

	private final NestedService nestedService;

	@Autowired
	public SampleController(StoreService storeService, NestedService nestedService) {
		this.storeService = storeService;
		this.nestedService = nestedService;
	}

	@ResponseBody
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String home() {
        return "Hello World!";
    }

	@ResponseBody
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public Object show(){
		return storeService.show();
	}

	@ResponseBody
    @RequestMapping(value = "/store", method = RequestMethod.POST)
    public Object store(@RequestBody SampleDTO dto) {
    	Map<String, Object> result = new HashMap<>();
    	try {
    		Customer customer = new Customer();
    		customer.setName(dto.getName());

			Order order = new Order();
    		order.setQuantity(dto.getQuantity());

			storeService.store(customer, order);

    		result.put("status", "0");

		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "1");
			result.put("msg", e.getMessage());
		}
    	return result;
    }

	@ResponseBody
	@RequestMapping(value = "/storeWithRollBack", method = RequestMethod.POST)
	public Object storeWithRollBack(@RequestBody SampleDTO dto) {
		Map<String, Object> result = new HashMap<>();
		try {
			Customer customer = new Customer();
			customer.setName(dto.getName());

			Order order = new Order();
			order.setQuantity(dto.getQuantity());

			storeService.storeWithStoreException(customer, order);

			result.put("status", "0");

		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "1");
			result.put("msg", e.getMessage());
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/storeWithoutRollBack", method = RequestMethod.POST)
	public Object storeWithNoRollBack(@RequestBody SampleDTO dto) {
		Map<String, Object> result = new HashMap<>();
		try {
			Customer customer = new Customer();
			customer.setName(dto.getName());

			Order order = new Order();
			order.setQuantity(dto.getQuantity());

			storeService.storeWithNoRollbackException(customer, order);

			result.put("status", "0");

		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "1");
			result.put("msg", e.getMessage());
		}
		return result;
	}

	@ResponseBody
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public Object transfer(@RequestBody IdDTO dto) {
    	Map<String, Object> result = new HashMap<>();
    	try {
    		nestedService.transferWithNested(dto.getFirstId(), dto.getSecondId());
    		result.put("status", "0");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "1");
			result.put("msg", e.getMessage());
		}
    	return result;
    }
}
