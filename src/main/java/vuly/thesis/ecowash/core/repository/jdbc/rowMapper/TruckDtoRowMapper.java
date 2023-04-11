package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.entity.type.Status;
import vuly.thesis.ecowash.core.payload.dto.StaffDto;
import vuly.thesis.ecowash.core.payload.dto.TruckDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TruckDtoRowMapper implements RowMapper<TruckDto> {

    @Override
    public TruckDto mapRow(ResultSet rs, int i) throws SQLException {
        return TruckDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .dateCreated(rs.getTimestamp("date_created").toInstant())
                .status(Status.valueOf(rs.getString("status")))
                .staff(StaffDto.builder()
                        .id(rs.getLong("staff_id"))
                        .code(rs.getString("staff_code"))
                        .fullName(rs.getString("staff_full_name"))
                        .build())
                .build();
    }
}
