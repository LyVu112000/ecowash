package vuly.thesis.ecowash.core.repository.jdbc.DAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.payload.dto.DeliveryReceiptDto;
import vuly.thesis.ecowash.core.payload.request.DeliveryReceiptSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.DeliveryReceiptDetailRowMapper;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.DeliveryReceiptDtoRowMapper;
import vuly.thesis.ecowash.core.util.DateTimeUtil;
import vuly.thesis.ecowash.core.util.EbstUserRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeliveryReceiptDAO {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DeliveryReceiptDetailRowMapper deliveryReceiptDetailRowMapper;
    private final DeliveryReceiptDtoRowMapper deliveryReceiptDtoRowMapper;
    private final EbstUserRequest ebstUserRequest;

    public Page<DeliveryReceiptDto> findAll(DeliveryReceiptSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder =
                "SELECT dr.id, dr.code, dr.date_created, dr.delivery_date, dr.status, dr.is_gen_by_debt, " +
                "   cus.full_name AS customerFullName, prt.name AS productTypeName, dr.is_express, " +
//                "   GROUP_CONCAT(si.name) AS specialInsValuesNames, " +
                "   subTb1.receivedCodes " +
                "FROM delivery_receipt dr " +
//                "   LEFT JOIN special_instruction_receipt sir ON sir.delivery_receipt_id = dr.id " +
//                "   LEFT JOIN special_instruction si ON si.id = sir.special_instruction_id " +
                "   LEFT JOIN product_type prt ON prt.id = dr.product_type_id " +
                "   LEFT JOIN customer cus ON cus.id = dr.customer_id " +
                "   LEFT JOIN (" +
                "       SELECT dlr.delivery_receipt_id, GROUP_CONCAT(rr.code SEPARATOR ', ') AS receivedCodes, GROUP_CONCAT(dlr.received_receipt_id) AS receivedIds " +
                "       FROM delivery_link_received dlr " +
                "       LEFT JOIN received_receipt rr ON rr.id = dlr.received_receipt_id " +
                "       GROUP BY dlr.delivery_receipt_id " +
                "   ) AS subTb1 ON subTb1.delivery_receipt_id = dr.id " +
                condition(request, namedParameters) +
                " GROUP BY dr.id, subTb1.receivedCodes " +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<DeliveryReceiptDto> listProductItemDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, deliveryReceiptDtoRowMapper::mapRow);

        return new PageImpl<>(listProductItemDto, pageable, this.count(request));
    }

    private StringBuilder condition(DeliveryReceiptSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE dr.id >= 0  ");

        conditionBuilder.append(" AND dr.is_flag_error = :isFlagError ");
        namedParameters.put("isFlagError", request.getIsFlagError());

        if (request.getCode() != null) {
            conditionBuilder.append(" AND dr.code LIKE :code ");
            namedParameters.put("code", "%" + request.getCode() + "%");
        }

        if (request.getCustomerCodeOrName() != null) {
            conditionBuilder.append(" AND ( cus.code LIKE :codeOrName or cus.full_name LIKE :codeOrName ) ");
            namedParameters.put("codeOrName", "%" + request.getCustomerCodeOrName() + "%");
        }

        if (request.getCustomerId() != null) {
            conditionBuilder.append(" AND dr.customer_id = :customerId ");
            namedParameters.put("customerId", request.getCustomerId());
        }

        if (request.getProductTypeValue() != null) {
            conditionBuilder.append(" AND prt.value = :productTypeValue ");
            namedParameters.put("productTypeValue", request.getProductTypeValue());
        }

        if (request.getIsRewash() != null) {
            conditionBuilder.append(" AND dr.is_rewash = :isRewash ");
            namedParameters.put("isRewash", request.getIsRewash());
        }

        if (request.getIsDebt() != null) {
            conditionBuilder.append(" AND dr.is_gen_by_debt = :isDebt ");
            namedParameters.put("isDebt", request.getIsDebt());
        }

        if (request.getStatuses() != null) {
            List<String> status = List.of(request.getStatuses().split(","));
            conditionBuilder.append(" AND dr.status IN (:status) ");
            namedParameters.put("status", status);
        } else {
            conditionBuilder.append(" AND dr.status != 'CANCEL' ");
        }

        if (request.getFromDate() != null && request.getToDate() != null) {
            conditionBuilder.append(" AND (dr.delivery_date BETWEEN :fromDate AND :toDate) ");
            namedParameters.put("fromDate", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest));
            namedParameters.put("toDate", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest));
        } else if (request.getFromDate() != null) {
            conditionBuilder.append(" AND dr.delivery_date >= :fromDate ");
            namedParameters.put("fromDate", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest));
        }

        if (request.getToDate() != null) {
            conditionBuilder.append(" AND dr.delivery_date <= :toDate ");
            namedParameters.put("toDate", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest));
        }

//        if(request.getSpecialInstruction() != null && !request.getSpecialInstruction().isEmpty()){
//            List<String> specialInstructions = List.of(request.getSpecialInstruction().split(","));
//            conditionBuilder.append(" AND dr.id IN (SELECT tb1.delivery_receipt_id as delivery_receipt_id " +
//                    "FROM special_instruction_receipt tb1, special_instruction tb2 " +
//                    "where tb2.id = tb1.special_instruction_id AND tb2.value IN ( :specialInstruction ))");
//            namedParameters.put("specialInstruction", specialInstructions);
//        }

        if(request.getReceivedId() != null){
            conditionBuilder.append(" AND subTb1.receivedIds LIKE :receivedReceiptId");
            namedParameters.put("receivedReceiptId", "%" + request.getReceivedId() + "%");
        }

        return conditionBuilder;
    }

    private String orders(Pageable pageable) {
        List<String> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            orders.add(" dr." + order.getProperty() + " " + order.getDirection());
        }
        return String.join(",", orders);
    }

    private int count(DeliveryReceiptSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder =
                "SELECT COUNT(*) " +
                "FROM delivery_receipt dr " +
                "LEFT JOIN customer cus ON cus.id = dr.customer_id " +
                "   LEFT JOIN product_type prt ON prt.id = dr.product_type_id " +
                "   LEFT JOIN (" +
                "       SELECT dlr.delivery_receipt_id, GROUP_CONCAT(dlr.received_receipt_id) AS receivedIds " +
                "       FROM delivery_link_received dlr " +
                "       LEFT JOIN received_receipt rr ON rr.id = dlr.received_receipt_id " +
                "       GROUP BY dlr.delivery_receipt_id " +
                "   ) AS subTb1 ON subTb1.delivery_receipt_id = dr.id " +
                condition(request, namedParameters);
        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }

    public DeliveryReceiptDto findById(Long id) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        String queryBuilder = "SELECT dr.id, dr.tenant_id, dr.code, dr.delivery_date, dr.bag_number, dr.number_of_loose_bags, " +
                "dr.weight, dr.number_room, dr.staff_check, dr.check_date, dr.note, dr.customer_id, dr.status, pt.value, dr.is_express " +
                "FROM delivery_receipt dr " +
                "LEFT JOIN product_type pt ON pt.id = dr.product_type_id " +
                "WHERE dr.id = :id";
        parameterSource.addValue("id", id);

        return namedParameterJdbcTemplate.queryForObject(
                queryBuilder,
                parameterSource,
                deliveryReceiptDetailRowMapper::mapRow
        );
    }
}
