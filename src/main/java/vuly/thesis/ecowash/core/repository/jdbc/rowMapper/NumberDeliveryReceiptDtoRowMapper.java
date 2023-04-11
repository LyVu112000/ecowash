package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.payload.dto.NumberDeliveryReceiptDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NumberDeliveryReceiptDtoRowMapper implements RowMapper<NumberDeliveryReceiptDto> {

        @Override
        public NumberDeliveryReceiptDto mapRow(ResultSet rs, int i) throws SQLException {
            return NumberDeliveryReceiptDto.builder()
                    .receiptId(rs.getLong("received_receipt_id"))
                    .productItemId(rs.getLong("product_item_id"))
                    .productItemName(rs.getString("pName"))
                    .productItemValue(rs.getString("pCode"))
                    .pieceTypeValue(rs.getString("ppValue"))
                    .pieceTypeName(rs.getString("ppName"))
                    .laundryFormId(rs.getLong("laundry_form_id"))
                    .laundryFormName(rs.getString("ldName"))
                    .laundryFormValue(rs.getString("ldValue"))
                    .numberReceived(rs.getInt("number_received"))
                    .numberDelivery(isThere(rs, "numberDelivery") ? rs.getInt("numberDelivery") : 0)
                    .numberDeliveryActual(isThere(rs, "numberDeliveryActual") ? rs.getInt("numberDeliveryActual") : 0)
                    .numberCanDelivery(rs.getInt("number_received") - rs.getInt("total_delivery"))
                    .randomNumberCheck(rs.getInt("random_number_check"))
                    .numberAfterProduction(rs.getInt("number_after_production"))
                    .note(rs.getString("note"))
                    .receivedCode(rs.getString("receivedCode"))
                    .numberRoom(rs.getString("number_room"))
                    .build();
        }

    private boolean isThere(ResultSet rs, String column){
        try{
            rs.findColumn(column);
            return true;
        } catch (SQLException sqlex){
        }

        return false;
    }
}
