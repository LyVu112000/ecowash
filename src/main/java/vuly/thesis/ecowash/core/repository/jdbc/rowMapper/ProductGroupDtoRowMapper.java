package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;


import org.springframework.jdbc.core.RowMapper;
import vuly.thesis.ecowash.core.payload.dto.ProductGroupDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductGroupDtoRowMapper implements RowMapper<ProductGroupDto> {

    @Override
    public ProductGroupDto mapRow(ResultSet rs, int i) throws SQLException {; //cast object
        return ProductGroupDto.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .note(rs.getString("note"))
                .productTypeName(rs.getString("product_type_name"))
                .dateCreated(rs.getTimestamp("date_created").toInstant())
                .active(rs.getString("active"))
                .build();
    }
}
