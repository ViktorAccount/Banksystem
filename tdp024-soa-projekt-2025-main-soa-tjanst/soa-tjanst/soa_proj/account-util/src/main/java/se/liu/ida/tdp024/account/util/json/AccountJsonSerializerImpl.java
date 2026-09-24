package se.liu.ida.tdp024.account.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Handles JSON de serialization for Account-related entities.
 
 */
public class AccountJsonSerializerImpl implements AccountJsonSerializer {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final ObjectMapper jsonMapper;

    public AccountJsonSerializerImpl() {
        jsonMapper = new ObjectMapper();
        jsonMapper.setDateFormat(new SimpleDateFormat(DATE_PATTERN));
    }

    @Override
    public String toJson(Object obj) {
        try {
            return jsonMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            System.err.println("JSON serialization failed: " + e.getMessage());
            return "{}";
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> type) {
        try {
            return jsonMapper.readValue(json, type);
        } catch (IOException e) {
            System.err.println("JSON parsing failed: " + e.getMessage());
            return null;
        }
    }
}
