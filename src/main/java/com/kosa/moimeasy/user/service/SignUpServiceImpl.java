package com.kosa.moimeasy.user.service;

import com.kosa.moimeasy.user.dto.SignupDTO;
import com.kosa.moimeasy.user.entity.Role;
import com.kosa.moimeasy.user.exception.DuplicateEmailException;
import com.kosa.moimeasy.user.exception.DuplicateNicknameException;
import com.kosa.moimeasy.user.exception.SignupException;
import com.kosa.moimeasy.security.repository.MemberRepository;
import com.kosa.moimeasy.security.repository.RoleRepository;
import com.kosa.moimeasy.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class SignUpServiceImpl implements SignUpService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired

    public SignUpServiceImpl(MemberRepository memberRepository,
                             RoleRepository roleRepository,
                             PasswordEncoder passwordEncoder) {
            this.memberRepository = memberRepository;
            this.roleRepository = roleRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public boolean signUp(SignupDTO dto) throws DuplicateEmailException, DuplicateNicknameException, SignupException {
            try {
                if (memberRepository.existsByEmail(dto.getEmail())) {
                    throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
                }
                if (memberRepository.existsByNickname(dto.getNickname())) {
        throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
    }
    Role userRole = roleRepository.findByRoleName("ROLE_USER")
            .orElseThrow(() -> new SignupException("사용자 역할을 찾을 수 없습니다."));

    User user = new User();
            user.setUserName(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setNickname(dto.getNickname());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setPhone(dto.getPhone());
            // 여기서 역할 설정 부분
//            user.setRole(user.getRole());
                user.setRole(userRole);
                user.setAccountNumber(generateVirtualAccountNumber()); // 가상 번호 저장

            memberRepository.save(user);
            log.info("회원가입 성공: " + user.toString());
                return true;
            } catch (DuplicateEmailException | DuplicateNicknameException e) {
                throw e;
            } catch (Exception e) {
                throw new SignupException("회원가입 중 오류가 발생했습니다.", e);
            }
        }
    @Override
    public boolean isEmailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public boolean isNicknameExists(String nickname) {
        return memberRepository.existsByNickname(nickname);
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
}
