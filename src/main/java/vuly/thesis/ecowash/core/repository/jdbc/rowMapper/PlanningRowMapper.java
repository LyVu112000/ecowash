package vuly.thesis.ecowash.core.repository.jdbc.rowMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vuly.thesis.ecowash.core.entity.type.PlanningStatus;
import vuly.thesis.ecowash.core.payload.dto.PlanningDto;
import vuly.thesis.ecowash.core.util.MapperUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class PlanningRowMapper {

    private final MapperUtil mapperUtil;

    public PlanningDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PlanningDto.builder()
                .id(rs.getLong("id"))
                .dateCreated(mapperUtil.mapInstant(rs.getTimestamp("date_created")))
                .createdBy(rs.getString("created_by"))
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .fromDate(mapperUtil.mapInstant(rs.getTimestamp("from_date")))
                .toDate(mapperUtil.mapInstant(rs.getTimestamp("to_date")))
                .status(mapperUtil.mapEnum(rs.getString("status"), PlanningStatus.class))
                .build();
    }
}
