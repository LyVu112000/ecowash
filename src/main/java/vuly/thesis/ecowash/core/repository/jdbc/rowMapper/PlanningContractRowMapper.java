package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.payload.dto.ContractDto;
import vuly.thesis.ecowash.core.payload.dto.CustomerDto;
import vuly.thesis.ecowash.core.payload.dto.PlanningContractDto;
import vuly.thesis.ecowash.core.payload.dto.ProductTypeDto;
import vuly.thesis.ecowash.core.util.MapperUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class PlanningContractRowMapper {

    private final MapperUtil mapperUtil;

    public PlanningContractDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PlanningContractDto.builder()
                .id(rs.getLong("id"))
                .timeReceived(rs.getString("time_received"))
                .timeDelivery(rs.getString("time_delivery"))
                .bagNumber(rs.getInt("bag_number"))
                .note(rs.getString("note"))
                .washingTeam(rs.getString("washing_team"))
                .contract(parseContract(rs))
                .customer(parseCustomer(rs))
                .productType(parseProductType(rs))
                .productTypeValue(rs.getString("product_type_value"))
                .build();
    }

    private ContractDto parseContract(ResultSet rs) throws SQLException {
        Long contractId = rs.getLong("contract_id");
        if (rs.wasNull())
            return null;

        return ContractDto.builder()
                .id(contractId)
                .code(rs.getString("contract_code"))
                .build();
    }

    private CustomerDto parseCustomer(ResultSet rs) throws SQLException {
        Long customerId = rs.getLong("customer_id");
        if (rs.wasNull())
            return null;

        return CustomerDto.builder()
                .id(customerId)
                .code(rs.getString("customer_code"))
                .fullName(rs.getString("customer_full_name"))
                .build();
    }

    private ProductTypeDto parseProductType(ResultSet rs) throws SQLException {
        Long productTypeId = rs.getLong("product_type_id");
        if (rs.wasNull())
            return null;

        return ProductTypeDto.builder()
                .id(productTypeId)
                .name(rs.getString("product_type_name"))
                .value(rs.getString("product_type_value"))
                .build();
    }
}
