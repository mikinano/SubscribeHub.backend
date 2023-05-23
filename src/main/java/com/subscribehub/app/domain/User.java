package com.subscribehub.app.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    @Column
    private String refreshToken;

    @Builder
    public User(String email, String nickname, String refreshToken) {
        this.email = email;
        this.nickname = nickname;
        this.refreshToken = refreshToken;
    }
    public User updateNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public User updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }
}
