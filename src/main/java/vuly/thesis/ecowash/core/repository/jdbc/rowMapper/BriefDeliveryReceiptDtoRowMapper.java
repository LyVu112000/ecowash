package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;


import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.payload.dto.BriefDeliveryReceiptDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BriefDeliveryReceiptDtoRowMapper implements RowMapper<BriefDeliveryReceiptDto> {

    @Override
    public BriefDeliveryReceiptDto mapRow(ResultSet rs, int i) throws SQLException {; //cast object
        return BriefDeliveryReceiptDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .customerId(rs.getLong("customer_id"))
                .productTypeValue(rs.getString("product_type_value"))
                .status(rs.getString("status"))
                .build();
    }
}