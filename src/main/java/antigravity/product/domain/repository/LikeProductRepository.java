package antigravity.product.domain.repository;

import antigravity.product.domain.entity.LikeProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class LikeProductRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    public Integer existsDipProductByUserIdAndProductId(Integer userId, Long productId) {
        String sql = "select count(*) from dip_product where user_id = :user_id and product_id = :product_id";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("product_id", productId);
        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }
    public Long save(LikeProduct likeProduct) {
        String sql = "insert into dip_product (user_id, product_id) values (:user_id, :product_id)";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", likeProduct.getUserId());
        params.put("product_id", likeProduct.getProductId());
        jdbcTemplate.update(sql, new MapSqlParameterSource(params),generatedKeyHolder);
        return (long)generatedKeyHolder.getKeys().get("id");
    }
    public Optional<LikeProduct> findById(Long id) {
        String query = "SELECT * FROM dip_product WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        return Optional.ofNullable(jdbcTemplate.queryForObject(query, params, dipProductRowMapper()));
    }

    public int countDipProductByProductId(long productId) {
        String sql = "select count(*) from dip_product where product_id = :product_id";
        MapSqlParameterSource param = new MapSqlParameterSource("product_id", productId);
        return jdbcTemplate.queryForObject(sql, param, Integer.class);
    }

    public int countDipProductByUserId(int userId) {
        String sql = "select count(*) from dip_product where user_id = :user_id";
        MapSqlParameterSource param = new MapSqlParameterSource("user_id", userId);
        return jdbcTemplate.queryForObject(sql, param, Integer.class);
    }

    // 유저가 찜한 상품 찾기
    public Page<LikeProduct> findAllByUserId(int userId, Pageable pageable) {
        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : Sort.Order.by("id");
        List<LikeProduct> likeProducts = jdbcTemplate.query("SELECT * FROM DIP_PRODUCT WHERE user_id = "+userId+" ORDER BY " + order.getProperty() + " " + order.getDirection().name() + " LIMIT " + pageable.getPageSize()
        +" OFFSET "+ pageable.getOffset(),dipProductRowMapper());
        return new PageImpl<LikeProduct>(likeProducts, pageable, countDipProductByUserId(userId));
    }

    private RowMapper<LikeProduct> dipProductRowMapper() {
        return (rs, rowNum) -> LikeProduct.builder()
                .id(rs.getLong("id"))
                .userId(rs.getInt("user_id"))
                .productId(rs.getLong("product_id"))
                .build();
    }
}