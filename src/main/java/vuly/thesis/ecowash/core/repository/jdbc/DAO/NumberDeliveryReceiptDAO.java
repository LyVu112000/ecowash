package vuly.thesis.ecowash.core.repository.jdbc.DAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.payload.dto.NumberDeliveryReceiptDto;
import vuly.thesis.ecowash.core.payload.request.NumberDeliveryReceiptSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.NumberDeliveryReceiptDtoRowMapper;
import vuly.thesis.ecowash.core.util.EbstUserRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class NumberDeliveryReceiptDAO {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final EbstUserRequest ebstUserRequest;

    public Page<NumberDeliveryReceiptDto> findAll(NumberDeliveryReceiptSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "" +
                "SELECT itr.id, itr.received_receipt_id, itr.number_received, " +
                "       itr.random_number_check, itr.number_after_production, itd.note, " +
                "       itr.product_item_id, pi.code pCode, pi.name pName, rr.code receivedCode, rr.number_room, " +
                "       pi.piece_type_id, pt.value ppValue, pt.name ppName, " +
                "       itr.laundry_form_id, lf.value ldValue, lf.name ldName, " +
                "       itd.number_delivery numberDelivery, itd.number_delivery_actual numberDeliveryActual, " +
                "       (select SUM(if(dr.status = 'DONE', itd.number_delivery_actual, itd.number_delivery))" +
                "       from item_received ir " +
                "       left join item_delivery itd on itd.received_receipt_id = ir.received_receipt_id " +
                "                                   and itd.product_item_id = ir.product_item_id " +
                "       left join delivery_receipt dr on dr.id = itd.delivery_receipt_id " +
                "       where ir.received_receipt_id = " + request.getReceiptId() +
                "       and ir.id in (itr.id) and dr.status != 'CANCEL' " +
                "       group by ir.id) as total_delivery " +
                "FROM item_received itr " +
                "LEFT JOIN item_delivery itd ON itd.product_item_id = itr.product_item_id " +
                "LEFT JOIN product_item pi on pi.id = itr.product_item_id " +
                "LEFT JOIN laundry_form lf on lf.id = itr.laundry_form_id " +
                "LEFT JOIN piece_type pt on pt.id = pi.piece_type_id " +
                "LEFT JOIN received_receipt rr on rr.id = itr.received_receipt_id " +
                condition(request, namedParameters) +
                " GROUP BY itr.id, itd.number_delivery, itd.number_delivery_actual, itd.note " +
                " ORDER BY itr.received_receipt_id " +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<NumberDeliveryReceiptDto> resultList = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new NumberDeliveryReceiptDtoRowMapper());

        return new PageImpl<>(resultList, pageable, resultList.size());
    }

    private StringBuilder condition(NumberDeliveryReceiptSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE itr.>= 0= ");

        if (request.getReceiptId() != null) {
            conditionBuilder
                    .append(" AND itr.received_receipt_id = :receiptId ");
            namedParameters.put("receiptId", request.getReceiptId());
        }

        if (request.getDeliveryId() != null) {
            conditionBuilder.append(" AND itd.delivery_receipt_id = :deliveryId ");
            namedParameters.put("deliveryId", request.getDeliveryId());
        }

        return conditionBuilder;
    }

    public Page<NumberDeliveryReceiptDto> findAllNotDeliveryReceiptId(NumberDeliveryReceiptSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "" +
                "SELECT itr.id, itr.received_receipt_id, itr.number_received, " +
                "       itr.random_number_check, itr.number_after_production, itr.note, " +
                "       itr.product_item_id, pi.code pCode, pi.name pName, rr.code receivedCode, rr.number_room, " +
                "       pi.piece_type_id, pt.value ppValue, pt.name ppName, " +
                "       itr.laundry_form_id, lf.value ldValue, lf.name ldName, " +
                "       (select SUM(if(dr.status = 'DONE', itd.number_delivery_actual, itd.number_delivery))" +
                "       from item_received ir " +
                "       left join item_delivery itd on itd.received_receipt_id = ir.received_receipt_id " +
                "                                   and itd.product_item_id = ir.product_item_id " +
                "       left join delivery_receipt dr on dr.id = itd.delivery_receipt_id " +
                "       where ir.received_receipt_id = " + request.getReceiptId() +
                "       and ir.id in (itr.id) and dr.status != 'CANCEL' " +
                "       group by ir.id) as total_delivery " +
                "FROM item_received itr " +
                "LEFT JOIN product_item pi on pi.id = itr.product_item_id " +
                "LEFT JOIN laundry_form lf on lf.id = itr.laundry_form_id " +
                "LEFT JOIN piece_type pt on pt.id = pi.piece_type_id " +
                "LEFT JOIN received_receipt rr on rr.id = itr.received_receipt_id " +
                condition(request, namedParameters) +
                " GROUP BY itr.id " +
                " ORDER BY itr.received_receipt_id " +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<NumberDeliveryReceiptDto> resultList = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new NumberDeliveryReceiptDtoRowMapper());

        return new PageImpl<>(resultList, pageable, resultList.size());
    }
}
