package se.liu.ida.tdp024.account.util.http;

import org.springframework.stereotype.Component;
import se.liu.ida.tdp024.account.util.logger.AccountLogger;
//import se.liu.ida.tdp024.account.util.logger.AccountLoggerImpl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

@Component
public class HTTPHelperImpl implements HTTPHelper {

    //private final AccountLogger accountLogger = new AccountLoggerImpl();

    @Override
    public String get(String endpoint, String... parameters) {
        String urlToRead = buildURL(endpoint, parameters);
        StringBuilder result = new StringBuilder();

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlToRead).openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result.toString();
    }

    @Override
    public String postJSON(String endpoint, String[] queryParameters, String[] dataParameters) {
        String urlToRead = buildURL(endpoint, queryParameters);
        String dataPayload = buildJSONPayload(dataParameters);

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlToRead).openConnection();
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setRequestMethod("POST");

            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "utf-8")) {
                writer.write(dataPayload);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream(), "utf-8"))
            ) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }

        return null;
    }

    // Helpers

    private String buildURL(String endpoint, String... parameters) {
        StringBuilder urlBuilder = new StringBuilder(endpoint);

        for (int i = 0; i < parameters.length - 1; i += 2) {
            try {
                String name = URLEncoder.encode(parameters[i], "UTF-8");
                String value = URLEncoder.encode(parameters[i + 1], "UTF-8");
                urlBuilder.append(i == 0 ? "?" : "&").append(name).append("=").append(value);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        return urlBuilder.toString();
    }

    private String buildJSONPayload(String[] dataParameters) {
        StringBuilder json = new StringBuilder("{");

        for (int i = 0; i < dataParameters.length - 1; i += 2) {
            String key = dataParameters[i];
            String value = dataParameters[i + 1];
            try {
                long numeric = Long.parseLong(value);
                json.append("\"").append(key).append("\":").append(numeric).append(",");
            } catch (NumberFormatException e) {
                json.append("\"").append(key).append("\":\"").append(value).append("\",");
            }
        }

        if (json.length() > 1) json.deleteCharAt(json.length() - 1);
        json.append("}");

        return json.toString().replaceAll("\\n", "\\\\n");
    }
}
