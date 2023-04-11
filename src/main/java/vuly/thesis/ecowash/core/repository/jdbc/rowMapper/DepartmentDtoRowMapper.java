package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;


import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.payload.dto.DepartmentDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentDtoRowMapper implements RowMapper<DepartmentDto> {

    @Override
    public DepartmentDto mapRow(ResultSet rs, int i) throws SQLException {
        return DepartmentDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .totalStaff(rs.getLong("total_staff"))
                .note(rs.getString("note"))
                .active(rs.getBoolean("active"))
                .phoneNumber(rs.getString("phone_number"))
                .build();
    }
}
