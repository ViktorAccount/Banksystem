package se.liu.ida.tdp024.account.util.json;

public interface AccountJsonSerializer {
    <T> T fromJson(String json, Class<T> clazz);
    String toJson(Object object);
}
