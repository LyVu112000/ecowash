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
public class StaffDtoDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Page<StaffDto> findAll(StaffSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "SELECT s.id, s.code, s.full_name, s.email, s.date_created, s.status, s.role, " +
                "s.phone_number, s.username, d.name as department_name, c.full_name as customer_name  " +
                "FROM staff s " +
                "LEFT JOIN customer c ON c.id = s.customer_id " +
                "LEFT JOIN department d ON d.id = s.department_id " +
                condition(request, namedParameters) +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        log.info("Staff list query {}", queryBuilder);
        List<StaffDto> listProductGroupDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new StaffDtoRowMapper());

        return new PageImpl<>(listProductGroupDto, pageable, this.count(request));
    }

    private StringBuilder condition(StaffSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE s.is_customer = false ");


        if (request.getCodeOrUsername() != null) {
            conditionBuilder.append(" AND (s.code LIKE :codeOrName OR s.full_name LIKE :codeOrName) ");
            namedParameters.put("codeOrName", "%" + request.getCodeOrUsername() + "%");
        }

        if (request.getDepartmentId() > 0 ) {
            conditionBuilder.append(" AND s.department_id = :departmentId ");
            namedParameters.put("departmentId", request.getDepartmentId());
        }

        if (request.getStatus() != null) {
            conditionBuilder.append(" AND s.status = :status ");
            namedParameters.put("status", request.getStatus());
        }

        if (request.getEmail() != null) {
            conditionBuilder.append(" AND s.email LIKE :email ");
            namedParameters.put("email","%" + request.getEmail() + "%");
        }

        if (request.getRole() != null) {
            conditionBuilder.append(" AND s.role LIKE :role ");
            namedParameters.put("role","%" + request.getRole() + "%");
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
