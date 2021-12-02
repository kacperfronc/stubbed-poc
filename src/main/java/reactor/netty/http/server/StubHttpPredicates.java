package reactor.netty.http.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StubHttpPredicates {

    private final Map<String, HttpPredicate> predicates;

    public StubHttpPredicates() {
        this.predicates = new ConcurrentHashMap<>();
    }

    public boolean test(String uri, HttpServerRequest request) {
        return getPredicate(uri, request).test(request);
    }

    public Map<String, String> apply(String uri, HttpServerRequest request) {
        return getPredicate(uri, request).apply(request.fullPath());
    }

    private HttpPredicate getPredicate(String uri, HttpServerRequest request) {
        return predicates.computeIfAbsent(request.method().name() + ":" + uri, k -> new HttpPredicate(uri, null, request.method()));
    }

}
