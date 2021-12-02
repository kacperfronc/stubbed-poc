package com.example.stubbed;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StubTest {

    private static final Logger log = LoggerFactory.getLogger(StubTest.class);

    private Stub stub;

    @BeforeEach
    void setUp() {

        Exchange exchange = Exchange.builder("jeden")
                .path("/testpath")
                .placeholdersReplacers(ImmutableMap.of(
                        "${testval}", req -> 42,
                        "${coo}", req -> ImmutableList.of("hue", "hua"),
                        "${test-valu}", req -> "some-value",
                        "${doe}", req -> ImmutableMap.of(
                                "test-field", "test-value",
                                "second-test-field", "second-test-value"
                        )))
                .build();

        Exchange exchange2 = Exchange.builder("dwa")
                .path("/testpath-he/{joe}/mojemoje")
                .placeholdersReplacers(ImmutableMap.of(
                        "${testval}", req -> 42,
                        "${coo}", req -> ImmutableList.of("hue", "hua"),
                        "${test-valu}", req -> "some-value",
                        "${doe}", req -> ImmutableMap.of(
                                "test-field", "test-value",
                                "second-test-field", "second-test-value"
                        )))
                .build();

        Exchange exchange3 = Exchange.builder("try")
                .path("/testpath-het")
                .placeholdersReplacers(ImmutableMap.of(
                        "${testval}", req -> 42,
                        "${coo}", req -> ImmutableList.of("hue", "hua"),
                        "${test-valu}", req -> "some-value",
                        "${doe}", req -> ImmutableMap.of(
                                "test-field", "test-value",
                                "second-test-field", "second-test-value"
                        )))
                .build();

        this.stub = Stub.builder("stubik", 6666, ImmutableList.of(exchange, exchange2, exchange3))
                .build()
                .start();
    }

    @AfterEach
    void tearDown() {
        stub.stop();
    }

    @Test
    void first() {
        HttpResponse<String> response = Unirest.get("http://localhost:6666/testpath").asString();
        log.info("------- response -------");
        log.info(response.getBody());
        log.info(response.getHeaders().toString());
    }

    @Test
    void second() {
        HttpResponse<String> response = Unirest.get("http://localhost:6666/testpath-het").asString();
        log.info("------- response -------");
        log.info(response.getBody());
        log.info(response.getHeaders().toString());
    }

    @Test
    void third() {
        HttpResponse<String> response = Unirest.post("http://localhost:6666/testpath-he/trawakolacja/mojemoje")
                .body(new TestClass("bb"))
                .asString();
        log.info("------- response -------");
        log.info(response.getBody());
        log.info(response.getHeaders().toString());
    }
}