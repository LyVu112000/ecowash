package vuly.thesis.ecowash.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.type.ReceiptStatus;
import vuly.thesis.ecowash.core.payload.dto.DashboardReceiptDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class DashboardReceiptMapper implements RowMapper<DashboardReceiptDto> {

    @Override
    public DashboardReceiptDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return DashboardReceiptDto.builder()
                .id(rs.getLong("id"))
                .status(ReceiptStatus.valueOf(rs.getString("status")))
                .code(rs.getString("code"))
                .time(rs.getTimestamp("time").toInstant())
                .isExpress(rs.getBoolean("is_express"))
                .isFlagError(rs.getBoolean("is_flag_error"))
                .build();
    }
}
