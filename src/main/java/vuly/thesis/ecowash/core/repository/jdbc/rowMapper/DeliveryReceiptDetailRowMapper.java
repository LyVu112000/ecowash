package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.payload.dto.DeliveryReceiptDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class DeliveryReceiptDetailRowMapper implements RowMapper<DeliveryReceiptDto> {

    @Override
    public DeliveryReceiptDto mapRow(ResultSet rs, int i) throws SQLException {; //cast object
        return DeliveryReceiptDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .customerId(rs.getLong("customer_id"))
                .deliveryDate(rs.getTimestamp("delivery_date").toInstant())
                .status(rs.getString("status"))
                .numberOfLooseBags(rs.getInt("number_of_loose_bags"))
                .bagNumber(rs.getInt("bag_number"))
                .weight(rs.getLong("weight"))
                .staffCheck(rs.getString("staff_check"))
                .checkDate(rs.getTimestamp("check_date").toInstant())
                .numberRoom(rs.getString("number_room"))
                .note(rs.getString("note"))
                .productTypeValue(rs.getString("value"))
                .isExpress(rs.getBoolean("is_express"))
                .build();
    }
}