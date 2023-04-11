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
import vuly.thesis.ecowash.core.payload.dto.ContractDto;
import vuly.thesis.ecowash.core.payload.request.ContractSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.ContractDtoRowMapper;
import vuly.thesis.ecowash.core.util.DateTimeUtil;
import vuly.thesis.ecowash.core.util.EbstUserRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContractDtoDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final EbstUserRequest ebstUserRequest;

    public Page<ContractDto> findAll(ContractSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "SELECT c.id, c.code, c.tenant_id, c.date_created, c.valid_date, c.expired_date, " +
                "c.created_by, c.status, ct.full_name as customer_name, c.customer_id " +
                "FROM contract c " +
                "LEFT JOIN customer ct ON ct.id = c.customer_id " +
                condition(request, namedParameters) +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<ContractDto> listContractDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new ContractDtoRowMapper());

        return new PageImpl<>(listContractDto, pageable, this.count(request));
    }

    private StringBuilder condition(ContractSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE c.active = true ");

        if (request.getCode() != null) {
            conditionBuilder.append(" AND c.code LIKE :code ");
            namedParameters.put("code", "%" + request.getCode() + "%");
        }

        if (request.getStatus() != null) {
            conditionBuilder.append(" AND c.status = :status ");
            namedParameters.put("status", request.getStatus());
        }

        if (request.getCustomerId() != null) {
            conditionBuilder.append(" AND ct.id = :customerId ");
            namedParameters.put("customerId", request.getCustomerId());
        }

        if (request.getCustomerCodeOrName() != null) {
            conditionBuilder.append(" AND ( ct.code LIKE :codeOrName or ct.full_name LIKE :codeOrName ) ");
            namedParameters.put("codeOrName", "%" + request.getCustomerCodeOrName() + "%");
        }

        // need to update
        if (request.getFromDate() != null && request.getToDate() != null) {
            conditionBuilder.append(" AND (c.date_created BETWEEN :fromDate AND :toDate) ");
            namedParameters.put("fromDate", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest));
            namedParameters.put("toDate", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest));
        } else if (request.getFromDate() != null) {
            conditionBuilder.append(" AND c.date_created >= :fromDate ");
            namedParameters.put("fromDate", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest));
        }
        if (request.getToDate() != null) {
            conditionBuilder.append(" AND c.date_created <= :toDate ");
            namedParameters.put("toDate", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest));
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

    private int count(ContractSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder =
                "SELECT COUNT(*) " +
                        "FROM contract c " +
                        "LEFT JOIN customer ct ON ct.id = c.customer_id " +                        condition(request, namedParameters);
        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }
}
