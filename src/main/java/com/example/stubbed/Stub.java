package com.example.stubbed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.resources.LoopResources;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static com.example.stubbed.SerializationUtils.readValue;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class Stub {

    private static final Logger log = LoggerFactory.getLogger(Stub.class);

    private static final LoopResources LOOP_RESOURCES = LoopResources.create("event-loop", 1, false);

    private final String name;
    private final ObjectMapper mapper;
    private final HttpServer stub;
    private DisposableServer server;

    private Stub(String name, Collection<Exchange> exchanges, ObjectMapper mapper, int port, Function<HttpServer, HttpServer> mutation) {
        this.name = name;
        this.mapper = mapper;
        this.stub = mutation.apply(HttpServer.create()
                .runOn(LOOP_RESOURCES)
                .port(port))
                .route(routes -> exchanges.stream()
                        .map(exchange -> InternalExchange.fromExchange(mapper, name, exchange))
                        .forEach(exchange ->
                                routes.route(exchange::matches, (req, res) -> handleRequest(exchange, req, res))));
    }

    public Stub start() {
        server = stub.bindNow();
        log.info("Started stub {} on port: {}", name, server.port());
        return this;
    }

    public void stop() {
        log.info("Stopping stub {}", name);
        if (server != null) {
            server.disposeNow();
        }
    }

    private Mono<Void> handleRequest(InternalExchange exchange, HttpServerRequest req, HttpServerResponse res) {
        return extractRequest(exchange, req)
                .flatMap(request -> {
                    Response matchingResponse = exchange.getMatchingResponse(request);
                    String body = matchingResponse.getBody() == null ? null : SerializationUtils.writeAsString(mapper, matchingResponse.getBody());
                    String headers = SerializationUtils.writeAsString(mapper, matchingResponse.getHeaders());

                    for (Map.Entry<String, Function<Request, Object>> entry : exchange.getPlaceholdersReplacers().entrySet()) {
                        String placeholder = entry.getKey();
                        Object replacement = entry.getValue().apply(request);
                        body = replace(body, placeholder, replacement);
                        headers = replace(headers, placeholder, replacement);
                    }

                    SerializationUtils.readValue(mapper, headers, new TypeReference<Map<CharSequence, CharSequence>>() {
                    }).forEach(res::header);

                    req.cookies().values().forEach(it -> it.forEach(res::addCookie));
                    res.status(matchingResponse.getCode());

                    return Mono.delay(Duration.ofMillis(matchingResponse.getMillisDelay()))
                            .then(body == null ? res.send() : res.sendString(Mono.just(body), UTF_8).then());
                });
    }

    private Mono<Request> extractRequest(InternalExchange exchange, HttpServerRequest req) {
        return req.receive().aggregate().asString(UTF_8)
                .map(body -> mapToRequest(exchange, req, body))
                .switchIfEmpty(Mono.just(mapToRequest(exchange, req, null)));
    }

    private Request mapToRequest(InternalExchange exchange, HttpServerRequest req, String body) {
        return new Request(
                mapper,
                body,
                req.cookies(),
                req.params(),
                exchange.extractPathVariables(req),
                req.requestHeaders(),
                req.fullPath(),
                req.scheme(),
                req.method().name()
        );
    }

    private String replace(String text, String placeholder, Object replacement) {
        if (StringUtil.isNullOrEmpty(text)) {
            return text;
        }
        String fullValueReplacement = SerializationUtils.writeAsString(mapper, replacement);
        return text.replace('"' + placeholder + '"', fullValueReplacement)
                .replace(placeholder, String.valueOf(replacement));
    }

    public static StubBuilder builder(String name, int port, Collection<Exchange> exchanges) {
        return new StubBuilder(name, port, exchanges);
    }

    public static final class StubBuilder {

        private final String name;
        private final int port;
        private final Collection<Exchange> exchanges;
        private ObjectMapper mapper;
        private Function<HttpServer, HttpServer> mutation = Function.identity();

        private StubBuilder(String name, int port, Collection<Exchange> exchanges) {
            this.name = name;
            this.port = port;
            this.exchanges = exchanges;
        }

        public StubBuilder mapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public StubBuilder mutation(Function<HttpServer, HttpServer> mutation) {
            this.mutation = mutation;
            return this;
        }

        public Stub build() {
            if (mapper == null) {
                mapper = new ObjectMapper();
            }
            return new Stub(name, exchanges, mapper, port, mutation);
        }
    }

}
