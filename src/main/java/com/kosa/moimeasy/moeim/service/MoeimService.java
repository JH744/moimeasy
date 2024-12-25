package com.kosa.moimeasy.moeim.service;

import com.kosa.moimeasy.chat.entity.ChatRoomUser;
import com.kosa.moimeasy.chat.repository.ChatRoomRepository;
import com.kosa.moimeasy.chat.repository.ChatRoomUserRepository;
import com.kosa.moimeasy.invitation.entity.Invitation;
import com.kosa.moimeasy.invitation.repository.InvitationRepository;
import com.kosa.moimeasy.moeim.dto.MoeimDTO;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import com.kosa.moimeasy.security.repository.RoleRepository;
import com.kosa.moimeasy.user.entity.Role;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.chat.entity.ChatRoom;
import com.kosa.moimeasy.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MoeimService {

    @Autowired
    private MoeimRepository moeimRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ChatRoomUserRepository chatRoomUserRepository;

    public Moeim createMoeim(MoeimDTO request) {
        // 1. 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. Role 조회
        Role adminRole = roleRepository.findById(1L) // 관리자 역할 ID를 하드코딩 대신 설정 값으로 가져오는 것을 추천
            .orElseThrow(() -> new IllegalArgumentException("관리자 역할을 찾을 수 없습니다."));

        // 3. 모임 생성
        Moeim moeim = new Moeim();
        moeim.setMoeimName(request.getMoeimName());
        moeim.setUser(user); // User 객체를 설정
        moeim.setAccountNumber(generateVirtualAccountNumber());
        moeim.setMoeimCode(generateMoeimCode());
        Moeim savedMoeim = moeimRepository.save(moeim);

        // 4. 채팅방 생성 및 연결
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(request.getMoeimName() + " 단체톡방");
        chatRoom.setCreatedBy(request.getUserId());
        chatRoom.setMoeim(savedMoeim);
        chatRoom = chatRoomRepository.save(chatRoom);

        // 6. 모임장 채팅방 멤버로 추가
        ChatRoomUser chatRoomUser = new ChatRoomUser();
        chatRoomUser.setChatRoom(chatRoom);
        chatRoomUser.setUser(user);
        chatRoomUser.setUserNickname(user.getNickname());
        chatRoomUserRepository.save(chatRoomUser);

        // 모임에 채팅방 연결
        savedMoeim.setChatRoom(chatRoom);
        moeimRepository.save(savedMoeim);

        // 5. 사용자 업데이트 (모임 ID, 역할 변경)
        user.setMoeimId(savedMoeim.getMoeimId());
        user.setRole(adminRole); // 관리자 역할 설정
        user.setRole(user.getRole()); // Enum 값을 설정 /
        userRepository.save(user);

        return savedMoeim;
    }

    public Long joinMoeim(MoeimDTO request) {
        // 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 초대 이메일 검증
        Invitation invitation = invitationRepository.findFirstByEmailOrderByCreatedAtDesc(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("초대된 이메일이 아닙니다."));

        // 초대된 모임 ID로 모임 조회
        Moeim moeim = moeimRepository.findById(invitation.getMoeimId())
                .orElseThrow(() -> new IllegalArgumentException("초대받은 모임이 존재하지 않습니다."));

        // 모임 코드 검증
        if (!moeim.getMoeimCode().equals(request.getMoeimCode())) {
            throw new IllegalArgumentException("입력된 모임 코드가 초대받은 모임 코드와 일치하지 않습니다.");
        }

        // 사용자 모임 ID 및 역할 업데이트
        Role memberRole = roleRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("멤버 역할을 찾을 수 없습니다."));
        user.setMoeimId(moeim.getMoeimId());
        user.setRole(memberRole);
        userRepository.save(user);

        // 초대 상태를 COMPLETED로 업데이트
        invitation.setStatus(Invitation.InvitationStatus.COMPLETED);
        invitationRepository.save(invitation);

        // 모임의 채팅방에 사용자 추가
        ChatRoom chatRoom = moeim.getChatRoom();
        if (chatRoom != null) {
            ChatRoomUser chatRoomUser = new ChatRoomUser();
            chatRoomUser.setChatRoom(chatRoom);
            chatRoomUser.setUser(user);
            chatRoomUser.setUserNickname(user.getNickname());
            chatRoomUserRepository.save(chatRoomUser);
        }

        return moeim.getMoeimId(); // 반환
    }


    // 가상 계좌 번호 생성 메서드
    private String generateVirtualAccountNumber() {
        Random random = new Random();
        StringBuilder accountNum = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            accountNum.append(random.nextInt(10));
        }
        return accountNum.toString();
    }

    // 모임 코드 생성 메서드 (6자리 숫자)
    private String generateMoeimCode() {
        Random random = new Random();
        StringBuilder moeimCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            moeimCode.append(random.nextInt(10));
        }
        return moeimCode.toString();
    }


    
}
