package vuly.thesis.ecowash.core.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import vuly.thesis.ecowash.core.util.DateTimeUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class UserTimeZoneAwareSerializer extends JsonSerializer<LocalDateTime> {

    private String zoneId;

    public UserTimeZoneAwareSerializer(String zoneId){
        if(DateTimeUtil.validZoneId(zoneId)){
            this.zoneId = zoneId;
        }else{
            this.zoneId = DateTimeUtil.DEFAULT_VN_ZONE_ID;
        }
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Grab the userTimeZone from the session then convert from UTC to userTimeZone

        // set value to UTC
        ZonedDateTime systemZonedDateTime = ZonedDateTime.of(value, ZoneId.of(DateTimeUtil.UTC_ZONE_ID));
        // convert value to client zoneId
        ZonedDateTime zdt = systemZonedDateTime.withZoneSameInstant(ZoneId.of(this.zoneId));
        gen.writeObject(zdt.format(vuly.thesis.ecowash.core.config.JsonDateTimeFormatConfig.FORMATTER));
    }
}