package com.jwlee.mochelin.model;

public class User {

    public String email;
    public String password;

    public String name;

    public String nickname;

    public User() {
    }

    public User(String email, String password, String name, String nickname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
