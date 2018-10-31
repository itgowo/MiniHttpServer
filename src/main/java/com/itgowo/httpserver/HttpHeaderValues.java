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
 * Standard HTTP header values.
 */
public final class HttpHeaderValues {
    /**
     * {@code "application/json"}
     */
    public static final String APPLICATION_JSON = String.valueOf("application/json");
    /**
     * {@code "application/x-www-form-urlencoded"}
     */
    public static final String APPLICATION_X_WWW_FORM_URLENCODED =
            String.valueOf("application/x-www-form-urlencoded");
    /**
     * {@code "application/octet-stream"}
     */
    public static final String APPLICATION_OCTET_STREAM = String.valueOf("application/octet-stream");
    /**
     * {@code "attachment"}
     * See {@link HttpHeaderNames#CONTENT_DISPOSITION}
     */
    public static final String ATTACHMENT = String.valueOf("attachment");
    /**
     * {@code "base64"}
     */
    public static final String BASE64 = String.valueOf("base64");
    /**
     * {@code "binary"}
     */
    public static final String BINARY = String.valueOf("binary");
    /**
     * {@code "boundary"}
     */
    public static final String BOUNDARY = String.valueOf("boundary");
    /**
     * {@code "bytes"}
     */
    public static final String BYTES = String.valueOf("bytes");
    /**
     * {@code "charset"}
     */
    public static final String CHARSET = String.valueOf("charset");
    /**
     * {@code "chunked"}
     */
    public static final String CHUNKED = String.valueOf("chunked");
    /**
     * {@code "close"}
     */
    public static final String CLOSE = String.valueOf("close");
    /**
     * {@code "compress"}
     */
    public static final String COMPRESS = String.valueOf("compress");
    /**
     * {@code "100-continue"}
     */
    public static final String CONTINUE = String.valueOf("100-continue");
    /**
     * {@code "deflate"}
     */
    public static final String DEFLATE = String.valueOf("deflate");
    /**
     * {@code "x-deflate"}
     */
    public static final String X_DEFLATE = String.valueOf("x-deflate");
    /**
     * {@code "file"}
     * See {@link HttpHeaderNames#CONTENT_DISPOSITION}
     */
    public static final String FILE = String.valueOf("file");
    /**
     * {@code "filename"}
     * See {@link HttpHeaderNames#CONTENT_DISPOSITION}
     */
    public static final String FILENAME = String.valueOf("filename");
    /**
     * {@code "form-data"}
     * See {@link HttpHeaderNames#CONTENT_DISPOSITION}
     */
    public static final String FORM_DATA = String.valueOf("form-data");
    /**
     * {@code "gzip"}
     */
    public static final String GZIP = String.valueOf("gzip");
    /**
     * {@code "gzip,deflate"}
     */
    public static final String GZIP_DEFLATE = String.valueOf("gzip,deflate");
    /**
     * {@code "x-gzip"}
     */
    public static final String X_GZIP = String.valueOf("x-gzip");
    /**
     * {@code "identity"}
     */
    public static final String IDENTITY = String.valueOf("identity");
    /**
     * {@code "keep-alive"}
     */
    public static final String KEEP_ALIVE = String.valueOf("keep-alive");
    /**
     * {@code "max-age"}
     */
    public static final String MAX_AGE = String.valueOf("max-age");
    /**
     * {@code "max-stale"}
     */
    public static final String MAX_STALE = String.valueOf("max-stale");
    /**
     * {@code "min-fresh"}
     */
    public static final String MIN_FRESH = String.valueOf("min-fresh");
    /**
     * {@code "multipart/form-data"}
     */
    public static final String MULTIPART_FORM_DATA = String.valueOf("multipart/form-data");
    /**
     * {@code "multipart/mixed"}
     */
    public static final String MULTIPART_MIXED = String.valueOf("multipart/mixed");
    /**
     * {@code "must-revalidate"}
     */
    public static final String MUST_REVALIDATE = String.valueOf("must-revalidate");
    /**
     * {@code "name"}
     * See {@link HttpHeaderNames#CONTENT_DISPOSITION}
     */
    public static final String NAME = String.valueOf("name");
    /**
     * {@code "no-cache"}
     */
    public static final String NO_CACHE = String.valueOf("no-cache");
    /**
     * {@code "no-store"}
     */
    public static final String NO_STORE = String.valueOf("no-store");
    /**
     * {@code "no-transform"}
     */
    public static final String NO_TRANSFORM = String.valueOf("no-transform");
    /**
     * {@code "none"}
     */
    public static final String NONE = String.valueOf("none");
    /**
     * {@code "0"}
     */
    public static final String ZERO = String.valueOf("0");
    /**
     * {@code "only-if-cached"}
     */
    public static final String ONLY_IF_CACHED = String.valueOf("only-if-cached");
    /**
     * {@code "private"}
     */
    public static final String PRIVATE = String.valueOf("private");
    /**
     * {@code "proxy-revalidate"}
     */
    public static final String PROXY_REVALIDATE = String.valueOf("proxy-revalidate");
    /**
     * {@code "public"}
     */
    public static final String PUBLIC = String.valueOf("public");
    /**
     * {@code "quoted-printable"}
     */
    public static final String QUOTED_PRINTABLE = String.valueOf("quoted-printable");
    /**
     * {@code "s-maxage"}
     */
    public static final String S_MAXAGE = String.valueOf("s-maxage");
    /**
     * {@code "text/plain"}
     */
    public static final String TEXT_PLAIN = String.valueOf("text/plain");
    /**
     * {@code "trailers"}
     */
    public static final String TRAILERS = String.valueOf("trailers");
    /**
     * {@code "upgrade"}
     */
    public static final String UPGRADE = String.valueOf("upgrade");
    /**
     * {@code "websocket"}
     */
    public static final String WEBSOCKET = String.valueOf("websocket");

    private HttpHeaderValues() {
    }
}
