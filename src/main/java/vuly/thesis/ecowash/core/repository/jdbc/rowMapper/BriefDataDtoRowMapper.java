package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.payload.dto.BriefDataDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class BriefDataDtoRowMapper {

    public BriefDataDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return BriefDataDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("deliveryCode"))
                .deliveryId(rs.getLong("deliveryId"))
                .status(rs.getString("status"))
                .isFlagError(rs.getBoolean("is_flag_error"))
                .isDebt(rs.getBoolean("is_gen_by_debt"))
                .build();
    }
}
