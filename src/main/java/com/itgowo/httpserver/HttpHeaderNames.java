/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.itgowo.httpserver;


/**
 * Standard HTTP header names.
 * <p>
 * These are all defined as lowercase to support HTTP/2 requirements while also not
 * violating HTTP/1.x requirements.  New header names should always be lowercase.
 */
public final class HttpHeaderNames {
    /**
     * {@code "accept"}
     */
    public static final String ACCEPT = String.valueOf("accept");
    /**
     * {@code "accept-charset"}
     */
    public static final String ACCEPT_CHARSET = String.valueOf("accept-charset");
    /**
     * {@code "accept-encoding"}
     */
    public static final String ACCEPT_ENCODING = String.valueOf("accept-encoding");
    /**
     * {@code "accept-language"}
     */
    public static final String ACCEPT_LANGUAGE = String.valueOf("accept-language");
    /**
     * {@code "accept-ranges"}
     */
    public static final String ACCEPT_RANGES = String.valueOf("accept-ranges");
    /**
     * {@code "accept-patch"}
     */
    public static final String ACCEPT_PATCH = String.valueOf("accept-patch");
    /**
     * {@code "access-control-allow-credentials"}
     */
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS =
            String.valueOf("access-control-allow-credentials");
    /**
     * {@code "access-control-allow-headers"}
     */
    public static final String ACCESS_CONTROL_ALLOW_HEADERS =
            String.valueOf("access-control-allow-headers");
    /**
     * {@code "access-control-allow-methods"}
     */
    public static final String ACCESS_CONTROL_ALLOW_METHODS =
            String.valueOf("access-control-allow-methods");
    /**
     * {@code "access-control-allow-origin"}
     */
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN =
            String.valueOf("access-control-allow-origin");
    /**
     * {@code "access-control-expose-headers"}
     */
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS =
            String.valueOf("access-control-expose-headers");
    /**
     * {@code "access-control-max-age"}
     */
    public static final String ACCESS_CONTROL_MAX_AGE = String.valueOf("access-control-max-age");
    /**
     * {@code "access-control-request-headers"}
     */
    public static final String ACCESS_CONTROL_REQUEST_HEADERS =
            String.valueOf("access-control-request-headers");
    /**
     * {@code "access-control-request-method"}
     */
    public static final String ACCESS_CONTROL_REQUEST_METHOD =
            String.valueOf("access-control-request-method");
    /**
     * {@code "age"}
     */
    public static final String AGE = String.valueOf("age");
    /**
     * {@code "allow"}
     */
    public static final String ALLOW = String.valueOf("allow");
    /**
     * {@code "authorization"}
     */
    public static final String AUTHORIZATION = String.valueOf("authorization");
    /**
     * {@code "cache-control"}
     */
    public static final String CACHE_CONTROL = String.valueOf("cache-control");
    /**
     * {@code "connection"}
     */
    public static final String CONNECTION = String.valueOf("connection");
    /**
     * {@code "content-base"}
     */
    public static final String CONTENT_BASE = String.valueOf("content-base");
    /**
     * {@code "content-encoding"}
     */
    public static final String CONTENT_ENCODING = String.valueOf("content-encoding");
    /**
     * {@code "content-language"}
     */
    public static final String CONTENT_LANGUAGE = String.valueOf("content-language");
    /**
     * {@code "content-length"}
     */
    public static final String CONTENT_LENGTH = String.valueOf("content-length");
    /**
     * {@code "content-location"}
     */
    public static final String CONTENT_LOCATION = String.valueOf("content-location");
    /**
     * {@code "content-transfer-encoding"}
     */
    public static final String CONTENT_TRANSFER_ENCODING = String.valueOf("content-transfer-encoding");
    /**
     * {@code "content-disposition"}
     */
    public static final String CONTENT_DISPOSITION = String.valueOf("content-disposition");
    /**
     * {@code "content-md5"}
     */
    public static final String CONTENT_MD5 = String.valueOf("content-md5");
    /**
     * {@code "content-range"}
     */
    public static final String CONTENT_RANGE = String.valueOf("content-range");
    /**
     * {@code "content-security-policy"}
     */
    public static final String CONTENT_SECURITY_POLICY = String.valueOf("content-security-policy");
    /**
     * {@code "content-type"}
     */
    public static final String CONTENT_TYPE = String.valueOf("content-type");
    /**
     * {@code "cookie"}
     */
    public static final String COOKIE = String.valueOf("cookie");
    /**
     * {@code "date"}
     */
    public static final String DATE = String.valueOf("date");
    /**
     * {@code "etag"}
     */
    public static final String ETAG = String.valueOf("etag");
    /**
     * {@code "expect"}
     */
    public static final String EXPECT = String.valueOf("expect");
    /**
     * {@code "expires"}
     */
    public static final String EXPIRES = String.valueOf("expires");
    /**
     * {@code "from"}
     */
    public static final String FROM = String.valueOf("from");
    /**
     * {@code "host"}
     */
    public static final String HOST = String.valueOf("host");
    /**
     * {@code "if-match"}
     */
    public static final String IF_MATCH = String.valueOf("if-match");
    /**
     * {@code "if-modified-since"}
     */
    public static final String IF_MODIFIED_SINCE = String.valueOf("if-modified-since");
    /**
     * {@code "if-none-match"}
     */
    public static final String IF_NONE_MATCH = String.valueOf("if-none-match");
    /**
     * {@code "if-range"}
     */
    public static final String IF_RANGE = String.valueOf("if-range");
    /**
     * {@code "if-unmodified-since"}
     */
    public static final String IF_UNMODIFIED_SINCE = String.valueOf("if-unmodified-since");
    /**
     * @deprecated use {@link #CONNECTION}
     * <p>
     * {@code "keep-alive"}
     */
    @Deprecated
    public static final String KEEP_ALIVE = String.valueOf("keep-alive");
    /**
     * {@code "last-modified"}
     */
    public static final String LAST_MODIFIED = String.valueOf("last-modified");
    /**
     * {@code "location"}
     */
    public static final String LOCATION = String.valueOf("location");
    /**
     * {@code "max-forwards"}
     */
    public static final String MAX_FORWARDS = String.valueOf("max-forwards");
    /**
     * {@code "origin"}
     */
    public static final String ORIGIN = String.valueOf("origin");
    /**
     * {@code "pragma"}
     */
    public static final String PRAGMA = String.valueOf("pragma");
    /**
     * {@code "proxy-authenticate"}
     */
    public static final String PROXY_AUTHENTICATE = String.valueOf("proxy-authenticate");
    /**
     * {@code "proxy-authorization"}
     */
    public static final String PROXY_AUTHORIZATION = String.valueOf("proxy-authorization");
    /**
     * @deprecated use {@link #CONNECTION}
     * <p>
     * {@code "proxy-connection"}
     */
    @Deprecated
    public static final String PROXY_CONNECTION = String.valueOf("proxy-connection");
    /**
     * {@code "range"}
     */
    public static final String RANGE = String.valueOf("range");
    /**
     * {@code "referer"}
     */
    public static final String REFERER = String.valueOf("referer");
    /**
     * {@code "retry-after"}
     */
    public static final String RETRY_AFTER = String.valueOf("retry-after");
    /**
     * {@code "sec-websocket-key1"}
     */
    public static final String SEC_WEBSOCKET_KEY1 = String.valueOf("sec-websocket-key1");
    /**
     * {@code "sec-websocket-key2"}
     */
    public static final String SEC_WEBSOCKET_KEY2 = String.valueOf("sec-websocket-key2");
    /**
     * {@code "sec-websocket-location"}
     */
    public static final String SEC_WEBSOCKET_LOCATION = String.valueOf("sec-websocket-location");
    /**
     * {@code "sec-websocket-origin"}
     */
    public static final String SEC_WEBSOCKET_ORIGIN = String.valueOf("sec-websocket-origin");
    /**
     * {@code "sec-websocket-protocol"}
     */
    public static final String SEC_WEBSOCKET_PROTOCOL = String.valueOf("sec-websocket-protocol");
    /**
     * {@code "sec-websocket-version"}
     */
    public static final String SEC_WEBSOCKET_VERSION = String.valueOf("sec-websocket-version");
    /**
     * {@code "sec-websocket-key"}
     */
    public static final String SEC_WEBSOCKET_KEY = String.valueOf("sec-websocket-key");
    /**
     * {@code "sec-websocket-accept"}
     */
    public static final String SEC_WEBSOCKET_ACCEPT = String.valueOf("sec-websocket-accept");
    /**
     * {@code "sec-websocket-protocol"}
     */
    public static final String SEC_WEBSOCKET_EXTENSIONS = String.valueOf("sec-websocket-extensions");
    /**
     * {@code "server"}
     */
    public static final String SERVER = String.valueOf("server");
    /**
     * {@code "set-cookie"}
     */
    public static final String SET_COOKIE = String.valueOf("set-cookie");
    /**
     * {@code "set-cookie2"}
     */
    public static final String SET_COOKIE2 = String.valueOf("set-cookie2");
    /**
     * {@code "te"}
     */
    public static final String TE = String.valueOf("te");
    /**
     * {@code "trailer"}
     */
    public static final String TRAILER = String.valueOf("trailer");
    /**
     * {@code "transfer-encoding"}
     */
    public static final String TRANSFER_ENCODING = String.valueOf("transfer-encoding");
    /**
     * {@code "upgrade"}
     */
    public static final String UPGRADE = String.valueOf("upgrade");
    /**
     * {@code "user-agent"}
     */
    public static final String USER_AGENT = String.valueOf("user-agent");
    /**
     * {@code "vary"}
     */
    public static final String VARY = String.valueOf("vary");
    /**
     * {@code "via"}
     */
    public static final String VIA = String.valueOf("via");
    /**
     * {@code "warning"}
     */
    public static final String WARNING = String.valueOf("warning");
    /**
     * {@code "websocket-location"}
     */
    public static final String WEBSOCKET_LOCATION = String.valueOf("websocket-location");
    /**
     * {@code "websocket-origin"}
     */
    public static final String WEBSOCKET_ORIGIN = String.valueOf("websocket-origin");
    /**
     * {@code "websocket-protocol"}
     */
    public static final String WEBSOCKET_PROTOCOL = String.valueOf("websocket-protocol");
    /**
     * {@code "www-authenticate"}
     */
    public static final String WWW_AUTHENTICATE = String.valueOf("www-authenticate");
    /**
     * {@code "x-frame-options"}
     */
    public static final String X_FRAME_OPTIONS = String.valueOf("x-frame-options");

    private HttpHeaderNames() {
    }
}
