package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;


import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.payload.dto.SpecialInstructionReceiptDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecialInstructionReceiptDtoRowMapper implements RowMapper<SpecialInstructionReceiptDto> {

    @Override
    public SpecialInstructionReceiptDto mapRow(ResultSet rs, int i) throws SQLException {; //cast object
        return SpecialInstructionReceiptDto.builder()
                .value(rs.getString("value"))
                .name(rs.getString("name"))
                .build();
    }
}
