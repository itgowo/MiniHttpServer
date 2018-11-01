package com.itgowo.httpserver;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;


/*
 * Copyright (c) 2018.
 *  @author lujianchao
 *  MiniHttpServer
 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 */

public class HttpRequest {
    private SocketChannel socketChannel;
    private String clientId;
    private HttpMethod method;
    private String uri;
    private String protocolVersion;
    private String queryParameterString;
    private Map<String, String> parms;
    private Map<String, String> headers;
    private String remoteIp;
    private long contentLength;
    private boolean multipart_formdata;
    private String body;
    private Map<String, File> fileList;

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public HttpRequest(SocketChannel socketChannel, String clientId) {
        this.socketChannel = socketChannel;
        this.clientId = clientId;
    }

    public boolean isMultipart_formdata() {
        return multipart_formdata;
    }

    public boolean isApplicationJson() {
        String json = headers.get(HttpHeaderNames.CONTENT_TYPE);
        if (json != null && HttpHeaderValues.APPLICATION_JSON.equalsIgnoreCase(json)) {
            return true;
        }
        return false;
    }

    public String getClientId() {
        return clientId;
    }

    public HttpRequest setMultipart_formdata(boolean multipart_formdata) {
        this.multipart_formdata = multipart_formdata;
        return this;
    }

    public String getBody() {
        return body;
    }

    public HttpRequest setBody(String body) {
        this.body = body;
        multipart_formdata = false;
        return this;
    }

    public Map<String, File> getFileList() {
        return fileList;
    }

    public boolean containsFile(String key) {
        if (fileList == null) return false;
        return fileList.containsKey(key);
    }

    public boolean containsHeader(String key) {
        if (headers == null) return false;
        return headers.containsKey(key);
    }

    protected HttpRequest addToFileList(String key, File file) {
        if (fileList == null) {
            multipart_formdata = true;
            fileList = new HashMap<>();
            fileList.put(key, file);
        }
        return this;
    }

    protected HttpRequest addFileList(Map<String, File> fileList) {
        if (this.fileList == null) {
            this.fileList = new HashMap<>();
        }
        this.fileList.putAll(fileList);
        multipart_formdata = true;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public HttpRequest setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public boolean isGzip() {
        String acceptEncoding = this.headers.get("accept-encoding");
        return acceptEncoding != null && acceptEncoding.contains("gzip");
    }

    public long getContentLength() {
        return contentLength;
    }

    public HttpRequest setContentLength(long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public boolean isKeepAlive() {
        if (headers == null) {
            return false;
        }
        String connection = headers.get("connection");
        boolean keepAlive = protocolVersion.equals("HTTP/1.1") && (connection == null || !connection.matches("(?i).*close.*"));
        return keepAlive;
    }

    public HttpRequest setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public Map<String, String> getParms() {
        if (parms == null) {
            parms = new HashMap<>();
        }
        return parms;
    }

    public HttpRequest setParms(Map<String, String> parms) {
        this.parms = parms;
        return this;
    }

    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        return headers;
    }

    public HttpRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public String getQueryParameterString() {
        return queryParameterString;
    }

    public HttpRequest setQueryParameterString(String queryParameterString) {
        this.queryParameterString = queryParameterString;
        return this;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public HttpRequest setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
        return this;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HttpRequest setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
        return this;
    }

    /**
     * 向客户端发送消息，最原始方式，http协议格式请用HttpResponse
     */
    @Deprecated
    public void sendData(ByteBuffer byteBuffer) throws IOException {
        socketChannel.write(byteBuffer);
        if (!isKeepAlive()) {
            socketChannel.close();
        }
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpRequest{");
        sb.append("socketChannel=").append(socketChannel);
        sb.append(", uri='").append(uri).append('\'');
        sb.append(", method=").append(method);
        sb.append(", parms=").append(parms);
        sb.append(", headers=").append(headers);
        sb.append(", queryParameterString='").append(queryParameterString).append('\'');
        sb.append(", remoteIp='").append(remoteIp).append('\'');
        sb.append(", protocolVersion='").append(protocolVersion).append('\'');
        sb.append(", contentLength=").append(contentLength);
        sb.append(", multipart_formdata=").append(multipart_formdata);
        sb.append(", body='").append(body).append('\'');
        sb.append(", fileList=").append(fileList);
        sb.append('}');
        return sb.toString();
    }
}
