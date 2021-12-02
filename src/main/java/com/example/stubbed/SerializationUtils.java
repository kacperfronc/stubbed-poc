package com.example.stubbed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

final class SerializationUtils {
    private SerializationUtils() {
    }

    static <T> T readValue(ObjectMapper mapper, URL resource, Class<T> clazz) {
        try {
            return mapper.readValue(resource, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T readValue(ObjectMapper mapper, String param, Class<T> clazz) {
        try {
            return mapper.readValue(param, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T readValue(ObjectMapper mapper, String param, TypeReference<T> typeRef) {
        try {
            return mapper.readValue(param, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static String writeAsString(ObjectMapper mapper, Object param) {
        try {
            return mapper.writeValueAsString(param);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
