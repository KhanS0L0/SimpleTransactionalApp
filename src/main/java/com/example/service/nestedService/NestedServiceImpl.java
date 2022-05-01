package com.example.service.nestedService;

import com.example.domain.customer.Customer;
import com.example.exception.NoRollbackException;
import com.example.exception.StoreException;
import com.example.repository.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NestedServiceImpl implements NestedService{

    private final CustomerRepository customerRepository;

    private final TransferService transferService;


    @Autowired
    public NestedServiceImpl(CustomerRepository customerRepository, TransferService transferService) {
        this.customerRepository = customerRepository;
        this.transferService = transferService;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = StoreException.class, noRollbackFor = NoRollbackException.class)
    public void transferWithNested(Long firstId, Long secondId) throws StoreException, NoRollbackException {
        Customer customer1 = customerRepository.findById(firstId).orElse(null);
        Customer customer2 = customerRepository.findById(secondId).orElse(null);

        if(customer1 != null && customer2 != null){
            String userName1 = customer1.getName();
            customer1.setName(customer2.getName());
            customer2.setName(userName1);

            transferService.transfer(firstId, secondId);
            if(true) throw new StoreException();

            customerRepository.save(customer1);
            customerRepository.save(customer2);
        }
    }
}
