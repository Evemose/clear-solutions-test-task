package org.users.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class DateTimeSerializationConfig {
    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        var timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());
        timeModule.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
        timeModule.addSerializer(LocalDate.class, new CustomLocalDateSerializer());
        timeModule.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer());
        objectMapper.registerModule(timeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    private static class CustomLocalDateTimeSerializer extends LocalDateTimeSerializer {
        public CustomLocalDateTimeSerializer() {
            super(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    private static class CustomLocalDateTimeDeserializer extends LocalDateTimeDeserializer {
        public CustomLocalDateTimeDeserializer() {
            super(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    private static class CustomLocalDateSerializer extends LocalDateSerializer {
        public CustomLocalDateSerializer() {
            super(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    private static class CustomLocalDateDeserializer extends LocalDateDeserializer {
        public CustomLocalDateDeserializer() {
            super(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
}
