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
import vuly.thesis.ecowash.core.payload.dto.ProductItemDto;
import vuly.thesis.ecowash.core.payload.request.ProductItemSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.ProductItemDtoRowMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductItemBriefDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public Page<ProductItemDto> findAll(ProductItemSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "SELECT pi.id, pi.code, pi.name, pi.tenant_id, pi.note, prt.value as product_type_value, pi.active, " +
                "pi.date_created, prt.name as product_type_name, pr.name as product_group_name, pt.name as piece_type_name, pt.value as piece_type_value " +
                "FROM product_item pi " +
                "LEFT JOIN piece_type pt ON pt.id = pi.piece_type_id " +
                "LEFT JOIN product_type prt ON prt.id = pi.product_type_id " +
                "LEFT JOIN product_group pr ON pr.id = pi.product_group_id " +
                condition(request, namedParameters) +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<ProductItemDto> listProductItemDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new ProductItemDtoRowMapper());

        return new PageImpl<>(listProductItemDto, pageable, this.count(request));
    }

    private StringBuilder condition(ProductItemSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE pi.active = true AND prt.value = :productTypeValue ");
        namedParameters.put("productTypeValue", request.getProductTypeValue());

        if (request.getCodeOrName() != null) {
            conditionBuilder.append(" AND (pi.code LIKE :codeOrName OR pi.name LIKE :codeOrName) ");
            namedParameters.put("codeOrName", "%" + request.getCodeOrName() + "%");
        }
        return conditionBuilder;
    }

    private String orders(Pageable pageable) {
        List<String> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            orders.add(" pi." + order.getProperty() + " " + order.getDirection());
        }
        return String.join(",", orders);
    }

    private int count(ProductItemSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder =
                "SELECT COUNT(*) " +
                        "FROM product_item pi " +
                        "LEFT JOIN piece_type pt ON pt.id = pi.piece_type_id " +
                        "LEFT JOIN product_type prt ON prt.id = pi.product_type_id " +
                        "LEFT JOIN product_group pr ON pr.id = pi.product_group_id " +
                        condition(request, namedParameters);
        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }
}
