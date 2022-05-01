package com.example.service.nestedService;

import com.example.domain.order.Order;
import com.example.exception.NoRollbackException;
import com.example.exception.StoreException;
import com.example.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferServiceImpl implements TransferService{

    private final OrderRepository orderRepository;

    @Autowired
    public TransferServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = NoRollbackException.class, rollbackFor = StoreException.class)
    public void transfer(Long firstId, Long secondId) {
        Order order1 = orderRepository.findById(firstId).orElse(null);
        Order order2 = orderRepository.findById(secondId).orElse(null);

        if(order1 != null && order2 != null){
            Long quantity1 = order1.getQuantity();
            order1.setQuantity(order2.getQuantity());
            order2.setQuantity(quantity1);

            orderRepository.save(order1);
            orderRepository.save(order2);
        }
    }
}