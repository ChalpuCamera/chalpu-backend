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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreResponse getStore(Long storeId) {
        try {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
            return StoreResponse.from(store);
        } catch (Exception e) {
            log.error("event=store_get_failed, store_id={}, error_message={}", storeId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public StoreResponse createStore(StoreRequest storeRequest) {
        try {
            Store store = Store.createStore(storeRequest);
            Store savedStore = storeRepository.save(store);
            log.info("event=store_created, store_id={}", savedStore.getId());
            return StoreResponse.from(savedStore);
        } catch (Exception e) {
            log.error("event=store_creation_failed, store_name={}, error_message={}",
                    storeRequest.getStoreName(), e.getMessage(), e);
            throw new StoreException(ErrorMessage.STORE_CREATE_FAILED);
        }
    }

    @Transactional
    public StoreResponse updateStore(Long storeId, StoreRequest storeRequest) {
        try {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
            
            store.updateStore(storeRequest);
            log.info("event=store_updated, store_id={}", storeId);
            return StoreResponse.from(store);
        } catch (Exception e) {
            log.error("event=store_update_failed, store_id={}, error_message={}",
                    storeId, e.getMessage(), e);
            throw new StoreException(ErrorMessage.STORE_UPDATE_FAILED);
        }
    }

    @Transactional
    public void deleteStore(Long storeId) {
        try {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
            storeRepository.delete(store);
            log.info("event=store_deleted, store_id={}", storeId);
        } catch (Exception e) {
            log.error("event=store_deletion_failed, store_id={}, error_message={}",
                    storeId, e.getMessage(), e);
            throw new StoreException(ErrorMessage.STORE_DELETE_FAILED);
        }
    }
} 