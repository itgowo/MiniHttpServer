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
    private HttpRequest httpRequest;

    private HttpStatus status;
    private String mimeType;
    private ByteBuffer data;
    private final Map<String, String> header = new HashMap();
    private boolean keepAlive;

    protected HttpResponse(HttpRequest httpRequest, HttpStatus status, String mimeType, ByteBuffer byteBuffer) {
        this.httpRequest = httpRequest;
        this.socketChannel = httpRequest.getSocketChannel();
        this.status = status;
        this.mimeType = mimeType;
        this.data = byteBuffer;
        keepAlive = httpRequest.isKeepAlive();
    }

    public HttpResponse(HttpRequest httpRequest, String mimeType) {
        this(httpRequest, HttpStatus.OK, mimeType, null);
    }

    public HttpResponse(HttpRequest httpRequest) {
        this(httpRequest, null);
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

        if (httpRequest.containsHeader(HttpHeaderNames.CONNECTION)) {
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
        while (data.hasRemaining()) {
            socketChannel.write(data);
        }
        if (!keepAlive && socketChannel.isOpen()) {
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

    public void sendFile(File file) throws IOException {
        sendFile(file, HttpStatus.OK, 0, true);
    }

    public void sendFile(File file, boolean autoHtmltoNotAttachment) throws IOException {
        sendFile(file, HttpStatus.OK, 0, autoHtmltoNotAttachment);
    }

    /**
     * 向客户端发送文件
     *
     * @param file                    待发送文件
     * @param httpStatus
     * @param cacheControl            单位秒，缓存策略，如果设置合适时间，规定时间内浏览器不会再请求文件，可以降低服务器压力
     * @param autoHtmltoNotAttachment 为true，html类型文件不会携带附件参数，浏览器可以直接打开显示，包含附件参数则为下载。
     * @throws IOException
     */
    public void sendFile(File file, HttpStatus httpStatus, int cacheControl, boolean autoHtmltoNotAttachment) throws IOException {
        try {
            if (file == null || !file.exists()) {
                sendData(HttpStatus.NOT_FOUND);
            } else {
                if (cacheControl > 0 && !header.containsKey(HttpHeaderNames.CACHE_CONTROL)) {
                    header.put(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.MAX_AGE + "=" + cacheControl);
                }
                if (!header.containsKey(HttpHeaderNames.CONTENT_TYPE)) {
                    header.put(HttpHeaderNames.CONTENT_TYPE, getDefaultMimeType(file));
                }

                boolean isAttachment = true;
                if (autoHtmltoNotAttachment) {
                    isAttachment = !(file.getName().endsWith(".html") || file.getName().endsWith(".htm"));
                }
                header.put(HttpHeaderNames.CONTENT_DISPOSITION, isAttachment ? "attachment; " : "" + "filename = " + file.getName());
                FileInputStream fileInputStream = new FileInputStream(file);
                FileChannel dest = fileInputStream.getChannel();
                MappedByteBuffer mappedByteBuffer = dest.map(FileChannel.MapMode.READ_ONLY, 0, dest.size());
                this.data = mappedByteBuffer;
                sendData(httpStatus);
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
        }
        return this;
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

    public boolean isKeepAlive() {
        return keepAlive;
    }
}
