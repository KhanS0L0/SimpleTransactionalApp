package com.example.service.nestedService;

import com.example.exception.NoRollbackException;
import com.example.exception.StoreException;

public interface NestedService {
    void transferWithNested(Long firstId, Long secondId) throws StoreException, NoRollbackException;
}
