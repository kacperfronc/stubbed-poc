package com.example.stubbed;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpMethod;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.StubHttpPredicates;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.stubbed.SerializationUtils.readValue;

//TODO factory?
final class InternalExchange {

    private final Collection<HttpMethod> methods;
    private final String path;
    private final String defaultResponseId;
    private final Collection<Response> responses;
    private final Map<String, Function<Request, Object>> placeholdersReplacers;
    private final BiFunction<Request, Response, Boolean> responseMatcher;

    private final StubHttpPredicates stubHttpPredicates;

    InternalExchange(Collection<HttpMethod> methods, String path, String defaultResponseId,
                     Collection<Response> responses, Map<String, Function<Request, Object>> placeholdersReplacers,
                     BiFunction<Request, Response, Boolean> responseMatcher) {
        this.methods = methods;
        this.path = path;
        this.defaultResponseId = defaultResponseId;
        this.responses = responses;
        this.placeholdersReplacers = placeholdersReplacers;
        this.responseMatcher = responseMatcher;

        this.stubHttpPredicates = new StubHttpPredicates();
    }

    static InternalExchange fromExchange(ObjectMapper objectMapper, String stubName, Exchange exchange) {

        getResponses(stubName, exchange, objectMapper);

        return new InternalExchange(exchange.getMethods(), exchange.getPath(), exchange.getDefaultResponseId(),
                getResponses(stubName, exchange, objectMapper), exchange.getPlaceholdersReplacers(),
                exchange.getResponseMatcher());
    }

    private static Collection<Response> getResponses(String stubName, Exchange exchange, ObjectMapper objectMapper) {
        if (exchange.isLoadResponsesFromClasspath()) {
            return Stream.concat(
                    ClasspathUtils.listResources(stubName, exchange.getName()).stream()
                            .map(paths -> readResponse(objectMapper, paths)),
                    exchange.getResponses().stream()
            ).collect(Collectors.toList());
        }
        return exchange.getResponses();
    }

    private static Response readResponse(ObjectMapper mapper, String path) {
        return SerializationUtils.readValue(mapper, ClasspathUtils.class.getClassLoader().getResource(path), Response.class);
    }

    Map<String, Function<Request, Object>> getPlaceholdersReplacers() {
        return placeholdersReplacers;
    }

    boolean matches(HttpServerRequest request) {
        return methods.stream()
                .anyMatch(it -> it.equals(request.method())) && stubHttpPredicates.test(path, request);
    }

    Map<String, String> extractPathVariables(HttpServerRequest request) {
        return stubHttpPredicates.apply(path, request);
    }

    Response getMatchingResponse(Request req) {

        List<Response> matchingResponses = responses.stream()
                .filter(it -> responseMatcher.apply(req, it))
                .collect(Collectors.toList());

        if (matchingResponses.isEmpty()) {
            return this.getDefaultResponse();
        } else if (matchingResponses.size() == 1) {
            return matchingResponses.get(0);
        } else {
            String matchingIds = matchingResponses.stream()
                    .map(Response::getId)
                    .collect(Collectors.joining(",", "[", "]"));
            throw new RuntimeException("More than 1 matching response, ids: " + matchingIds);
        }
    }

    private Response getDefaultResponse() {
        return responses.stream()
                .filter(it -> defaultResponseId.equals(it.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No matching default response for id: " + defaultResponseId));
    }

}
