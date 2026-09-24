package se.liu.ida.tdp024.account.util.http;

public interface HTTPHelper {
    String get(String endpoint, String... parameters);
    String postJSON(String endpoint, String[] queryParameters, String[] dataParameters);
}
// metoder för att skicka HTTP-anrop 
// Används i logiklagret 