package vuly.thesis.ecowash.core.repository.jdbc.DAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import vuly.thesis.ecowash.core.payload.dto.ReceivedReceiptDto;
import vuly.thesis.ecowash.core.payload.request.ReceivedReceiptSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.ReceivedReceiptDtoRowMapper;
import vuly.thesis.ecowash.core.util.DateTimeUtil;
import vuly.thesis.ecowash.core.util.EbstUserRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReceivedReceiptDtoDAO {
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    EbstUserRequest ebstUserRequest;

    public Page<ReceivedReceiptDto> findAll(ReceivedReceiptSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder = "SELECT rr.id, rr.code, cus.full_name, prt.name as product_type_name, rr.reference_code, " +
                "rr.date_created, rr.received_date as received_date, rr.is_rewash, rr.status, GROUP_CONCAT(si.value) value, " +
                "GROUP_CONCAT(si.name) name, t2.deliveryCode " +
                "FROM received_receipt rr " +
                "LEFT JOIN special_instruction_receipt sir ON sir.received_receipt_id = rr.id " +
                "LEFT JOIN product_type prt ON prt.id = rr.product_type_id " +
                "LEFT JOIN customer cus ON cus.id = rr.customer_id " +
                "LEFT JOIN special_instruction si ON si.id = sir.special_instruction_id " +
                "LEFT JOIN (select rr.id, GROUP_CONCAT(dr.code) as deliveryCode " +
                "                from received_receipt rr " +
                "                LEFT JOIN received_link_delivery t1 ON t1.received_receipt_id = rr.id " +
                "                LEFT JOIN delivery_receipt dr ON dr.id = t1.delivery_receipt_id " +
                "                group by rr.id) t2 on t2.id = rr.id " +
                condition(request, namedParameters) +
                " GROUP BY rr.id, rr.code, cus.full_name, prt.name, rr.date_created, rr.received_date, rr.is_rewash "+
//                " ORDER BY FIELD(rr.status, 'DONE'), FIELD(rr.is_express, true) desc, rr.id desc  " +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        log.info("QUERY "+ queryBuilder);
        List<ReceivedReceiptDto> listProductItemDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters, new ReceivedReceiptDtoRowMapper());

        return new PageImpl<>(listProductItemDto, pageable, this.count(request));
    }

    private StringBuilder condition(ReceivedReceiptSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE rr.status NOT IN ('WAITING_DELIVERY', 'DELIVERY', 'RELEASE', 'CUSTOMER_RECEIVED') ");

        if (request.getCode() != null) {
            conditionBuilder.append(" AND rr.code LIKE :code ");
            namedParameters.put("code", "%" + request.getCode() + "%");
        }

        if (request.getIsFlagError() != null) {
            conditionBuilder.append(" AND rr.is_flag_error = :isFlagError ");
            namedParameters.put("isFlagError", request.getIsFlagError());
        }

        if (request.getCustomerCodeOrName() != null) {
            conditionBuilder.append(" AND ( cus.code LIKE :codeOrName or cus.full_name LIKE :codeOrName ) ");
            namedParameters.put("codeOrName", "%" + request.getCustomerCodeOrName() + "%");
        }


        if (request.getCustomerId() != null) {
            conditionBuilder.append(" AND rr.customer_id = :customerId ");
            namedParameters.put("customerId", request.getCustomerId());
        }

        if (request.getProductTypeValue() != null ) {
            conditionBuilder.append(" AND prt.value = :productTypeValue");
            namedParameters.put("productTypeValue", request.getProductTypeValue());
        }

        if (request.getIsRewash() != null) {
            conditionBuilder.append(" AND rr.is_rewash = :isRewash");
            namedParameters.put("isRewash", request.getIsRewash());
        }

        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
//            List<String> status = new ArrayList<String>(Arrays.asList(request.getStatus().split(",")));
//            StringBuilder stringBuilder= new StringBuilder();
//            for(String item: status){
//                stringBuilder.append("\"").append(item).append("\",");
//            }
            List<String> status = List.of(request.getStatus().split(", "));
            conditionBuilder.append(" AND rr.status IN ( :status)");
            namedParameters.put("status", status);
        } else {
            conditionBuilder.append(" AND rr.status != 'CANCEL' ");
        }

        if (request.getFromDate() != null && request.getToDate() != null) {
            conditionBuilder.append(" AND (rr.received_date BETWEEN :fromDate AND :toDate) ");
            namedParameters.put("fromDate", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest));
            namedParameters.put("toDate", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest));
        } else if (request.getFromDate() != null) {
            conditionBuilder.append(" AND rr.received_date >= :fromDate ");
            namedParameters.put("fromDate", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest));
        }
        if (request.getToDate() != null) {
            conditionBuilder.append(" AND rr.received_date <= :toDate ");
            namedParameters.put("toDate", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest));
        }

        if(request.getSpecialInstruction() != null && !request.getSpecialInstruction().isEmpty()){
            List<String> specialInstructions = List.of(request.getSpecialInstruction().split(","));
            conditionBuilder.append(" AND rr.id IN (SELECT tb1.received_receipt_id as received_receipt_id " +
                    "FROM special_instruction_receipt tb1, special_instruction tb2 " +
                    "where tb2.id = tb1.special_instruction_id AND tb2.value IN (:specialInstructions))");
            namedParameters.put("specialInstructions", specialInstructions);
        }

        if(request.getDeliveryId() != null){
            conditionBuilder.append(" AND t1.delivery_receipt_id = :deliveryReceiptId");
            namedParameters.put("deliveryReceiptId", request.getDeliveryId());
        }

        return conditionBuilder;
    }

    private String orders(Pageable pageable) {
        List<String> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            orders.add(" rr." + order.getProperty() + " " + order.getDirection());
        }
        return String.join(",", orders);
    }

    private int count(ReceivedReceiptSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder =
                "SELECT COUNT(*) FROM( " +
                        "SELECT DISTINCT (rr.id) " +
                        "                FROM received_receipt rr " +
                        "                LEFT JOIN special_instruction_receipt sir ON sir.received_receipt_id = rr.id " +
                        "                LEFT JOIN product_type prt ON prt.id = rr.product_type_id " +
                        "                LEFT JOIN customer cus ON cus.id = rr.customer_id " +
                        "                LEFT JOIN special_instruction si ON si.id = sir.special_instruction_id " +
                        "                LEFT JOIN received_link_delivery t1 ON t1.received_receipt_id = rr.id " +
                        condition(request, namedParameters) +
                        " GROUP BY rr.id, rr.code, cus.full_name, prt.name, rr.date_created, rr.received_date, rr.is_rewash) " +
                        " AS COUNT ";
        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }
}
