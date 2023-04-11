package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;


import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.payload.dto.ReceivedReceiptDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReceivedReceiptDtoRowMapper implements RowMapper<ReceivedReceiptDto> {
    @Override
    public ReceivedReceiptDto mapRow(ResultSet rs, int i) throws SQLException {; //cast object
        return ReceivedReceiptDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .customerName(rs.getString("full_name"))
                .productType(rs.getString("product_type_name"))
                .receivedDate(rs.getTimestamp("received_date").toInstant())
                .isRewash(rs.getBoolean("is_rewash"))
                .dateCreated(rs.getTimestamp("date_created").toInstant())
                .status(rs.getString("status"))
                .specialInstructions(rs.getString("name"))
                .deliveryCode(rs.getString("deliveryCode"))
                .totalItem(columnExist(rs, "total_item") ? rs.getLong("total_item") : null)
                .referenceCode(rs.getString("reference_code"))
                .build();
    }

    private boolean columnExist(ResultSet rs, String column){
        try{
            rs.findColumn(column);
            return true;
        } catch (SQLException exception){
            System.out.print("");
        }

        return false;
    }
}