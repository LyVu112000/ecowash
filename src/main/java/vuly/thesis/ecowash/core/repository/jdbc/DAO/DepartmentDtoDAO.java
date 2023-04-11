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
import vuly.thesis.ecowash.core.payload.dto.DepartmentDto;
import vuly.thesis.ecowash.core.payload.request.DepartmentSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.DepartmentDtoRowMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DepartmentDtoDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public Page<DepartmentDto> findAll(DepartmentSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "SELECT d.id, d.code, d.name, d.note, d.active, d.tenant_id, d.phone_number, " +
                "(select count(s.id) from staff s where s.department_id = d.id) as total_staff " +
                "FROM department d " +
                condition(request, namedParameters) +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<DepartmentDto> listDepartmentDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new DepartmentDtoRowMapper());

        return new PageImpl<>(listDepartmentDto, pageable, this.count(request));
    }

    private StringBuilder condition(DepartmentSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE d.id >= 0  ");

        if (request.getCodeOrName() != null) {
            conditionBuilder.append(" AND (d.code LIKE :codeOrName OR d.name LIKE :codeOrName) ");
            namedParameters.put("codeOrName", "%" + request.getCodeOrName() + "%");
        }
        return conditionBuilder;
    }

    private String orders(Pageable pageable) {
        List<String> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            orders.add(" d." + order.getProperty() + " " + order.getDirection());
        }
        return String.join(",", orders);
    }

    private int count(DepartmentSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder =
                "SELECT COUNT(*) " +
                        "FROM department d " +
                        condition(request, namedParameters);

        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }
}
