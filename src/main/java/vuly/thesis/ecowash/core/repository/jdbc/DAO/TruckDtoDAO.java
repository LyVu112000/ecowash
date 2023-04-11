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
import vuly.thesis.ecowash.core.payload.dto.TruckDto;
import vuly.thesis.ecowash.core.payload.request.TruckSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.TruckDtoRowMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TruckDtoDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public Page<TruckDto> findAll(TruckSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "SELECT t.id, t.code, t.tenant_id, t.date_created, t.status,  " +
                " s.code as staff_code, s.full_name as staff_full_name, s.id as staff_id  " +
                "FROM truck t " +
                "LEFT JOIN staff s ON s.id = t.staff_id " +
                condition(request, namedParameters) +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        log.info("Truck list query {}", queryBuilder);
        List<TruckDto> listProductGroupDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new TruckDtoRowMapper());

        return new PageImpl<>(listProductGroupDto, pageable, this.count(request));
    }

    private StringBuilder condition(TruckSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE t.id >= 0 ");

        if (request.getCode() != null) {
            conditionBuilder.append(" AND (t.code LIKE :code) ");
            namedParameters.put("code", "%" + request.getCode() + "%");
        }

        if (request.getStaffCode() != null ) {
            conditionBuilder.append(" AND (s.code LIKE :staff_code) ");
            namedParameters.put("staff_code",  "%" + request.getStaffCode() + "%");
        }

        if (request.getStatus() != null) {
            conditionBuilder.append(" AND t.status = :status ");
            namedParameters.put("status", request.getStatus());
        }

        return conditionBuilder;
    }

    private String orders(Pageable pageable) {
        List<String> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            orders.add(" t." + order.getProperty() + " " + order.getDirection());
        }
        return String.join(",", orders);
    }

    private int count(TruckSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder =
                "SELECT COUNT(*) " +
                        "FROM truck t " +
                        "LEFT JOIN staff s ON s.id = t.staff_id " +
                        condition(request, namedParameters);
        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }
}
