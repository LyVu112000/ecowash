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
import vuly.thesis.ecowash.core.payload.dto.CustomerDto;
import vuly.thesis.ecowash.core.payload.request.CustomerSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.CustomerDtoRowMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerDtoDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Page<CustomerDto> findAll(CustomerSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "SELECT c.id, c.code, c.full_name, c.email, c.address, c.date_created, c.active  " +
                "FROM customer c " +
                condition(request, namedParameters) +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<CustomerDto> listProductGroupDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new CustomerDtoRowMapper());

        return new PageImpl<>(listProductGroupDto, pageable, this.count(request));
    }

    private StringBuilder condition(CustomerSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE c.id >= 0 ");

        if (request.getCodeOrName() != null) {
            conditionBuilder.append(" AND (c.code LIKE :codeOrName OR c.full_name LIKE :codeOrName) ");
            namedParameters.put("codeOrName", "%" + request.getCodeOrName() + "%");
        }

        if (request.getActive() != null) {
            conditionBuilder.append(" AND c.active = :active ");
            namedParameters.put("active", request.getActive());
        }
        return conditionBuilder;
    }

    private String orders(Pageable pageable) {
        List<String> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            orders.add(" c." + order.getProperty() + " " + order.getDirection());
        }
        return String.join(",", orders);
    }

    private int count(CustomerSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder =
                "SELECT COUNT(*) " +
                        "FROM customer c " +
                        condition(request, namedParameters);
        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }
}
