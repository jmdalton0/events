package com.jmd0.events.data;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.jmd0.events.models.UserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class UserRepository implements UserRepositoryInterface {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserEntity findByLoginName(String username) {
        String sql = "SELECT u.*, r.role FROM users u LEFT JOIN roles r ON u.id = r.user_id WHERE u.username= ?";
        Object[] params = {username};
        int[] types = {java.sql.Types.VARCHAR};
        try {
            List<UserEntity> users = jdbcTemplate.query(sql, params, types, new UserWithRolesExtractor());
            if (users == null || users.isEmpty()) {
                return null;
            } else {
                return users.get(0);
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<UserEntity> findAll() {
        String sql = "SELECT u.*, r.role FROM users u LEFT JOIN roles r ON u.id = r.user_id";
        return jdbcTemplate.query(sql, new UserWithRolesExtractor());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        Object[] params = {id};
        int[] types = {java.sql.Types.BIGINT};
        jdbcTemplate.update(sql, params, types);
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        if (userEntity.getId() == null) {

            if (userEntity.getRoles() == null) {
                userEntity.setRoles(new HashSet<>(Arrays.asList("ROLE_USER", "ROLE_ADMIN")));
            } else {
                userEntity.getRoles().add("ROLE_USER");
            }

            String sql = "INSERT INTO users (username, password, enabled, account_non_expired, credentials_non_expired, account_non_locked)" + 
                "VALUES ('?', '?', '?', '?', '?', '?')";
            Object[] params = {
                userEntity.getusername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNonExpired(),
                userEntity.isCredentialsNonExpired(),
                userEntity.isAccountNonLocked()
            };
            int[] types = {
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.TINYINT,
                java.sql.Types.TINYINT,
                java.sql.Types.TINYINT,
                java.sql.Types.TINYINT
            };
            jdbcTemplate.update(sql);
            Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", params, types, Long.class);
            userEntity.setId(id);
        } else {
            String sql = "UPDATE users SET username = '?'," +
                    "password = '?'," +
                    "enabled = '?'," +
                    "account_non_expired = '?'," +
                    "credentials_non_expired = '?'" +
                    "account_non_locked = '?'" +
                    " WHERE id = '?'";
            Object[] params = {
                userEntity.getusername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNonExpired(),
                userEntity.isCredentialsNonExpired(),
                userEntity.isAccountNonLocked(),
                userEntity.getId()
            };
            int[] types = {
                java.sql.Types.VARCHAR,
                java.sql.Types.VARCHAR,
                java.sql.Types.TINYINT,
                java.sql.Types.TINYINT,
                java.sql.Types.TINYINT,
                java.sql.Types.TINYINT,
                java.sql.Types.BIGINT
            };
            jdbcTemplate.update(sql, params, types);
        }

        deleteRoles(userEntity);
        saveRoles(userEntity);

        return userEntity;
    }

    public void saveRoles(UserEntity userEntity) {
        if (userEntity.getRoles() != null) {
            for (String role : userEntity.getRoles()) {
                String sql = "INSERT INTO roles (user_id, role) VALUES (" + userEntity.getId() + ", '" + role + "')";
                jdbcTemplate.update(sql);
            }
        }
    }

    public void deleteRoles(UserEntity userEntity) {
        String sql = "DELETE FROM roles WHERE user_id = " + userEntity.getId();
        jdbcTemplate.update(sql);
    }

    @Override
    public UserEntity findById(Long id) {
        String sql = "SELECT u.*, r.role FROM users u LEFT JOIN roles r ON u.id = r.user_id WHERE u.id = " + id;
        try {
            List<UserEntity> users = jdbcTemplate.query(sql, new UserWithRolesExtractor());
            if (users == null || users.isEmpty()) {
                return null;
            } else {
                return users.get(0);
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        Long result = jdbcTemplate.queryForObject(sql, Long.class);
        return result != null ? result : 0;
    }

    @Override
    public void delete(UserEntity user) {
        deleteById(user.getId());
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM users";
        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteAll(Iterable<? extends UserEntity> users) {
        for (UserEntity user : users) {
            delete(user);
        }
    }

    @Override
    public List<UserEntity> saveAll(Iterable<UserEntity> users) {
        for (UserEntity user : users) {
            save(user);
        }
        return (List<UserEntity>) users;
    }

    private static class UserWithRolesExtractor implements ResultSetExtractor<List<UserEntity>> {
        @Override
        public List<UserEntity> extractData(@SuppressWarnings("null") ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, UserEntity> userMap = new HashMap<>();
            while (rs.next()) {
                Long id = rs.getLong("id");
                UserEntity user = userMap.get(id);
                if (user == null) {
                    user = new UserEntity();
                    user.setId(id);
                    user.setusername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    user.setRoles(new HashSet<>());
                    userMap.put(id, user);
                }
                String role = rs.getString("role");
                if (role != null) {
                    user.getRoles().add(role);
                }
            }
            return new ArrayList<>(userMap.values());
        }
    }
}
