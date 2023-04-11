package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.payload.dto.ContractDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContractDtoRowMapper implements RowMapper<ContractDto> {

        @Override
        public ContractDto mapRow(ResultSet rs, int i) throws SQLException {
            return ContractDto.builder()
                    .id(rs.getLong("id"))
                    .code(rs.getString("code"))
                    .customerName(rs.getString("customer_name"))
                    .expiredDate(rs.getTimestamp("expired_date") != null ? rs.getTimestamp("expired_date").toInstant() : null)
                    .validDate(rs.getTimestamp("valid_date") != null ? rs.getTimestamp("valid_date").toInstant() : null)
                    .dateCreated(rs.getTimestamp("date_created").toInstant())
                    .createdBy(rs.getString("created_by"))
                    .status(rs.getString("status"))
                    .customerId(rs.getLong("customer_id"))
                    .build();
        }
}
