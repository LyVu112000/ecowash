package vuly.thesis.ecowash.core.repository.jdbc.DAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.payload.dto.ItemDeliveryDto;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.ItemDeliveryRowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemDeliveryDtoDAO {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ItemDeliveryDto> findByDeliveryReceiptId(Long deliveryReceiptId, String productItemName) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder = "SELECT pi.id as product_item_id, pi.code as product_item_code, pi.name as product_item_name, pt.name as piece_type_name, " +
                "                lf.name as laundry_form_name, lf.id as laundry_form_id, lf.value as laundry_form_value, di.delivery_receipt_id  " +
                "                FROM core.item_delivery di " +
                "                LEFT JOIN product_item pi ON pi.id = di.product_item_id " +
                "                LEFT JOIN piece_type pt ON pt.id = pi.piece_type_id " +
                "                LEFT JOIN laundry_form lf ON lf.id = di.laundry_form_id " +
               condition(deliveryReceiptId, productItemName, namedParameters) +
                "                group by pi.id, pi.code, pi.name, pt.name, lf.name, di.delivery_receipt_id, lf.id, lf.value " ;
        List<ItemDeliveryDto> listItemDeliveryDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new ItemDeliveryRowMapper());

        return listItemDeliveryDto;
    }

    private StringBuilder condition(Long deliveryReceiptId, String productItemName, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE di.delivery_receipt_id = :deliveryReceiptId ");
        namedParameters.put("deliveryReceiptId", deliveryReceiptId);

        if (productItemName != null) {
            conditionBuilder.append(" AND pi.name LIKE :productItemName ");
            namedParameters.put("productItemName", productItemName);
        }

        return conditionBuilder;
    }
}
