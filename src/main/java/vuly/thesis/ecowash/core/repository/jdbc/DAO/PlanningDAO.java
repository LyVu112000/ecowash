package vuly.thesis.ecowash.core.repository.jdbc.DAO;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.payload.dto.PlanningDto;
import vuly.thesis.ecowash.core.payload.request.PlanningSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.PlanningRowMapper;
import vuly.thesis.ecowash.core.util.DateTimeUtil;
import vuly.thesis.ecowash.core.util.EbstUserRequest;

import java.util.LinkedList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlanningDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final EbstUserRequest ebstUserRequest;
    private final PlanningRowMapper rowMapper;

    public Page<PlanningDto> findAll(PlanningSearchRequest request, Pageable pageable) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        String queryBuilder = "SELECT DISTINCT pl.id, pl.tenant_id, pl.date_created, pl.created_by, pl.code, pl.name, pl.from_date, pl.to_date, pl.status " +
                "FROM planning pl " +
                "LEFT JOIN planning_contract plc ON plc.planning_id = pl.id " +
                "LEFT JOIN contract ct ON ct.id = plc.contract_id " +
                "LEFT JOIN customer cus ON cus.id = ct.customer_id " +
                condition(request, parameterSource) +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<PlanningDto> planningDtos = namedParameterJdbcTemplate.query(
                queryBuilder,
                parameterSource,
                rowMapper::mapRow
        );

        return new PageImpl<>(planningDtos, pageable, this.count(request));
    }

    private StringBuilder condition(PlanningSearchRequest request, MapSqlParameterSource parameterSource) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append("WHERE pl.id >= 0 ");

        if (request.getCodeOrName() != null) {
            conditionBuilder.append(" AND (pl.code LIKE :codeOrName OR pl.name LIKE :codeOrName) ");
            parameterSource.addValue("codeOrName", "%" + request.getCodeOrName().trim() + "%");
        }

        if (request.getStatus() != null) {
            conditionBuilder.append(" AND pl.status = :status");
            parameterSource.addValue("status", request.getStatus());
        }

        if (request.getFromDate() != null) {
            conditionBuilder.append(" AND pl.from_date >= :fromDate");
            parameterSource.addValue("fromDate", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest));
        }

        if (request.getToDate() != null) {
            conditionBuilder.append(" AND pl.from_date <= :toDate");
            parameterSource.addValue("toDate", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest));
        }

        if (request.getCustomerFullName() != null) {
            conditionBuilder.append(" AND cus.full_name LIKE :customerFullName");
            parameterSource.addValue("customerFullName", "%" + request.getCustomerFullName() + "%");
        }
        return conditionBuilder;
    }

    private String orders(Pageable pageable) {
        List<String> orders = new LinkedList<>();
        for (Sort.Order order : pageable.getSort()) {
            orders.add(" pl." + order.getProperty() + " " + order.getDirection());
        }
        return String.join(",", orders);
    }

    private int count(PlanningSearchRequest request) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String queryBuilder =
                "SELECT COUNT(*) " +
                        "FROM planning pl " +
                        "LEFT JOIN planning_contract plc ON plc.planning_id = pl.id " +
                        "LEFT JOIN contract ct ON ct.id = plc.contract_id " +
                        "LEFT JOIN customer cus ON cus.id = ct.customer_id " +
                        condition(request, namedParameters);

        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }

    public PlanningDto findById(Long id) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        String queryBuilder = "SELECT DISTINCT pl.id, pl.tenant_id, pl.date_created, pl.created_by, pl.code, " +
                "pl.name, pl.from_date, pl.to_date, pl.status " +
                "FROM planning pl " +
                "WHERE pl.id = :id";
        parameterSource.addValue("id", id);

        return namedParameterJdbcTemplate.queryForObject(
                queryBuilder,
                parameterSource,
                rowMapper::mapRow
        );
    }
}
