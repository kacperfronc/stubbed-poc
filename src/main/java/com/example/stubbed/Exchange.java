package com.example.stubbed;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.netty.handler.codec.http.HttpMethod.CONNECT;
import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.HEAD;
import static io.netty.handler.codec.http.HttpMethod.OPTIONS;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpMethod.TRACE;

public final class Exchange {

    private final String name;
    private final Collection<HttpMethod> methods;
    private final String path;
    private final String defaultResponseId;
    private final Collection<Response> responses;
    private final Map<String, Function<Request, Object>> placeholdersReplacers;
    private final BiFunction<Request, Response, Boolean> responseMatcher;
    private final boolean loadResponsesFromClasspath;

    private Exchange(String name, Collection<HttpMethod> methods, String path, String defaultResponseId,
                     Collection<Response> responses, Map<String, Function<Request, Object>> placeholdersReplacers,
                     BiFunction<Request, Response, Boolean> responseMatcher, boolean loadResponsesFromClasspath) {
        this.name = name;
        this.methods = methods;
        this.path = path;
        this.defaultResponseId = defaultResponseId;
        this.responses = responses;
        this.placeholdersReplacers = placeholdersReplacers;
        this.responseMatcher = responseMatcher;
        this.loadResponsesFromClasspath = loadResponsesFromClasspath;
    }

    String getName() {
        return name;
    }

    Collection<HttpMethod> getMethods() {
        return methods;
    }

    String getPath() {
        return path;
    }

    String getDefaultResponseId() {
        return defaultResponseId;
    }

    Collection<Response> getResponses() {
        return responses;
    }

    Map<String, Function<Request, Object>> getPlaceholdersReplacers() {
        return placeholdersReplacers;
    }

    BiFunction<Request, Response, Boolean> getResponseMatcher() {
        return responseMatcher;
    }

    boolean isLoadResponsesFromClasspath() {
        return loadResponsesFromClasspath;
    }

    public static ExchangeBuilder builder(String name) {
        return new ExchangeBuilder(name);
    }

    public static final class ExchangeBuilder {

        private final String name;
        private HttpMethod[] methods = new HttpMethod[]{GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH};
        private String path = "";
        private String defaultResponseId = "default";
        private boolean loadResponsesFromClasspath = true;
        private Collection<Response> inlineResponses = Collections.emptyList();
        private Map<String, Function<Request, Object>> placeholdersReplacers = Collections.emptyMap();
        private BiFunction<Request, Response, Boolean> responseMatcher = (req, res) -> true;

        private ExchangeBuilder(String name) {
            this.name = name;
        }

        public ExchangeBuilder methods(HttpMethod... methods) {
            this.methods = methods;
            return this;
        }

        public ExchangeBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ExchangeBuilder defaultResponseId(String defaultResponseId) {
            this.defaultResponseId = defaultResponseId;
            return this;
        }

        public ExchangeBuilder loadResponsesFromClasspath(boolean loadResponsesFromClasspath) {
            this.loadResponsesFromClasspath = loadResponsesFromClasspath;
            return this;
        }

        public ExchangeBuilder inlineResponses(Collection<Response> inlineResponses) {
            this.inlineResponses = inlineResponses;
            return this;
        }

        public ExchangeBuilder placeholdersReplacers(Map<String, Function<Request, Object>> placeholdersReplacers) {
            this.placeholdersReplacers = placeholdersReplacers;
            return this;
        }

        public ExchangeBuilder responseMatcher(BiFunction<Request, Response, Boolean> responseMatcher) {
            this.responseMatcher = responseMatcher;
            return this;
        }

        public Exchange build() {
            return new Exchange(name, Arrays.asList(methods), path, defaultResponseId, inlineResponses,
                    placeholdersReplacers, responseMatcher, loadResponsesFromClasspath);
        }
    }

}
