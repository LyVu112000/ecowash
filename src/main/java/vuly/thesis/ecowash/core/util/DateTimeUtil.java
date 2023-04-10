package vuly.thesis.ecowash.core.util;

import lombok.extern.slf4j.Slf4j;
import vuly.thesis.ecowash.core.config.JsonDateTimeFormatConfig;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
@Slf4j
public class DateTimeUtil {
    public static final String JSON_DATETIME_PATTERN	    = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String JSON_DATETIME_PATTERN_FAT_GUY = "dd-MM-yyyy HH:mm";
    public static final String JSON_DATE_PATTERN		    = "yyyy-MM-dd";
    public static final DateTimeFormatter formatter         = DateTimeFormatter.ofPattern(JSON_DATE_PATTERN);
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(JSON_DATETIME_PATTERN);
    public static final String UTC_ZONE_ID                  = "UTC";
    public static final String DEFAULT_VN_ZONE_ID           = "Asia/Ho_Chi_Minh";
    public static final String ZONE_ID_HEADER               = "X-TZ-Offset";
    public static LocalDateTime parse(String stringOfDatetime) {
        if (stringOfDatetime != null && stringOfDatetime.length() > 12) {
            return LocalDateTime.parse(stringOfDatetime, formatter);
        }
        return LocalDate.parse(stringOfDatetime,formatter).atStartOfDay();
    }


    public static LocalDateTime endOfDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);

        return c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime startOfDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        return c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime getCurrentTime(){
        Calendar c = Calendar.getInstance();
        return c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static ZonedDateTime getCurrentSystemTime(){
        return ZonedDateTime.now(ZoneId.of(DEFAULT_VN_ZONE_ID));
    }

    public static boolean validZoneId(String zoneId){
        try {
            ZoneId.of(zoneId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static LocalDateTime parseDateTime(String dateTimeInString) {
        return LocalDateTime.parse(dateTimeInString, dateTimeFormatter);
    }

    public static LocalDateTime convertToUTC(String localDateTimeString, ZoneId fromZoneId) {
        return convertToUTC(LocalDateTime.parse(localDateTimeString, JsonDateTimeFormatConfig.FORMATTER), fromZoneId);
    }

    public static LocalDateTime convertToUTC(LocalDateTime localDateTime, ZoneId fromZoneId) {
        // set time from client to client time zone
        ZonedDateTime clientZonedDateTime = localDateTime.atZone(fromZoneId);
        // convert time to utc
        ZonedDateTime zonedDateTime = clientZonedDateTime.withZoneSameInstant(ZoneId.of(DateTimeUtil.UTC_ZONE_ID));
        return zonedDateTime.toLocalDateTime();
    }

    public static Instant convertToUTC (Instant instant, EbstUserRequest ebstUserRequest) {
        Integer zonedOffset = instant.atZone(ebstUserRequest.currentZoneId()).getOffset().getTotalSeconds();
        Instant converted = Instant.ofEpochSecond(instant.getEpochSecond() - zonedOffset);
        return converted;
    }

    public static String revertFromUTC (String string, ZoneId currentZoneId) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss.0")
                .withZone(ZoneId.of(UTC_ZONE_ID));
        Integer zonedOffset = Instant.now().atZone(currentZoneId).getOffset().getTotalSeconds();
        String converted = formatter.format(Instant.ofEpochSecond(Instant.from(formatter.parse(string)).getEpochSecond() + zonedOffset));
        return converted;
    }

    public static String formatToString(Instant instant) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

    public static String formatToString(LocalDate localDate) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withZone(ZoneId.systemDefault())
                .format(localDate);
    }

    public static String revertFromUTCNoS (String string, ZoneId currentZoneId) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss.0")
                .withZone(ZoneId.of(UTC_ZONE_ID));
        Integer zonedOffset = Instant.now().atZone(currentZoneId).getOffset().getTotalSeconds();
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(Instant.from(formatter.parse(string)).getEpochSecond() + zonedOffset));
    }
}
