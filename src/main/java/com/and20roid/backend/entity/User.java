package com.and20roid.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;
    private String nickname;

    public User(String uid, String nickname) {
        this.uid = uid;
        this.nickname = nickname;
    }

    public User updateNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }
}
