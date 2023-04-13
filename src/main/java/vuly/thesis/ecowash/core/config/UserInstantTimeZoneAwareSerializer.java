package vuly.thesis.ecowash.core.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import vuly.thesis.ecowash.core.util.DateTimeUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class UserInstantTimeZoneAwareSerializer extends JsonSerializer<Instant> {

    private String zoneId;

    public UserInstantTimeZoneAwareSerializer(String zoneId){
        if(DateTimeUtil.validZoneId(zoneId)){
            this.zoneId = zoneId;
        }else{
            this.zoneId = DateTimeUtil.DEFAULT_VN_ZONE_ID;
        }
    }

    @Override
    public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        ZonedDateTime systemZonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of(DateTimeUtil.UTC_ZONE_ID));
        ZonedDateTime zdt = systemZonedDateTime.withZoneSameInstant(ZoneId.of(this.zoneId));
        jsonGenerator.writeObject(zdt.format(JsonDateTimeFormatConfig.FORMATTER_FAT_GUY));
    }
}