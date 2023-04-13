package vuly.thesis.ecowash.core.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import vuly.thesis.ecowash.core.util.DateTimeUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class UserTimeZoneAwareDeserializer extends JsonDeserializer<LocalDateTime> {

    private String zoneIdStr;

    public UserTimeZoneAwareDeserializer(String str){
        if( zoneIdStr!= null && !zoneIdStr.isEmpty() && DateTimeUtil.validZoneId(zoneIdStr)) {
            zoneIdStr = str;
        }else{
            zoneIdStr = DateTimeUtil.DEFAULT_VN_ZONE_ID;
        }
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Grab the userTimeZone then convert from userTimeZone to UTC
        LocalDateTime ldt = LocalDateTime.parse(p.getValueAsString(), vuly.thesis.ecowash.core.config.JsonDateTimeFormatConfig.FORMATTER);
        // set time from client to client time zone
        ZonedDateTime clientZonedDateTime = ldt.atZone(ZoneId.of(this.zoneIdStr));
        // convert time to utc
        ZonedDateTime zonedDateTime = clientZonedDateTime.withZoneSameInstant(ZoneId.of(DateTimeUtil.UTC_ZONE_ID));
        return zonedDateTime.toLocalDateTime();
    }
}


