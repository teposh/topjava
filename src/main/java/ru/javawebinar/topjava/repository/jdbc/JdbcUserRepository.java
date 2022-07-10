package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository extends AbstractJdbcRepository<User> implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        validate(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else {
            if (namedParameterJdbcTemplate.update("""
                       UPDATE users SET name = :name, email = :email, password = :password, registered = :registered,
                       enabled = :enabled, calories_per_day = :caloriesPerDay WHERE id = :id
                    """, parameterSource) == 0) {
                return null;
            }
            jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", user.getId());
        }

        insertUserRoles(user.getRoles(), user.getId());

        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users LEFT JOIN user_roles ON user_roles.user_id = users.id WHERE id=?",
                ROW_MAPPER,
                id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users LEFT JOIN user_roles ON user_roles.user_id = users.id WHERE email=?",
                ROW_MAPPER,
                email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM users LEFT JOIN user_roles ON user_roles.user_id = users.id ORDER BY name, email",
                ROW_MAPPER);
    }

    private final ResultSetExtractor<List<User>> ROW_MAPPER = new ResultSetExtractor<List<User>>() {
        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, User> users = new LinkedHashMap<>();

            while (rs.next()) {
                User user = users.get(rs.getInt("id"));

                if (user == null) {
                    user = mapRowToUser(rs);
                    users.put(user.getId(), user);
                }

                final String roleName = rs.getString("role");
                if (roleName != null) {
                    Set<Role> roles = user.getRoles();
                    roles.add(Role.valueOf(rs.getString("role")));
                    user.setRoles(roles);
                }
            }

            return new ArrayList<>(users.values());
        }

        private User mapRowToUser(ResultSet rs) throws SQLException {
            final String role = rs.getString("role");
            return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getInt("calories_per_day"),
                    rs.getBoolean("enabled"),
                    rs.getDate("registered"),
                    role != null ? List.of(Role.valueOf(role))
                            : List.of()
            );
        }
    };

    private void insertUserRoles(Set<Role> roles, int userId) {
        insertUserRoles(roles, userId, 200);
    }

    private void insertUserRoles(Set<Role> roles, int userId, int batchSize) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles (user_id, role) VALUES (?, ?)",
                roles,
                batchSize,
                (ps, argument) -> {
                    ps.setInt(1, userId);
                    ps.setString(2, argument.toString());
                });
    }
}
