package com.example.chalpu.store.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.StoreException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.domain.UserStoreRole;
import com.example.chalpu.store.repository.StoreRepository;
import com.example.chalpu.store.repository.UserStoreRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserStoreRoleRepository userStoreRoleRepository;

    public PageResponse<Store> getMyStores(Long userId, Pageable pageable) {
        Page<Store> storePage = storeRepository.findStoresByUserId(userId, pageable);
        return PageResponse.from(storePage);
    }

    public Store getStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
    }

    @Transactional
    public Store createStore(Store storeRequest, Long userId) {
        Store store = storeRepository.save(storeRequest);
        // TODO: 매장 생성자를 OWNER로 등록하는 로직 추가
        return store;
    }

    @Transactional
    public Store updateStore(Long storeId, Store storeRequest, Long userId) {
        Store store = getStore(storeId);
        // TODO: 권한 검증 및 업데이트 로직 추가
        return storeRepository.save(store);
    }

    @Transactional
    public UserStoreRole inviteMember(Long storeId, UserStoreRole memberRequest, Long userId) {
        // TODO: 권한 검증 및 멤버 초대 로직 추가
        return userStoreRoleRepository.save(memberRequest);
    }
} 