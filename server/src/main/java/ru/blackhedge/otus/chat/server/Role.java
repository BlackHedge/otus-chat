package ru.blackhedge.otus.chat.server;

public enum Role {
    USER("USER"), ADMIN("ADMIN");

    private String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

}
