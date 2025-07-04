package com.example.chalpu.store.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.StoreException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.domain.StoreRoleType;
import com.example.chalpu.store.domain.UserStoreRole;
import com.example.chalpu.store.dto.MemberInviteRequest;
import com.example.chalpu.store.dto.MemberResponse;
import com.example.chalpu.store.dto.StoreResponse;
import com.example.chalpu.store.repository.StoreRepository;
import com.example.chalpu.store.repository.UserStoreRoleRepository;
import com.example.chalpu.user.domain.User;
import com.example.chalpu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStoreRoleService {

    private final UserStoreRoleRepository userStoreRoleRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    /**
     * 사용자가 속한 매장 목록 조회 (페이지네이션)
     */
    public PageResponse<StoreResponse> getMyStores(Long userId, Pageable pageable) {
        Page<UserStoreRole> userStoreRolePage = userStoreRoleRepository.findByUserId(userId, pageable);
        Page<StoreResponse> storeResponsePage = userStoreRolePage.map(usr -> StoreResponse.from(usr.getStore()));
        log.info("userStoreRolePage: {} storeResponsePage: {}", userStoreRolePage, storeResponsePage);
        return PageResponse.from(storeResponsePage);
    }

    /**
     * 사용자가 속한 매장 목록 조회 (전체)
     */
    public List<StoreResponse> getMyStores(Long userId) {
        List<UserStoreRole> userStoreRoles = userStoreRoleRepository.findByUserId(userId);
        log.info("userStoreRoles: {}", userStoreRoles);
        return userStoreRoles.stream()
                .map(usr -> StoreResponse.from(usr.getStore()))
                .toList();
    }

    /**
     * 사용자가 소유한 매장 목록 조회
     */
    public List<StoreResponse> getOwnedStores(Long userId) {
        List<UserStoreRole> userStoreRoles = userStoreRoleRepository.findByUserId(userId);
        log.info("userStoreRoles: {}", userStoreRoles);
        return userStoreRoles.stream()
                .filter(UserStoreRole::getIsActive)
                .filter(UserStoreRole::isOwner)
                .map(role -> StoreResponse.from(role.getStore()))
                .toList();
    }

    /**
     * 사용자가 관리할 수 있는 매장 목록 조회
     */
    public List<StoreResponse> getManageableStores(Long userId) {
        List<UserStoreRole> userStoreRoles = userStoreRoleRepository.findByUserId(userId);
        log.info("userStoreRoles: {}", userStoreRoles);
        return userStoreRoles.stream()
                .filter(UserStoreRole::getIsActive)
                .filter(UserStoreRole::canManageStore)
                .map(role -> StoreResponse.from(role.getStore()))
                .toList();
    }

    /**
     * 특정 매장의 멤버 목록 조회
     */
    public List<MemberResponse> getStoreMembers(Long storeId, Long requestUserId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));

        // 권한 검증: 해당 매장에 속한 사용자만 멤버 목록 조회 가능
        List<UserStoreRole> requestUserRoles = userStoreRoleRepository.findByUserId(requestUserId);
        if (!canUserAccessStore(requestUserRoles, store)) {
            log.error("canUserAccessStore: {}, 해당 매장에 속하지 않습니다.", canUserAccessStore(requestUserRoles, store));
            throw new StoreException(ErrorMessage.STORE_ACCESS_DENIED);
        }

        // 매장의 모든 멤버 조회 (활성 멤버만)
        List<UserStoreRole> storeMembers = userStoreRoleRepository.findByStoreId(storeId)
                .stream()
                .filter(UserStoreRole::getIsActive)
                .toList();

        log.info("storeMembers: {}", storeMembers);
        return storeMembers.stream()
                .map(MemberResponse::from)
                .toList();
    }

    /**
     * 매장 소유자 역할 생성 (사용자 ID와 매장 ID로)
     */
    @Transactional
    public void createOwnerRole(Long userId, Long storeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new StoreException(ErrorMessage.USER_NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
        createOwnerRole(user, store);
        log.info("user: {}, store: {} 매장 소유자 생성에 성공", user, store);
    }

    /**
     * 매장 소유자 역할 생성 (매장 생성 시 사용)
     */
    public void createOwnerRole(User user, Store store) {
        UserStoreRole ownerRole = UserStoreRole.createOwner(user, store);
        userStoreRoleRepository.save(ownerRole);
    }

    /**
     * 매장에 멤버 초대
     */
    @Transactional
    public MemberResponse inviteMember(Long storeId, MemberInviteRequest memberRequest, Long inviterUserId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
        User inviteUser = userRepository.findById(memberRequest.getUserId())
                .orElseThrow(() -> new StoreException(ErrorMessage.USER_NOT_FOUND));

        // 권한 검증: 멤버 초대 권한이 있는지 확인
        List<UserStoreRole> inviterRoles = userStoreRoleRepository.findByUserId(inviterUserId);
        if (!canInviteMembers(inviterRoles, store)) {
            log.error("canInviteMembers: {}, 멤버 초대 권한이 없습니다.", canInviteMembers(inviterRoles, store));
            throw new StoreException(ErrorMessage.STORE_ACCESS_DENIED);
        }

        // 이미 매장 구성원인지 확인
        Optional<UserStoreRole> existingRole = userStoreRoleRepository.findByUserIdAndStoreId(
                memberRequest.getUserId(), storeId);
        if (existingRole.isPresent()) {
            log.error("existingRole: {}, 이미 매장 구성원입니다.", existingRole);
            throw new StoreException(ErrorMessage.STORE_MEMBER_ALREADY_EXISTS);
        }

        // 직원 역할 생성
        UserStoreRole newUserRole = UserStoreRole.createEmployee(inviteUser, store, memberRequest.getRoleType());
        UserStoreRole savedUserStoreRole = userStoreRoleRepository.save(newUserRole);
        log.info("savedUserStoreRole {} 직원 역할 생성에 성공", savedUserStoreRole);
        return MemberResponse.from(savedUserStoreRole);
    }

    /**
     * 멤버 역할 변경
     */
    @Transactional
    public MemberResponse changeRole(Long storeId, Long targetUserId, StoreRoleType newRoleType, Long requestUserId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));

        // 요청자와 대상자의 역할 조회
        List<UserStoreRole> requestUserRoles = userStoreRoleRepository.findByUserId(requestUserId);
        UserStoreRole requestUserRole = getUserRoleInStore(requestUserRoles, store)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_ACCESS_DENIED));

        UserStoreRole targetRole = userStoreRoleRepository.findByUserIdAndStoreId(targetUserId, storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_MEMBER_NOT_FOUND));

        // 권한 검증
        validateRoleChange(requestUserRole, targetRole, newRoleType);

        // 역할 변경
        targetRole.changeRole(newRoleType);
        UserStoreRole updatedRole = userStoreRoleRepository.save(targetRole);
        log.info("updatedRole {} 역할 변경에 성공", updatedRole);
        return MemberResponse.from(updatedRole);
    }

    /**
     * 멤버 제거 (비활성화)
     */
    @Transactional
    public void removeMember(Long storeId, Long targetUserId, Long requestUserId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));

        // 요청자의 권한 확인
        List<UserStoreRole> requestUserRoles = userStoreRoleRepository.findByUserId(requestUserId);
        if (!canInviteMembers(requestUserRoles, store)) {
            throw new StoreException(ErrorMessage.STORE_ACCESS_DENIED);
        }

        // 대상자 역할 조회 및 비활성화
        UserStoreRole targetRole = userStoreRoleRepository.findByUserIdAndStoreId(targetUserId, storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_MEMBER_NOT_FOUND));

        // 본인은 제거할 수 없음
        if (targetUserId.equals(requestUserId)) {
            throw new StoreException(ErrorMessage.INVALID_REQUEST);
        }

        targetRole.deactivate();
        userStoreRoleRepository.save(targetRole);
        log.info("targetRole {} 멤버 제거에 성공", targetRole);
    }

    /**
     * 매장 탈퇴 (본인)
     */
    @Transactional
    public void leaveStore(Long storeId, Long userId) {
        UserStoreRole userRole = userStoreRoleRepository.findByUserIdAndStoreId(userId, storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_MEMBER_NOT_FOUND));

        // 소유자는 탈퇴할 수 없음 (매장을 다른 사람에게 양도하거나 매장을 삭제해야 함)
        if (userRole.isOwner()) {
            throw new StoreException(ErrorMessage.STORE_OWNER_REQUIRED);
        }

        userRole.deactivate();
        userStoreRoleRepository.save(userRole);
        log.info("userRole {} 매장 탈퇴에 성공", userRole);
    }

    /**
     * 특정 매장에서 사용자의 권한 확인
     */
    public boolean canUserAccessStore(Long userId, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
        
        List<UserStoreRole> userRoles = userStoreRoleRepository.findByUserId(userId);
        log.info("userRoles: {}", userRoles);
        return canUserAccessStore(userRoles, store);
    }

    /**
     * 특정 매장에서 사용자의 관리 권한 확인
     */
    public boolean canUserManageStore(Long userId, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
        
        List<UserStoreRole> userRoles = userStoreRoleRepository.findByUserId(userId);
        return canManageStore(userRoles, store);
    }

    // === 내부 유틸리티 메서드들 ===

    /**
     * 사용자가 특정 매장에 접근할 수 있는지 확인
     */
    private boolean canUserAccessStore(List<UserStoreRole> userRoles, Store store) {
        return userRoles.stream()
                .filter(UserStoreRole::getIsActive)
                .anyMatch(role -> role.getStore().equals(store));
    }

    /**
     * 사용자가 특정 매장을 관리할 수 있는지 확인
     */
    private boolean canManageStore(List<UserStoreRole> userRoles, Store store) {
        return getUserRoleInStore(userRoles, store)
                .map(UserStoreRole::canManageStore)
                .orElse(false);
    }

    /**
     * 사용자가 특정 매장에 멤버를 초대할 수 있는지 확인
     */
    private boolean canInviteMembers(List<UserStoreRole> userRoles, Store store) {
        return getUserRoleInStore(userRoles, store)
                .map(UserStoreRole::canInviteMembers)
                .orElse(false);
    }

    /**
     * 역할 변경 시 권한 검증
     */
    private void validateRoleChange(UserStoreRole currentUserRole, UserStoreRole targetRole, StoreRoleType newRoleType) {
        if (!currentUserRole.canInviteMembers()) {
            throw new StoreException(ErrorMessage.STORE_ACCESS_DENIED);
        }

        if (!currentUserRole.hasHigherAuthorityThan(targetRole)) {
            throw new StoreException(ErrorMessage.STORE_ACCESS_DENIED);
        }

        if (currentUserRole.getRoleType().getAuthorityLevel() <= newRoleType.getAuthorityLevel()) {
            throw new StoreException(ErrorMessage.STORE_ACCESS_DENIED);
        }
    }

    /**
     * 사용자의 특정 매장에서의 역할 조회
     */
    private Optional<UserStoreRole> getUserRoleInStore(List<UserStoreRole> userRoles, Store store) {
        return userRoles.stream()
                .filter(UserStoreRole::getIsActive)
                .filter(role -> role.getStore().equals(store))
                .findFirst();
    }
} 