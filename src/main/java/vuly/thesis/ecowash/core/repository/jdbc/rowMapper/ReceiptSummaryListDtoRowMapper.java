package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;


import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.entity.type.ReceiptStatus;
import vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryListDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReceiptSummaryListDtoRowMapper implements RowMapper<ReceiptSummaryListDto> {
    @Override
    public ReceiptSummaryListDto mapRow(ResultSet rs, int i) throws SQLException {
        return ReceiptSummaryListDto.builder()
                .id(rs.getLong("id"))
                .receivedCode(rs.getString("code"))
                .customerName(rs.getString("customerName"))
                .productType(rs.getString("productType"))
                .deliveryCode(rs.getString("deliveryCode"))
                .status(ReceiptStatus.valueOf(rs.getString("status")))
                .totalReceived(rs.getInt("totalReceived"))
                .totalDelivery(rs.getInt("totalDelivery"))
                .totalAfterProduction(rs.getInt("totalAfterProduction"))
                .totalRandomCheck(rs.getInt("totalRandomCheck"))
                .build();
    }

}