package vuly.thesis.ecowash.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;

@Service
@Slf4j
public class EbstUserRequest {

	@Autowired
	HttpServletRequest request;

	public ZoneId currentZoneId() {
		// Only access servlet request if inside a request context instead of job
		if (RequestContextHolder.getRequestAttributes() != null) {
			String zoneId = DateTimeUtil.DEFAULT_VN_ZONE_ID;
			String timeZoneHeader = request.getHeader(DateTimeUtil.ZONE_ID_HEADER);
			if(!StringUtil.isEmpty(timeZoneHeader) && DateTimeUtil.validZoneId(timeZoneHeader)){
				zoneId = timeZoneHeader;
			}
			return ZoneId.of(zoneId);
		} else {
			return ZoneId.systemDefault();
		}
	}
}
