package vuly.thesis.ecowash.core.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import vuly.thesis.ecowash.core.util.DateTimeUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class UserInstantTimeZoneAwareDeserializer extends JsonDeserializer<Instant> {

    private String zoneIdStr;

    public UserInstantTimeZoneAwareDeserializer(String str){
        if( zoneIdStr!= null && !zoneIdStr.isEmpty() && DateTimeUtil.validZoneId(zoneIdStr)) {
            zoneIdStr = str;
        }else{
            zoneIdStr = DateTimeUtil.DEFAULT_VN_ZONE_ID;
        }
    }

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        // Grab the userTimeZone then convert from userTimeZone to UTC
        LocalDateTime ldt = LocalDateTime.parse(jsonParser.getValueAsString(), JsonDateTimeFormatConfig.FORMATTER);
        // set time from client to client time zone
        ZonedDateTime clientZonedDateTime = ldt.atZone(ZoneId.of(this.zoneIdStr));
        // convert time to utc
        ZonedDateTime zonedDateTime = clientZonedDateTime.withZoneSameInstant(ZoneId.of(DateTimeUtil.UTC_ZONE_ID));
        return zonedDateTime.toInstant();
    }
}


