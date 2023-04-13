package vuly.thesis.ecowash.core.repository.jdbc.DAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.payload.dto.BriefDeliveryReceiptDto;
import vuly.thesis.ecowash.core.payload.request.BriefDeliveryReceiptSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.BriefDeliveryReceiptDtoRowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BriefDeliveryReceiptDtoDAO {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public List<BriefDeliveryReceiptDto> getBriefReceiptList(BriefDeliveryReceiptSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "SELECT dr.id, dr.code, dr.customer_id, pt.value as product_type_value, dr.status " +
                "FROM core.delivery_receipt dr " +
                "LEFT JOIN product_type pt ON pt.id = dr.product_type_id " +
                condition(request, namedParameters) +
                " ORDER BY dr.id desc ";

        List<BriefDeliveryReceiptDto> listBriefDeliveryReceiptDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new BriefDeliveryReceiptDtoRowMapper());

        return listBriefDeliveryReceiptDto;
    }
    private StringBuilder condition(BriefDeliveryReceiptSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE AND dr.is_flag_error = false AND dr.status = 'DONE' ");

        conditionBuilder.append(" AND dr.customer_id = :customerId ");
        namedParameters.put("customerId", request.getCustomerId());

        conditionBuilder.append(" AND pt.value = :productTypeValue ");
        namedParameters.put("productTypeValue", request.getProductTypeValue());

        if(request.getIds() != null) {
            conditionBuilder.append(" AND dr.id in (:ids) ");
            namedParameters.put("ids", request.getIds());
        }
        return conditionBuilder;
    }
}
