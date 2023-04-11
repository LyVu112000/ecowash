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
import vuly.thesis.ecowash.core.payload.dto.ReceiptSummaryListDto;
import vuly.thesis.ecowash.core.payload.request.DeliveryReceiptSearchRequest;
import vuly.thesis.ecowash.core.repository.jdbc.rowMapper.ReceiptSummaryListDtoRowMapper;
import vuly.thesis.ecowash.core.util.DateTimeUtil;
import vuly.thesis.ecowash.core.util.EbstUserRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SummaryReceiptDAO {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final EbstUserRequest ebstUserRequest;
    public Page<ReceiptSummaryListDto> findAll(DeliveryReceiptSearchRequest request, Pageable pageable) {
        Map<String, Object> namedParameters = new HashMap<>();
        String queryBuilder =
                "SELECT rr.id, rr.code, rr.status, c.full_name customerName, pt.name productType, " +
                        "sum(ir.number_received) totalReceived, sum(ir.random_number_check) totalRandomCheck, " +
                        "sum(ir.number_after_production) totalAfterProduction, " +
                        "if(abc.totalDelivery is not null, abc.totalDelivery, 0) totalDelivery, abc.deliveryCode " +
                        "FROM core.received_receipt rr " +
                        "left join customer c on c.id = rr.customer_id " +
                        "left join product_type pt on pt.id = rr.product_type_id " +
                        "left join item_received ir on ir.received_receipt_id = rr.id " +
                        "left join (select rr.id, SUM(itd.number_delivery) totalDelivery, group_concat(dr.code) deliveryCode " +
                        "from received_receipt rr " +
                        "left join item_delivery itd on rr.id = itd.received_receipt_id " +
                        "left join delivery_receipt dr on dr.id = itd.delivery_receipt_id " +
                        "where itd.received_receipt_id in (rr.id)  " +
                        "group by rr.id) abc on rr.id = abc.id " +
                condition(request, namedParameters) +
                " group by rr.id, rr.code, c.full_name, pt.name " +
                " ORDER BY " + orders(pageable) +
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<ReceiptSummaryListDto> listReceiptSummaryDto = namedParameterJdbcTemplate.query(
                queryBuilder, namedParameters,new ReceiptSummaryListDtoRowMapper());

        return new PageImpl<>(listReceiptSummaryDto, pageable, this.count(request));
    }

    private StringBuilder condition(DeliveryReceiptSearchRequest request, Map<String, Object> namedParameters) {
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append(" WHERE rr.id >= 0  ");


        if (request.getCode() != null) {
            conditionBuilder.append(" AND rr.code LIKE :code or abc.deliveryCode like :code ");
            namedParameters.put("code", "%" + request.getCode() + "%");
        }

        if (request.getCustomerId() != null) {
            conditionBuilder.append(" AND rr.customer_id = :customerId ");
            namedParameters.put("customerId", request.getCustomerId());
        }

        if (request.getProductTypeValue() != null) {
            conditionBuilder.append(" AND pt.value = :productTypeValue ");
            namedParameters.put("productTypeValue", request.getProductTypeValue());
        }


        if (request.getStatuses() != null) {
            List<String> status = List.of(request.getStatuses().split(","));
            conditionBuilder.append(" AND rr.status IN (:status) ");
            namedParameters.put("status", status);
        }

        if (request.getFromDate() != null && request.getToDate() != null) {
            conditionBuilder.append(" AND (rr.delivery_date BETWEEN :fromDate AND :toDate) ");
            namedParameters.put("fromDate", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest));
            namedParameters.put("toDate", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest));
        } else if (request.getFromDate() != null) {
            conditionBuilder.append(" AND rr.delivery_date >= :fromDate ");
            namedParameters.put("fromDate", DateTimeUtil.convertToUTC(request.getFromDate(), ebstUserRequest));
        }

        if (request.getToDate() != null) {
            conditionBuilder.append(" AND rr.delivery_date <= :toDate ");
            namedParameters.put("toDate", DateTimeUtil.convertToUTC(request.getToDate(), ebstUserRequest));
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

    private int count(DeliveryReceiptSearchRequest request) {
        Map<String, Object> namedParameters = new HashMap<>();

        String queryBuilder =
                "FROM core.received_receipt rr " +
                        "left join customer c on c.id = rr.customer_id" +
                        "left join product_type pt on pt.id = rr.product_type_id " +
                        "left join item_received ir on ir.received_receipt_id = rr.id " +
                        "left join (select rr.id, SUM(itd.number_delivery) totalDelivery, group_concat(dr.code) deliveryCode " +
                        "from received_receipt rr" +
                        "left join item_delivery itd on rr.id = itd.received_receipt_id " +
                        "left join delivery_receipt dr on dr.id = itd.delivery_receipt_id " +
                        "where itd.received_receipt_id in (rr.id)  " +
                condition(request, namedParameters);
        return namedParameterJdbcTemplate.queryForObject(queryBuilder, namedParameters, Integer.class);
    }
}
