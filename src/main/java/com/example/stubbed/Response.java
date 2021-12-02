package com.example.stubbed;

import java.util.Collections;
import java.util.Map;

public final class Response {

    private String id = "";
    private int code = 200;
    private Map<CharSequence, CharSequence> headers = Collections.emptyMap();
    private long millisDelay = 0;
    private Object body;

    public Response() {
    }

    public Response(String id, int code, Map<CharSequence, CharSequence> headers, long millisDelay, Object body) {
        this.id = id;
        this.code = code;
        this.headers = headers;
        this.millisDelay = millisDelay;
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<CharSequence, CharSequence> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<CharSequence, CharSequence> headers) {
        this.headers = headers;
    }

    public long getMillisDelay() {
        return millisDelay;
    }

    public void setMillisDelay(long millisDelay) {
        this.millisDelay = millisDelay;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id='" + id + '\'' +
                ", code=" + code +
                ", headers=" + headers +
                ", millisDelay=" + millisDelay +
                ", body=" + body +
                '}';
    }
}
