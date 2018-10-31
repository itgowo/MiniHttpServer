package com.itgowo.httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * Copyright (c) 2018.
 *  @author lujianchao
 *  MiniHttpServer
 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 */

public class HttpResponse {
    public static final String JSON = "application/json;charset=utf-8";
    public static final String HTML = "text/html";
    public static final String JS = "application/javascript";
    public static final String CSS = "text/css";
    public static final String OBJECT = "application/octet-stream";

    private SocketChannel socketChannel;
    private HttpRequest httpHander;

    private HttpStatus status;
    private String mimeType;
    private ByteBuffer data;
    private final Map<String, String> header = new HashMap();
    private HttpMethod requestMethod;
    private boolean keepAlive;

    protected HttpResponse(HttpRequest httpHander, HttpStatus status, String mimeType, ByteBuffer byteBuffer) {
        this.httpHander = httpHander;
        this.socketChannel = httpHander.getSocketChannel();
        this.status = status;
        this.mimeType = mimeType;
        this.data = byteBuffer;
        keepAlive = httpHander.isKeepAlive();
    }

    public HttpResponse(HttpRequest httpHander, String mimeType) {
        this(httpHander, HttpStatus.OK, mimeType, null);
    }

    public HttpResponse(HttpRequest httpHander) {
        this(httpHander, null);
    }

    public void addHeader(String name, String value) {
        this.header.put(name, value);
    }

    public String getHeader(String name) {
        for (String headerName : header.keySet()) {
            if (headerName.equalsIgnoreCase(name)) {
                return header.get(headerName);
            }
        }
        return null;
    }

    public void setKeepAlive(boolean useKeepAlive) {
        this.keepAlive = useKeepAlive;
    }

    public void sendData(HttpStatus status) throws IOException {
        if (data == null) {
            data = ByteBuffer.allocate(0);
        }
        SimpleDateFormat gmtFrmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        if (this.mimeType != null) {
            header.put(HttpHeaderNames.CONTENT_TYPE, this.mimeType);
        }

        if (this.header.get("Date") == null) {
            header.put(HttpHeaderNames.DATE, gmtFrmt.format(new Date()));
        }

        if (httpHander.containsHeader(HttpHeaderNames.CONNECTION)) {
            header.put(HttpHeaderNames.CONNECTION, this.keepAlive ? HttpHeaderValues.KEEP_ALIVE : HttpHeaderValues.CLOSE);
        }

        header.put(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(data.limit()));
        StringBuilder builder = new StringBuilder("HTTP/1.1 ");
        builder.append(status.getStatus()).append("\r\n");
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
            }
        }
        builder.append("\r\n");
        ByteBuffer header = ByteBuffer.wrap(builder.toString().getBytes("utf-8"));
        socketChannel.write(new ByteBuffer[]{header, data});
        if (!keepAlive) {
            socketChannel.close();
        }
    }

    public void sendOptionsResult() throws IOException {
        header.put(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "*");
        header.put(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        header.put(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        header.put(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, "Origin, 3600");
        sendData(HttpStatus.OK);
    }

    public void sendRedirect(String newUrl) throws IOException {
        header.put(HttpHeaderNames.LOCATION, newUrl);
        sendData(HttpStatus.REDIRECT);
    }

    public String getDefaultMimeType(File file) {
        if (file == null) {
            return JSON;
        } else if (file.getName().toLowerCase().endsWith(".html")) {
            return HTML;
        } else if (file.getName().toLowerCase().endsWith(".htm")) {
            return HTML;
        } else if (file.getName().toLowerCase().endsWith(".js")) {
            return JS;
        } else if (file.getName().toLowerCase().endsWith(".css")) {
            return CSS;
        } else {
            return OBJECT;
        }
    }

    public void sendFile(File file, boolean autoHtmltoNotAttachment) throws IOException {
        try {
            if (file == null || !file.exists()) {
                sendData(HttpStatus.NOT_FOUND);
            } else {
                boolean isAttachment = true;
                if (autoHtmltoNotAttachment) {
                    isAttachment = !(file.getName().endsWith(".html") || file.getName().endsWith(".htm"));
                }
                header.put(HttpHeaderNames.CONTENT_DISPOSITION, isAttachment ? "attachment; " : "" + "filename = " + file.getName());
                FileInputStream fileInputStream = new FileInputStream(file);
                FileChannel dest = fileInputStream.getChannel();
                MappedByteBuffer mappedByteBuffer = dest.map(FileChannel.MapMode.READ_ONLY, 0, dest.size());
                this.data = mappedByteBuffer;
                sendData(HttpStatus.OK);
            }
        } catch (Exception e) {
            try {
                sendData(HttpStatus.NOT_FOUND);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw e;
        }
    }

    public HttpResponse setData(ByteBuffer data) {
        this.data = data;
        return this;
    }

    public HttpResponse setData(String data) {
        try {
            this.data = ByteBuffer.wrap(data.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } return this;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public HttpResponse setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public String getMimeType() {
        return mimeType;
    }

    public HttpResponse setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public ByteBuffer getData() {
        return data;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public HttpMethod getRequestMethod() {
        return requestMethod;
    }

    public HttpResponse setRequestMethod(HttpMethod requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }
}
