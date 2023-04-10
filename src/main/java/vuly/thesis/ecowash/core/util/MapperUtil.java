package vuly.thesis.ecowash.core.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapperUtil {

    public Instant mapInstant(Timestamp timestamp) {
        return timestamp != null ? timestamp.toInstant() : null;
    }

    public <T extends Enum<T>> T mapEnum(String typeValue, Class<T> enumTypeClass) {
        if (StringUtil.isEmpty(typeValue)) {
            return null;
        }

        try {
            return Enum.valueOf(enumTypeClass, typeValue);
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("ENUM type " + enumTypeClass + " is not contain value " + typeValue);
        } catch (Exception ex) {
            log.info("ENUM casting error", ex);
        }
        return null;
    }
}
