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
import vuly.thesis.ecowash.core.payload.dto.StaffDto;
import vuly.thesis.ecowash.core.payload.request.StaffSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.StaffDtoRowMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerAccountDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Page<StaffDto> findAll(StaffSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "SELECT s.id, s.code, s.full_name, s.email, s.username, s.date_created, s.role, " +
                "s.status, s.phone_number, c.full_name as customer_name, d.name as department_name " +
                "FROM staff s " +
                "LEFT JOIN customer c ON c.id = s.customer_id " +
                "LEFT JOIN department d ON d.id = s.department_id " +
                condition(request, namedParameters) +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<StaffDto> listProductGroupDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new StaffDtoRowMapper());

        return new PageImpl<>(listProductGroupDto, pageable, this.count(request));
    }

    private StringBuilder condition(StaffSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE s.is_customer = true ");

        if (request.getCodeOrUsername() != null) {
            conditionBuilder.append(" AND (s.code LIKE :codeOrUsername OR s.username LIKE :codeOrUsername) ");
            namedParameters.put("codeOrUsername", "%" + request.getCodeOrUsername() + "%");
        }

        if (request.getCustomerCodeOrName() != null) {
            conditionBuilder.append(" AND ( c.code LIKE :codeOrName or c.full_name LIKE :codeOrName ) ");
            namedParameters.put("codeOrName", "%" + request.getCustomerCodeOrName() + "%");
        }

        if (request.getCustomerId() > 0 ) {
            conditionBuilder.append(" AND s.customer_id = :customerId ");
            namedParameters.put("customerId", request.getCustomerId());
        }

        if (request.getStatus() != null) {
            conditionBuilder.append(" AND s.status = :status ");
            namedParameters.put("status", request.getStatus());
        }

        if (request.getEmail() != null) {
            conditionBuilder.append(" AND s.email LIKE :email ");
            namedParameters.put("email","%" + request.getEmail() + "%");
        }
        return conditionBuilder;
    }

    private String orders(Pageable pageable) {
        List<String> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            orders.add(" s." + order.getProperty() + " " + order.getDirection());
        }
        return String.join(",", orders);
    }

    private int count(StaffSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder =
                "SELECT COUNT(*) " +
                        "FROM staff s " +
                        "LEFT JOIN customer c ON c.id = s.customer_id " +
                        "LEFT JOIN department d ON d.id = s.department_id " +
                        condition(request, namedParameters);
        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }
}
