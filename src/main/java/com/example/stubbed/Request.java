package com.example.stubbed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.example.stubbed.SerializationUtils.readValue;

public final class Request {

    private final ObjectMapper objectMapper;

    private final String body;
    private final Map<CharSequence, Set<Cookie>> cookies;
    private final Map<String, String> params;
    private final Map<String, String> pathVariables;
    private final HttpHeaders requestHeaders;
    private final String path;
    private final String scheme;
    private final String method;


    Request(ObjectMapper objectMapper, String body, Map<CharSequence, Set<Cookie>> cookies, Map<String, String> params,
            Map<String, String> pathVariables, HttpHeaders requestHeaders, String path, String scheme,
            String method) {
        this.objectMapper = objectMapper;
        this.body = body;
        this.cookies = cookies;
        this.params = params;
        this.pathVariables = pathVariables;
        this.requestHeaders = requestHeaders;
        this.path = path;
        this.scheme = scheme;
        this.method = method;
    }

    public <T> Optional<T> getBody(Class<T> clazz) {
        return body == null
                ? Optional.empty()
                : Optional.ofNullable(SerializationUtils.readValue(objectMapper, body, clazz));
    }

    public <T> Optional<T> getBody(TypeReference<T> typeReference) {
        return body == null
                ? Optional.empty()
                : Optional.ofNullable(SerializationUtils.readValue(objectMapper, body, typeReference));
    }

    public Map<CharSequence, Set<Cookie>> getCookies() {
        return cookies;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }

    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public String getPath() {
        return path;
    }

    public String getScheme() {
        return scheme;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "Request{" +
                "objectMapper=" + objectMapper +
                ", body='" + body + '\'' +
                ", cookies=" + cookies +
                ", params=" + params +
                ", pathVariables=" + pathVariables +
                ", requestHeaders=" + requestHeaders +
                ", path='" + path + '\'' +
                ", scheme='" + scheme + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
