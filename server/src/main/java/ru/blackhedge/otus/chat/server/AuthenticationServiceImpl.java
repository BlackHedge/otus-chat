package ru.blackhedge.otus.chat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static ru.blackhedge.otus.chat.server.Role.ADMIN;
import static ru.blackhedge.otus.chat.server.Role.USER;

public class AuthenticationServiceImpl implements AuthenticationService, AutoCloseable {
    private final String DB_AUTH_URL = "jdbc:postgresql://localhost:5432/auth";
    private final Connection connection = DriverManager.getConnection(DB_AUTH_URL, "postgres", "password");
    private Server server;
    List<User> users;

    public AuthenticationServiceImpl(Server server) throws SQLException {
        this.server = server;
        this.users = new ArrayList<>();
    }

    public String getConnection() {
        return connection.toString();
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        String rq = "SELECT nickname FROM users WHERE login = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(rq)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rsp = ps.executeQuery();
            while (rsp.next()) {
                return rsp.getString("nickname");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean register(String login, String password, String nickname) {
        String rq = "INSERT INTO users (login, password, nickname, role) VALUES (?, ?, ?, ?)";
        if (isLoginAlreadyExist(login)) {
            return false;
        }
        if (isNicknameAlreadyExist(nickname)) {
            return false;
        }
        try (PreparedStatement ps = connection.prepareStatement(rq)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, nickname);
            ps.setString(4, USER.getRoleName());
            ps.executeUpdate();
            users.add(new User(nickname));
            return true;
        } catch (SQLException ex) {
            ex.getMessage();
            return false;
        }
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        String rq = "SELECT count(login) FROM users WHERE login = ?";
        try (PreparedStatement ps = connection.prepareStatement(rq)) {
            ps.setString(1, login);
            ResultSet rsp = ps.executeQuery();
            while (rsp.next()) {
                if (rsp.getInt(1) == 0) {
                    return false;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean isNicknameAlreadyExist(String nickname) {
        String rq = "SELECT count(nickname) FROM users WHERE nickname = ?";
        try (PreparedStatement ps = connection.prepareStatement(rq)) {
            ps.setString(1, nickname);
            ResultSet rsp = ps.executeQuery();
            while (rsp.next()) {
                if (rsp.getInt(1) == 0) {
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean hasPermission(String nickname) {
        String rq = "SELECT role FROM users WHERE nickname = ?";
        try (PreparedStatement ps = connection.prepareStatement(rq)) {
            ps.setString(1, nickname);
            ResultSet rsp = ps.executeQuery();
            while (rsp.next()) {
                if (rsp.toString().equals(ADMIN.getRoleName())) {
                    return true;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        server.searchByNickname(nickname).sendMessage("У вас недостаточно прав для выполнения команды");
        return false;
    }

    @Override
    public void close() throws Exception {
        try {
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
