package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.payload.dto.ItemDeliveryDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class ItemDeliveryRowMapper implements RowMapper<ItemDeliveryDto> {

    public ItemDeliveryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ItemDeliveryDto.builder()
                .id(columnExist(rs, "item_delivery_id") ? rs.getLong("item_delivery_id") : null)
                .receivedReceiptId(columnExist(rs, "received_receipt_id") ? rs.getLong("received_receipt_id") : null)
                .productItemId(rs.getLong("product_item_id"))
                .productItemName(rs.getString("product_item_name"))
                .productItemCode(rs.getString("product_item_code"))
                .numberReceived(columnExist(rs, "number_received") ? rs.getInt("number_received") : null)
                .numberDelivery(columnExist(rs, "number_delivery") ? rs.getInt("number_delivery") : null)
                .laundryFormName(rs.getString("laundry_form_name"))
                .laundryFormId(columnExist(rs, "laundry_form_id") ? rs.getLong("laundry_form_id") : null)
                .laundryFormValue(columnExist(rs, "laundry_form_value") ? rs.getString("laundry_form_value") : null)
                .note(columnExist(rs, "note") ? rs.getString("note") : null)
                .randomNumberCheck(columnExist(rs, "random_number_check") ? rs.getInt("random_number_check") : null)
                .numberAfterProduction(columnExist(rs, "number_after_production") ? rs.getInt("number_after_production") : null)
                .numberDeliveryActual(columnExist(rs, "number_delivery_actual") ? rs.getInt("number_delivery_actual") : null)
                .receivedDate(columnExist(rs, "received_date") ? rs.getTimestamp("received_date").toInstant() : null)
                .pieceTypeName(rs.getString("piece_type_name"))
                .deliveryReceiptId(rs.getLong("delivery_receipt_id"))
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
