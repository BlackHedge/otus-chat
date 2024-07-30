package ru.blackhedge.otus.chat.server;

public class User {
    private String login;
    private String password;
    private String nickname;
    private Role role;
    private boolean isActive;

    public User(String nickname) {
        this.nickname = nickname;
    }

    public User(String login, String password, String nickname, Role role, boolean isActive) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}