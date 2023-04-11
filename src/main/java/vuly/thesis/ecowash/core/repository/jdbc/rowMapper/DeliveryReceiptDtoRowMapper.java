package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.payload.dto.DeliveryReceiptDto;
import vuly.thesis.ecowash.core.util.MapperUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class DeliveryReceiptDtoRowMapper implements RowMapper<DeliveryReceiptDto> {

    private final MapperUtil mapperUtil;

    @Override
    public DeliveryReceiptDto mapRow(ResultSet rs, int i) throws SQLException {
        return DeliveryReceiptDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .customerName(rs.getString("customerFullName"))
                .productType(rs.getString("productTypeName"))
                .dateCreated(mapperUtil.mapInstant(rs.getTimestamp("date_created")))
                .deliveryDate(mapperUtil.mapInstant(rs.getTimestamp("delivery_date")))
                .status(rs.getString("status"))
//                .specialInstructions(rs.getString("specialInsValuesNames"))
                .totalItem(columnExist(rs, "total_item") ? rs.getLong("total_item") : null)
                .receivedCode(rs.getString("receivedCodes"))
                .isExpress(rs.getBoolean("is_express"))
                .isDebt(rs.getBoolean("is_gen_by_debt"))
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