package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.entity.type.Status;
import vuly.thesis.ecowash.core.payload.dto.StaffDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffDtoRowMapper implements RowMapper<StaffDto> {

    @Override
    public StaffDto mapRow(ResultSet rs, int i) throws SQLException {
        return StaffDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .fullName(rs.getString("full_name"))
                .dateCreated(rs.getTimestamp("date_created").toInstant())
                .email(rs.getString("email"))
                .status(Status.valueOf(rs.getString("status")))
                .departmentName(rs.getString("department_name"))
                .phoneNumber(rs.getString("phone_number"))
                .customerName(rs.getString("customer_name"))
                .username(rs.getString("username"))
                .roles(rs.getString("role"))
                .build();
    }
}
