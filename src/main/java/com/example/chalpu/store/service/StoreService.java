package com.example.chalpu.store.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.StoreException;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.dto.StoreRequest;
import com.example.chalpu.store.dto.StoreResponse;
import com.example.chalpu.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreResponse getStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
        return StoreResponse.from(store);
    }

    @Transactional
    public StoreResponse createStore(StoreRequest storeRequest) {
        Store store = Store.createStore(storeRequest);
        Store savedStore = storeRepository.save(store);
        return StoreResponse.from(savedStore);
    }

    @Transactional
    public StoreResponse updateStore(Long storeId, StoreRequest storeRequest) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
        
        store.updateStore(storeRequest);
        Store updatedStore = storeRepository.save(store);
        return StoreResponse.from(updatedStore);
    }

    @Transactional
    public void deleteStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
        storeRepository.delete(store);
    }
} 