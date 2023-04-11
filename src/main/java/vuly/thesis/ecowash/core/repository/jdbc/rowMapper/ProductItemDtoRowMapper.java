package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.payload.dto.ProductItemDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductItemDtoRowMapper implements RowMapper<ProductItemDto> {

    @Override
    public ProductItemDto mapRow(ResultSet rs, int i) throws SQLException {; //cast object
        return ProductItemDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .productTypeName(rs.getString("product_type_name"))
                .productGroupName(rs.getString("product_group_name"))
                .dateCreated(rs.getTimestamp("date_created").toInstant())
                .note(rs.getString("note"))
                .pieceTypeName(rs.getString("piece_type_name"))
                .productTypeValue(rs.getString("product_type_value"))
                .pieceTypeValue(rs.getString("piece_type_value"))
                .active(rs.getString("active"))
                .build();
    }
}
