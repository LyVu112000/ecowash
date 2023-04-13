package vuly.thesis.ecowash.core.repository.jdbc.DAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.payload.dto.ProductGroupDto;
import vuly.thesis.ecowash.core.payload.request.ProductGroupSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.ProductGroupDtoRowMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductGroupDtoDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Page<ProductGroupDto> findAll(ProductGroupSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "SELECT pr.id, pr.code, pr.name, pr.note, pr.active, " +
                "pr.date_created, prt.name as product_type_name " +
                "FROM product_group pr " +
                "LEFT JOIN piece_type pt ON pt.id = pr.piece_type_id " +
                "LEFT JOIN product_type prt ON prt.id = pr.product_type_id " +
                condition(request, namedParameters) +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<ProductGroupDto> listProductGroupDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new ProductGroupDtoRowMapper());

        return new PageImpl<>(listProductGroupDto, pageable, this.count(request));
    }

    private StringBuilder condition(ProductGroupSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE pr >= 0 ");

        if (request.getCodeOrName() != null) {
            conditionBuilder.append(" AND (pr.code LIKE :codeOrName OR pr.name LIKE :codeOrName) ");
            namedParameters.put("codeOrName", "%" + request.getCodeOrName() + "%");
        }

        if (request.getProductTypeValue() != null) {
            conditionBuilder.append(" AND prt.value = :value  ");
            namedParameters.put("value", request.getProductTypeValue());
        }

        if (request.getActive() != null) {
            conditionBuilder.append(" AND pr.active = :active ");
            namedParameters.put("active", request.getActive() );
        }
        return conditionBuilder;
    }

    private String orders(Pageable pageable) {
        List<String> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            orders.add(" pr." + order.getProperty() + " " + order.getDirection());
        }
        return String.join(",", orders);
    }

    private int count(ProductGroupSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder =
                "SELECT COUNT(*) " +
                        "FROM product_group pr " +
                        "LEFT JOIN piece_type pt ON pt.id = pr.piece_type_id " +
                        "LEFT JOIN product_type prt ON prt.id = pr.product_type_id " +
                        condition(request, namedParameters);
        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }
}
