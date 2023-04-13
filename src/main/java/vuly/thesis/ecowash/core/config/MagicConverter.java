package vuly.thesis.ecowash.core.config;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;
import org.springframework.util.TypeUtils;
import vuly.thesis.ecowash.core.util.DateTimeUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class MagicConverter extends MappingJackson2HttpMessageConverter {

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(clazz, (Class)null);
        return this.readJavaType(javaType, inputMessage, getObjectMapperWithZoneId(inputMessage));
    }

    @Override
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {


        JavaType javaType = super.getJavaType(type, contextClass);
        return this.readJavaType( javaType,inputMessage, getObjectMapperWithZoneId(inputMessage));
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage, ObjectMapper objectMapper) throws IOException {
        try {
            if (inputMessage instanceof MappingJacksonInputMessage) {
                Class<?> deserializationView = ((MappingJacksonInputMessage)inputMessage).getDeserializationView();
                if (deserializationView != null) {
                    return objectMapper.readerWithView(deserializationView).forType(javaType).readValue(inputMessage.getBody());
                }
            }

            return objectMapper.readValue(inputMessage.getBody(), javaType);
        } catch (InvalidDefinitionException var4) {
            throw new HttpMessageConversionException("Type definition error: " + var4.getType(), var4);
        } catch (JsonProcessingException var5) {
            throw new HttpMessageNotReadableException("JSON parse error: " + var5.getOriginalMessage(), var5);
        }
    }

    @Override
    protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        String zoneId = "";
        List<String> timeZoneHeader = outputMessage.getHeaders().get(DateTimeUtil.ZONE_ID_HEADER);
        if(timeZoneHeader != null && timeZoneHeader.size() > 0 && DateTimeUtil.validZoneId(timeZoneHeader.get(0))){
            zoneId = timeZoneHeader.get(0);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new vuly.thesis.ecowash.core.config.UserTimeZoneAwareDeserializer(zoneId));
        javaTimeModule.addSerializer(LocalDateTime.class, new vuly.thesis.ecowash.core.config.UserTimeZoneAwareSerializer(zoneId));
        javaTimeModule.addDeserializer(Instant.class, new vuly.thesis.ecowash.core.config.UserInstantTimeZoneAwareDeserializer(zoneId));
        javaTimeModule.addSerializer(Instant.class, new vuly.thesis.ecowash.core.config.UserInstantTimeZoneAwareSerializer(zoneId));
        objectMapper.registerModule(javaTimeModule);

        MediaType contentType = outputMessage.getHeaders().getContentType();
        JsonEncoding encoding = this.getJsonEncoding(contentType);
        JsonGenerator generator = objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);

        try {
            this.writePrefix(generator, object);
            Object value = object;
            Class<?> serializationView = null;
            FilterProvider filters = null;
            JavaType javaType = null;
            if (object instanceof MappingJacksonValue) {
                MappingJacksonValue container = (MappingJacksonValue)object;
                value = container.getValue();
                serializationView = container.getSerializationView();
                filters = container.getFilters();
            }

            if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
                javaType = this.getJavaType(type, (Class)null);
            }

            ObjectWriter objectWriter = serializationView != null ? objectMapper.writerWithView(serializationView) : objectMapper.writer();
            if (filters != null) {
                objectWriter = objectWriter.with(filters);
            }

            if (javaType != null && javaType.isContainerType()) {
                objectWriter = objectWriter.forType(javaType);
            }

            SerializationConfig config = objectWriter.getConfig();
            if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) && config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
                prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
                objectWriter = objectWriter.with(prettyPrinter);
            }

            objectWriter.writeValue(generator, value);
            this.writeSuffix(generator, object);
            generator.flush();
        } catch (InvalidDefinitionException var13) {
            throw new HttpMessageConversionException("Type definition error: " + var13.getType(), var13);
        } catch (JsonProcessingException var14) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + var14.getOriginalMessage(), var14);
        }
    }

    private ObjectMapper getObjectMapperWithZoneId(HttpInputMessage inputMessage){
        String zoneId = DateTimeUtil.DEFAULT_VN_ZONE_ID;
        List<String> timeZoneHeader = inputMessage.getHeaders().get(DateTimeUtil.ZONE_ID_HEADER);
        if(timeZoneHeader != null && timeZoneHeader.size() > 0 && DateTimeUtil.validZoneId(timeZoneHeader.get(0))){
            zoneId = timeZoneHeader.get(0);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new vuly.thesis.ecowash.core.config.UserTimeZoneAwareDeserializer(zoneId));
        javaTimeModule.addSerializer(LocalDateTime.class, new vuly.thesis.ecowash.core.config.UserTimeZoneAwareSerializer(zoneId));
        javaTimeModule.addDeserializer(Instant.class, new vuly.thesis.ecowash.core.config.UserInstantTimeZoneAwareDeserializer(zoneId));
        javaTimeModule.addSerializer(Instant.class, new vuly.thesis.ecowash.core.config.UserInstantTimeZoneAwareSerializer(zoneId));
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

}
