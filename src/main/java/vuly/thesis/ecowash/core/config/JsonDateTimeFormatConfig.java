package vuly.thesis.ecowash.core.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import vuly.thesis.ecowash.core.util.DateTimeUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Configuration
public class JsonDateTimeFormatConfig {

	public static final DateTimeFormatter FORMATTER_FAT_GUY = DateTimeFormatter.ofPattern(DateTimeUtil.JSON_DATETIME_PATTERN_FAT_GUY);
	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DateTimeUtil.JSON_DATETIME_PATTERN);
	public static final DateFormat DATE_FORMAT 		= new SimpleDateFormat(DateTimeUtil.JSON_DATETIME_PATTERN);

	@Bean("objectMapper")
	@Primary
	public ObjectMapper serializingObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateSerializer());
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateDeserializer());
		objectMapper.registerModule(javaTimeModule);
		return objectMapper;
	}

	public class LocalDateSerializer extends JsonSerializer<LocalDateTime> {

		@Override
		public void serialize(LocalDateTime value,
							  JsonGenerator gen,
							  SerializerProvider serializers) throws IOException {
			gen.writeString(value.format(FORMATTER));
		}
	}

	public class DateSerializer extends JsonSerializer<Date> {

		@Override
		public void serialize(Date value,
							  JsonGenerator gen,
							  SerializerProvider serializers) throws IOException {
			gen.writeString(DATE_FORMAT.format(value));

		}
	}

	public class LocalDateDeserializer extends JsonDeserializer<LocalDateTime> {

		@Override
		public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			return LocalDateTime.parse(p.getValueAsString(), FORMATTER);
		}
	}

	public class DateDeserializer extends JsonDeserializer<Date> {

		@SneakyThrows
		@Override
		public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			return DATE_FORMAT.parse(p.getValueAsString());
		}
	}
}
