package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;


import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.payload.dto.CustomerDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDtoRowMapper implements RowMapper<CustomerDto> {

    @Override
    public CustomerDto mapRow(ResultSet rs, int i) throws SQLException {
        return CustomerDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .fullName(rs.getString("full_name"))
                .dateCreated(rs.getTimestamp("date_created").toInstant())
                .email(rs.getString("email"))
                .address(rs.getString("address"))
                .active(rs.getString("active"))
                .build();
    }
}
