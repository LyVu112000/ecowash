package vuly.thesis.ecowash.core.repository.jdbc.DAO;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.mapper.DashboardReceiptMapper;
import vuly.thesis.ecowash.core.payload.dto.DashboardReceiptDto;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardReceiptDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DashboardReceiptMapper rowMapper;

    public List<DashboardReceiptDto> getDeliveryDashboard(Instant fromDate, Instant toDate) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        String queryBuilder =
                "select dr.id, dr.code, dr.status, dr.is_express, dr.delivery_date time, dr.is_flag_error " +
                "from delivery_receipt dr " +
                "where dr.status not in ('DONE', 'CANCEL') " +
                "and dr.delivery_date BETWEEN :fromDate AND :toDate " +
                "ORDER BY (dr.delivery_date < now()) desc, FIELD(dr.is_express, true) desc, dr.delivery_date asc " ;
        parameterSource.addValue("fromDate", fromDate.toString());
        parameterSource.addValue("toDate", toDate.toString());

        return namedParameterJdbcTemplate.query(
                queryBuilder, parameterSource, rowMapper::mapRow);
    }

    public List<DashboardReceiptDto> getReceivedDashboard(Instant fromDate, Instant toDate) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String queryBuilder =
                "select rr.id, rr.code, rr.status, rr.is_express, rr.received_date time, rr.is_flag_error  " +
                        "from received_receipt rr " +
                        "where rr.status not in ('DONE', 'CANCEL') " +
                        "and (rr.received_date BETWEEN :fromDate AND :toDate) " +
                        "ORDER BY (rr.received_date < now() and rr.status = 'WAITING') desc, FIELD(rr.is_express, true) desc, rr.received_date asc";
        parameterSource.addValue("fromDate", fromDate.toString());
        parameterSource.addValue("toDate", toDate.toString());

        return namedParameterJdbcTemplate.query(
                queryBuilder, parameterSource, rowMapper::mapRow);
    }
}
