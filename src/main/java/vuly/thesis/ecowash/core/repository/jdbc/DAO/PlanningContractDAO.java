package vuly.thesis.ecowash.core.repository.jdbc.DAO;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.payload.dto.PlanningContractDto;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.PlanningContractRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlanningContractDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PlanningContractRowMapper rowMapper;

    public List<PlanningContractDto> findByPlanningId(Long planningId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        String queryBuilder = "SELECT plc.id, plc.time_received, plc.time_delivery, plc.washing_team, plc.bag_number, plc.note, " +
                "ct.id AS contract_id, ct.code AS contract_code, " +
                "cus.id AS customer_id, cus.code AS customer_code, cus.full_name AS customer_full_name, " +
                "pt.id AS product_type_id, pt.name AS product_type_name, pt.value AS product_type_value " +
                "FROM planning_contract plc " +
                "LEFT JOIN contract ct ON ct.id = plc.contract_id " +
                "LEFT JOIN customer cus ON cus.id = plc.customer_id " +
                "LEFT JOIN product_type pt ON pt.id = plc.product_type_id " +
                "WHERE plc.planning_id = :planningId";
        parameterSource.addValue("planningId", planningId);

        return namedParameterJdbcTemplate.query(
                queryBuilder,
                parameterSource,
                rowMapper::mapRow
        );
    }
}
