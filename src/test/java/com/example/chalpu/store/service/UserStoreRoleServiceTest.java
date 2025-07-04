package com.example.chalpu.store.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStoreRoleServiceTest {

    @Mock
    private UserStoreRoleRepository userStoreRoleRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserStoreRoleService userStoreRoleService;

    private User testUser;
    private Store testStore;
    private UserStoreRole testUserStoreRole;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("테스트 사용자")
                .build();

        testStore = Store.builder()
                .id(1L)
                .storeName("테스트 매장")
                .businessType("한식")
                .address("서울시 강남구")
                .phone("02-1234-5678")
                .businessRegistrationNumber("123-45-67890")
                .build();

        testUserStoreRole = UserStoreRole.builder()
                .id(1L)
                .user(testUser)
                .store(testStore)
                .roleType(StoreRoleType.OWNER)
                .isActive(true)
                .build();

        testPageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("사용자가 속한 매장 목록 조회")
    void getMyStores_WithPagination_Success() {
        // given
        List<UserStoreRole> userStoreRoles = List.of(testUserStoreRole);
        Page<UserStoreRole> userStoreRolePage = new PageImpl<>(userStoreRoles, testPageable, 1);
        
        when(userStoreRoleRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(userStoreRolePage);

        // when
        PageResponse<StoreResponse> result = userStoreRoleService.getMyStores(1L, testPageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStoreName()).isEqualTo("테스트 매장");
        
        verify(userStoreRoleRepository).findByUserId(1L, testPageable);
    }

    @Test
    @DisplayName("사용자가 속한 매장 목록 조회 (전체) - 성공")
    void getMyStores_All_Success() {
        // given
        List<UserStoreRole> userStoreRoles = List.of(testUserStoreRole);
        
        when(userStoreRoleRepository.findByUserId(anyLong()))
                .thenReturn(userStoreRoles);

        // when
        List<StoreResponse> result = userStoreRoleService.getMyStores(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStoreName()).isEqualTo("테스트 매장");
        
        verify(userStoreRoleRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("사용자가 소유한 매장 목록 조회")
    void getOwnedStores_Success() {
        // given
        List<UserStoreRole> userStoreRoles = List.of(testUserStoreRole);
        
        when(userStoreRoleRepository.findByUserId(anyLong()))
                .thenReturn(userStoreRoles);

        // when
        List<StoreResponse> result = userStoreRoleService.getOwnedStores(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStoreName()).isEqualTo("테스트 매장");
        
        verify(userStoreRoleRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("사용자가 관리할 수 있는 매장 목록 조회 - 성공")
    void getManageableStores_Success() {
        // given
        List<UserStoreRole> userStoreRoles = List.of(testUserStoreRole);
        
        when(userStoreRoleRepository.findByUserId(anyLong()))
                .thenReturn(userStoreRoles);

        // when
        List<StoreResponse> result = userStoreRoleService.getManageableStores(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStoreName()).isEqualTo("테스트 매장");
        
        verify(userStoreRoleRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("매장 소유자 역할 생성 (사용자 ID와 매장 ID로) - 성공")
    void createOwnerRole_WithIds_Success() {
        // given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(userStoreRoleRepository.save(any(UserStoreRole.class))).thenReturn(testUserStoreRole);

        // when
        userStoreRoleService.createOwnerRole(1L, 1L);

        // then
        verify(userRepository).findById(1L);
        verify(storeRepository).findById(1L);
        verify(userStoreRoleRepository).save(any(UserStoreRole.class));
    }

    @Test
    @DisplayName("매장 소유자 역할 생성 - 사용자 없음 예외")
    void createOwnerRole_UserNotFound_ThrowsException() {
        // given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStoreRoleService.createOwnerRole(1L, 1L))
                .isInstanceOf(StoreException.class);
        
        verify(userRepository).findById(1L);
        verify(storeRepository, never()).findById(anyLong());
        verify(userStoreRoleRepository, never()).save(any(UserStoreRole.class));
    }

    @Test
    @DisplayName("매장 소유자 역할 생성 - 매장 없음 예외")
    void createOwnerRole_StoreNotFound_ThrowsException() {
        // given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(storeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStoreRoleService.createOwnerRole(1L, 1L))
                .isInstanceOf(StoreException.class);
        
        verify(userRepository).findById(1L);
        verify(storeRepository).findById(1L);
        verify(userStoreRoleRepository, never()).save(any(UserStoreRole.class));
    }

    @Test
    @DisplayName("매장에 멤버 초대 - 성공")
    void inviteMember_Success() {
        // given
        MemberInviteRequest request = new MemberInviteRequest();
        request.setUserId(2L);
        request.setRoleType(StoreRoleType.STAFF);

        User inviteUser = User.builder().id(2L).email("invite@example.com").name("초대 사용자").build();
        UserStoreRole newRole = UserStoreRole.builder()
                .id(2L)
                .user(inviteUser)
                .store(testStore)
                .roleType(StoreRoleType.STAFF)
                .isActive(true)
                .build();

        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(inviteUser));
        when(userStoreRoleRepository.findByUserId(anyLong())).thenReturn(List.of(testUserStoreRole));
        when(userStoreRoleRepository.findByUserIdAndStoreId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(userStoreRoleRepository.save(any(UserStoreRole.class))).thenReturn(newRole);

        // when
        MemberResponse result = userStoreRoleService.inviteMember(1L, request, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(2L);
        
        verify(storeRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(userStoreRoleRepository).save(any(UserStoreRole.class));
    }

    @Test
    @DisplayName("매장에 멤버 초대 - 이미 존재하는 멤버 예외")
    void inviteMember_AlreadyExists_ThrowsException() {
        // given
        MemberInviteRequest request = new MemberInviteRequest();
        request.setUserId(2L);
        request.setRoleType(StoreRoleType.STAFF);

        User inviteUser = User.builder().id(2L).email("invite@example.com").name("초대 사용자").build();
        UserStoreRole existingRole = UserStoreRole.builder()
                .id(2L)
                .user(inviteUser)
                .store(testStore)
                .roleType(StoreRoleType.STAFF)
                .isActive(true)
                .build();

        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(inviteUser));
        when(userStoreRoleRepository.findByUserId(anyLong())).thenReturn(List.of(testUserStoreRole));
        when(userStoreRoleRepository.findByUserIdAndStoreId(anyLong(), anyLong())).thenReturn(Optional.of(existingRole));

        // when & then
        assertThatThrownBy(() -> userStoreRoleService.inviteMember(1L, request, 1L))
                .isInstanceOf(StoreException.class);
        
        verify(userStoreRoleRepository, never()).save(any(UserStoreRole.class));
    }

    @Test
    @DisplayName("멤버 역할 변경 - 성공")
    void changeRole_Success() {
        // given
        UserStoreRole targetRole = UserStoreRole.builder()
                .id(2L)
                .user(User.builder().id(2L).build())
                .store(testStore)
                .roleType(StoreRoleType.STAFF)
                .isActive(true)
                .build();

        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(userStoreRoleRepository.findByUserId(anyLong())).thenReturn(List.of(testUserStoreRole));
        when(userStoreRoleRepository.findByUserIdAndStoreId(anyLong(), anyLong())).thenReturn(Optional.of(targetRole));
        when(userStoreRoleRepository.save(any(UserStoreRole.class))).thenReturn(targetRole);

        // when
        MemberResponse result = userStoreRoleService.changeRole(1L, 2L, StoreRoleType.MANAGER, 1L);

        // then
        assertThat(result).isNotNull();
        
        verify(userStoreRoleRepository).save(any(UserStoreRole.class));
    }

    @Test
    @DisplayName("멤버 제거 - 성공")
    void removeMember_Success() {
        // given
        UserStoreRole targetRole = UserStoreRole.builder()
                .id(2L)
                .user(User.builder().id(2L).build())
                .store(testStore)
                .roleType(StoreRoleType.STAFF)
                .isActive(true)
                .build();

        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(userStoreRoleRepository.findByUserId(anyLong())).thenReturn(List.of(testUserStoreRole));
        when(userStoreRoleRepository.findByUserIdAndStoreId(anyLong(), anyLong())).thenReturn(Optional.of(targetRole));
        when(userStoreRoleRepository.save(any(UserStoreRole.class))).thenReturn(targetRole);

        // when
        userStoreRoleService.removeMember(1L, 2L, 1L);

        // then
        verify(userStoreRoleRepository).save(any(UserStoreRole.class));
    }

    @Test
    @DisplayName("매장 탈퇴 - 성공")
    void leaveStore_Success() {
        // given
        UserStoreRole userRole = UserStoreRole.builder()
                .id(1L)
                .user(testUser)
                .store(testStore)
                .roleType(StoreRoleType.STAFF) // 소유자가 아닌 직원
                .isActive(true)
                .build();

        when(userStoreRoleRepository.findByUserIdAndStoreId(anyLong(), anyLong())).thenReturn(Optional.of(userRole));
        when(userStoreRoleRepository.save(any(UserStoreRole.class))).thenReturn(userRole);

        // when
        userStoreRoleService.leaveStore(1L, 1L);

        // then
        verify(userStoreRoleRepository).save(any(UserStoreRole.class));
    }

    @Test
    @DisplayName("매장 탈퇴 - 소유자는 탈퇴 불가 예외")
    void leaveStore_OwnerCannotLeave_ThrowsException() {
        // given
        UserStoreRole ownerRole = UserStoreRole.builder()
                .id(1L)
                .user(testUser)
                .store(testStore)
                .roleType(StoreRoleType.OWNER) // 소유자
                .isActive(true)
                .build();

        when(userStoreRoleRepository.findByUserIdAndStoreId(anyLong(), anyLong())).thenReturn(Optional.of(ownerRole));

        // when & then
        assertThatThrownBy(() -> userStoreRoleService.leaveStore(1L, 1L))
                .isInstanceOf(StoreException.class);
        
        verify(userStoreRoleRepository, never()).save(any(UserStoreRole.class));
    }

    @Test
    @DisplayName("사용자 매장 접근 권한 확인 - 성공")
    void canUserAccessStore_Success() {
        // given
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(userStoreRoleRepository.findByUserId(anyLong())).thenReturn(List.of(testUserStoreRole));

        // when
        boolean result = userStoreRoleService.canUserAccessStore(1L, 1L);

        // then
        assertThat(result).isTrue();
        
        verify(storeRepository).findById(1L);
        verify(userStoreRoleRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("사용자 매장 관리 권한 확인 - 성공")
    void canUserManageStore_Success() {
        // given
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(userStoreRoleRepository.findByUserId(anyLong())).thenReturn(List.of(testUserStoreRole));

        // when
        boolean result = userStoreRoleService.canUserManageStore(1L, 1L);

        // then
        assertThat(result).isTrue();
        
        verify(storeRepository).findById(1L);
        verify(userStoreRoleRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("사용자가 소유한 매장 목록 조회 - 소유한 매장 없음")
    void getOwnedStores_NoOwnedStores_ReturnsEmptyList() {
        // given
        UserStoreRole staffRole = UserStoreRole.builder()
                .roleType(StoreRoleType.STAFF)
                .build();
        
        when(userStoreRoleRepository.findByUserId(anyLong()))
                .thenReturn(List.of(staffRole));

        // when
        List<StoreResponse> result = userStoreRoleService.getOwnedStores(1L);

        // then
        assertThat(result).isEmpty();
        verify(userStoreRoleRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("사용자가 관리할 수 있는 매장 목록 조회 - 관리 가능 매장 없음")
    void getManageableStores_NoManageableStores_ReturnsEmptyList() {
        // given
        UserStoreRole staffRole = UserStoreRole.builder()
                .roleType(StoreRoleType.STAFF)
                .build();
        
        when(userStoreRoleRepository.findByUserId(anyLong()))
                .thenReturn(List.of(staffRole));

        // when
        List<StoreResponse> result = userStoreRoleService.getManageableStores(1L);

        // then
        assertThat(result).isEmpty();
        verify(userStoreRoleRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("매장에 멤버 초대 - 권한 없음 예외")
    void inviteMember_NoPermission_ThrowsException() {
        // given
        MemberInviteRequest request = new MemberInviteRequest();
        request.setUserId(2L);
        request.setRoleType(StoreRoleType.STAFF);

        UserStoreRole inviterRole = UserStoreRole.builder()
                .user(testUser)
                .store(testStore)
                .roleType(StoreRoleType.STAFF) // 초대 권한이 없는 STAFF
                .isActive(true)
                .build();

        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(userStoreRoleRepository.findByUserId(anyLong())).thenReturn(List.of(inviterRole));

        // when & then
        assertThatThrownBy(() -> userStoreRoleService.inviteMember(1L, request, 1L))
                .isInstanceOf(StoreException.class);
        
        verify(userStoreRoleRepository, never()).save(any(UserStoreRole.class));
    }

    @Test
    @DisplayName("멤버 제거 - 권한 없음 예외")
    void removeMember_NoPermission_ThrowsException() {
        // given
        UserStoreRole removerRole = UserStoreRole.builder()
                .user(testUser)
                .store(testStore)
                .roleType(StoreRoleType.STAFF) // 제거 권한이 없는 STAFF
                .isActive(true)
                .build();

        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(userStoreRoleRepository.findByUserId(anyLong())).thenReturn(List.of(removerRole));

        // when & then
        assertThatThrownBy(() -> userStoreRoleService.removeMember(1L, 2L, 1L))
                .isInstanceOf(StoreException.class);

        verify(userStoreRoleRepository, never()).findByUserIdAndStoreId(anyLong(), anyLong());
        verify(userStoreRoleRepository, never()).save(any(UserStoreRole.class));
    }
} 