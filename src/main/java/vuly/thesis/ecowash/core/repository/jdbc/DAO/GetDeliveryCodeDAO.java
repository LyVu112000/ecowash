package vuly.thesis.ecowash.core.repository.jdbc.DAO;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.payload.dto.BriefDataDto;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.BriefDataDtoRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GetDeliveryCodeDAO {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final BriefDataDtoRowMapper rowMapper;

    public List<BriefDataDto> getDeliveryCode(Long receiptId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        String queryBuilder = "SELECT rr.id, dr.id deliveryId, group_concat(distinct(dr.code)) deliveryCode, dr.status, " +
                "dr.is_flag_error, dr.is_gen_by_debt " +
                "from received_receipt rr " +
                "left join item_delivery itd on itd.received_receipt_id = rr.id " +
                "left join delivery_receipt dr on dr.id = itd.delivery_receipt_id " +
                "where rr.id = " + receiptId +
                " group by rr.id, dr.id ";
        List<BriefDataDto> briefDataDtoList = namedParameterJdbcTemplate.query(
                queryBuilder,
                parameterSource,
                rowMapper::mapRow
        );
        return briefDataDtoList;
    }
}
