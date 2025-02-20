package antigravity.user.repository;


import antigravity.global.exception.AntiException;
import antigravity.user.entity.User;
import antigravity.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    public User findById(Long id) {
        try{
            String query = "SELECT * FROM user WHERE id = :id";
            MapSqlParameterSource params = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(query, params, userRowMapper());
        }catch (Exception e) {
            throw new AntiException(UserErrorCode.USER_NOT_EXIST);
        }
    }

    public Long save(User user) {
        String sql = "insert into user (name, created_at) values (:name, :createdAt)";
        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getName());
        params.put("createdAt", user.getCreatedAt());
        jdbcTemplate.update(sql, new MapSqlParameterSource(params),generatedKeyHolder);
        return (long)generatedKeyHolder.getKeys().get("id");
    }
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
