package com.itgowo.httpserver;

/*
 * Copyright (c) 2018.
 *  @author lujianchao
 *  MiniHttpServer
 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 */

public enum HttpMethod {
    GET,
    PUT,
    POST,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT,
    PATCH;

    static HttpMethod find(String method) {
        for (HttpMethod m : HttpMethod.values()) {
            if (m.toString().equalsIgnoreCase(method)) {
                return m;
            }
        }
        return null;
    }
}
