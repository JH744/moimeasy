package com.kosa.moimeasy.user.entity;

import com.kosa.moimeasy.membership.entity.UserAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 100)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(length = 255)
    private String address;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY) // Role과의 관계 설정
    @JoinColumn(name = "role_id", nullable = false) // 외래 키 매핑
    private Role role;

    @Column(length = 25)
    private String nickname;

    @Column
    private Long moeimId;

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
        if(this.createAt == null){
            this.createAt = LocalDateTime.now();
        }else {
            this.createAt = createAt;
        }

    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "user")
    private List<UserAccount> userAccounts = new ArrayList<>();
}


//    public enum Role {
//        user,admin
//    }



//    @Column(name = "PROFILE_URL")
//    private String profileUrl;
//
//    // 다대다 관계 설정
//    @ManyToMany
//    @JoinTable(
//            name = "USER_MOEIM",
//            joinColumns = @JoinColumn(name = "USER_ID"),
//            inverseJoinColumns = @JoinColumn(name = "MOEIM_ID")
//    )
//    private Set<Moeim> moeims = new HashSet<>();



