package com.itgowo.httpserver;


import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright (c) 2018.
 *  @author lujianchao
 *  MiniHttpServer
 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 */

public class HttpHander implements Runnable {
    /**
     * 大部分浏览器和web容器默认header限制都是8k
     */
    public static final int HTTP_BUFFER = 8 * 1024;
    public static final int MAX_HEADER_SIZE = 1024;
    public static final int BODY_BUFFER_SIZE = 8 * 1024;

    private static final String CHARSET_REGEX = "[ |\t]*(charset)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;]*)['|\"]?";
    private static final Pattern CHARSET_PATTERN = Pattern.compile(CHARSET_REGEX, Pattern.CASE_INSENSITIVE);
    private static final String BOUNDARY_REGEX = "[ |\t]*(boundary)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;]*)['|\"]?";
    private static final Pattern BOUNDARY_PATTERN = Pattern.compile(BOUNDARY_REGEX, Pattern.CASE_INSENSITIVE);
    private static final String CONTENT_DISPOSITION_REGEX = "([ |\t]*Content-Disposition[ |\t]*:)(.*)";
    private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile(CONTENT_DISPOSITION_REGEX, Pattern.CASE_INSENSITIVE);
    private static final String CONTENT_DISPOSITION_ATTRIBUTE_REGEX = "[ |\t]*([a-zA-Z]*)[ |\t]*=[ |\t]*['|\"]([^\"^']*)['|\"]";
    private static final Pattern CONTENT_DISPOSITION_ATTRIBUTE_PATTERN = Pattern.compile(CONTENT_DISPOSITION_ATTRIBUTE_REGEX);
    private static final String CONTENT_TYPE_REGEX = "([ |\t]*Content-Type[ |\t]*:)(.*)";
    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile(CONTENT_TYPE_REGEX, Pattern.CASE_INSENSITIVE);

    private boolean isClosed = false;
    private SocketChannel socketChannel;
    private FileManager fileManager;
    private HttpRequest httpRequestHander;
    private HttpResponse httpResponse;
    private onHttpListener httpListener;
    private String clientId;

    public HttpHander(SocketChannel socketChannel, String clientId, FileManager fileManager, onHttpListener httpListener) {
        this.socketChannel = socketChannel;
        this.httpListener = httpListener;
        this.clientId = clientId;
        this.fileManager = fileManager;
        try {
            parseHttp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始解析
     *
     * @throws IOException
     */
    private void parseHttp() throws IOException {
        if (!socketChannel.isOpen()) {
            return;
        }
        httpRequestHander = new HttpRequest(socketChannel, clientId);
        httpResponse = new HttpResponse(httpRequestHander);
        ByteBuffer byteBuffer = ByteBuffer.allocate(HTTP_BUFFER);
        int endPosition = 0;
        while (!isClosed && socketChannel.isOpen()) {
            int result = socketChannel.read(byteBuffer);
            if (result == -1) {
                socketChannel.close();
                isClosed = true;
                break;
            }
//            System.out.println(new String(byteBuffer.array(), 0, result));
            if (result == 0) {
                try {
                    Thread.sleep(50);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            endPosition = indexOfHeaderEndPoint(byteBuffer);
            if (endPosition > 0) {
                break;
            }
        }

        if (!isClosed) {
            byteBuffer.flip();
            byte[] bytes = new byte[endPosition];
            byteBuffer.get(bytes, 0, endPosition);
            parseHttpHeader(new String(bytes, "utf-8"), httpRequestHander, byteBuffer.limit() - endPosition);
            InetAddress inetAddress = ((InetSocketAddress) socketChannel.getRemoteAddress()).getAddress();
            httpRequestHander.setRemoteIp(inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress() ? "127.0.0.1" : inetAddress.getHostAddress().toString());
            byteBuffer.position(endPosition);//读位置移动到body开始位置
            parseBody(httpRequestHander, byteBuffer);
        }
    }

    /**
     * 解析Body
     *
     * @param httpRequest
     * @param remaining
     * @throws IOException
     */
    private void parseBody(HttpRequest httpRequest, ByteBuffer remaining) throws IOException {
        HttpMethod method = httpRequest.getMethod();
        RandomAccessFile randomAccessFile = null;
        if (HttpMethod.PUT.equals(method) || HttpMethod.POST.equals(method)) {
            long size = httpRequest.getContentLength();
            ByteArrayOutputStream baos = null;
            DataOutput request_data_output = null;
            if (size < HTTP_BUFFER) {
                baos = new ByteArrayOutputStream();
                request_data_output = new DataOutputStream(baos);
            } else {
                randomAccessFile = fileManager.getTempRandomAccessFile();
                request_data_output = randomAccessFile;
            }

            ByteBuffer buf = ByteBuffer.allocate((int) Math.min(size, BODY_BUFFER_SIZE));
            int length = remaining.remaining();
            if (length > 0) {
                buf.put(remaining);
                size -= buf.position();
                request_data_output.write(buf.array(), 0, length);
            }
            while (length >= 0 && size > 0) {
                buf = ByteBuffer.allocate((int) Math.min(size, BODY_BUFFER_SIZE));
                length = socketChannel.read(buf);
                size -= length;
                if (length > 0) {
                    request_data_output.write(buf.array(), 0, length);
                }
            }

            ByteBuffer fbuf = null;
            if (baos != null) {
                fbuf = ByteBuffer.wrap(baos.toByteArray(), 0, baos.size());
            } else {
                fbuf = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());
                randomAccessFile.seek(0);
            }
            if (HttpMethod.POST.equals(httpRequest.getMethod())) {
                String contentType = "";
                String contentTypeHeader = httpRequest.getHeaders().get(HttpHeaderNames.CONTENT_TYPE);
                StringTokenizer st = null;
                if (contentTypeHeader != null) {
                    st = new StringTokenizer(contentTypeHeader, ",; ");
                    if (st.hasMoreTokens()) {
                        contentType = st.nextToken();
                    }
                }

                if (HttpHeaderValues.MULTIPART_FORM_DATA.equalsIgnoreCase(contentType)) {
                    if (st.hasMoreTokens()) {
                        parseMultipartFormData(httpRequest, getAttributeFromContentHeader(contentTypeHeader, BOUNDARY_PATTERN, null),
                                getAttributeFromContentHeader(contentTypeHeader, CHARSET_PATTERN, "UTF-8"), fbuf);
                        httpRequest.setMultipart_formdata(true);
                    }
                } else {
                    byte[] postBytes = new byte[fbuf.remaining()];
                    fbuf.get(postBytes);
                    String postLine = new String(postBytes).trim();
                    if (HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.equalsIgnoreCase(contentType)) {
                        httpRequest.getParms().putAll(parseParms(postLine));
                    } else if (postLine.length() != 0) {
                        httpRequest.setBody(postLine).setMultipart_formdata(false);
                    }
                }

            } else if (HttpMethod.PUT.equals(httpRequest.getMethod())) {
                httpRequest.addToFileList("content", saveFile(fbuf, 0, fbuf.limit(), null));
            }
        }
    }

    /**
     * 二进制查找 boundary
     *
     * @param b        如果文件特别大，最好用内存映射的MappedByteBuffer 减少内存oom
     * @param boundary 文件分割信息
     * @return 返回所有找到的位置数组
     */
    private int[] getBoundaryPositions(ByteBuffer b, byte[] boundary) {
        int[] res = new int[0];
        if (b.remaining() < boundary.length) {
            return res;
        }
        int search_window_pos = 0;
        byte[] search_window = new byte[4 * 1024 + boundary.length];

        int first_fill = (b.remaining() < search_window.length) ? b.remaining() : search_window.length;
        b.get(search_window, 0, first_fill);
        int new_bytes = first_fill - boundary.length;
        do {
            for (int j = 0; j < new_bytes; j++) {
                for (int i = 0; i < boundary.length; i++) {
                    if (search_window[j + i] != boundary[i])
                        break;
                    if (i == boundary.length - 1) {
                        int[] new_res = new int[res.length + 1];
                        System.arraycopy(res, 0, new_res, 0, res.length);
                        new_res[res.length] = search_window_pos + j;
                        res = new_res;
                    }
                }
            }
            search_window_pos += new_bytes;
            System.arraycopy(search_window, search_window.length - boundary.length, search_window, 0, boundary.length);
            new_bytes = search_window.length - boundary.length;
            new_bytes = (b.remaining() < new_bytes) ? b.remaining() : new_bytes;
            b.get(search_window, boundary.length, new_bytes);
        } while (new_bytes > 0);
        return res;
    }

    /**
     * 解析表单数据
     *
     * @param httpRequest
     * @param boundary    分割符
     * @param encoding    编码信息
     * @param byteBuffer  如果文件特别大，最好用内存映射的MappedByteBuffer 减少内存oom
     * @throws IOException
     */
    private void parseMultipartFormData(HttpRequest httpRequest, String boundary, String encoding, ByteBuffer byteBuffer) throws IOException {
        int[] boundary_idxs = getBoundaryPositions(byteBuffer, boundary.getBytes());
        if (boundary_idxs.length < 2) {
            onServerBadRequest("BAD REQUEST: Content type is multipart/form-data but contains less than two boundary strings.");
            httpListener.onError(new Exception("BAD REQUEST: Content type is multipart/form-data but contains less than two boundary strings."));
        }
        byte[] part_header_buff = new byte[MAX_HEADER_SIZE];
        for (int bi = 0; bi < boundary_idxs.length - 1; bi++) {
            byteBuffer.position(boundary_idxs[bi]);
            int len = (byteBuffer.remaining() < MAX_HEADER_SIZE) ? byteBuffer.remaining() : MAX_HEADER_SIZE;
            byteBuffer.get(part_header_buff, 0, len);
            BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(part_header_buff, 0, len), Charset.forName(encoding)), len);
            int contentLength = 2;
            int headerLines = 0;
            String mpline = in.readLine();
            headerLines++;
            if (!mpline.contains(boundary)) {
                onServerBadRequest("BAD REQUEST: Content type is multipart/form-data but chunk does not start with boundary.");
                httpListener.onError(new Exception("BAD REQUEST: Content type is multipart/form-data but chunk does not start with boundary."));
            }

            String part_name = null, file_name = null, content_type = null;
            mpline = in.readLine();
            headerLines++;
            while (mpline != null && mpline.trim().length() > 0) {
                Matcher matcher = CONTENT_DISPOSITION_PATTERN.matcher(mpline);
                if (matcher.matches()) {
                    contentLength += mpline.length() + 2;
                    String attributeString = matcher.group(2);
                    matcher = CONTENT_DISPOSITION_ATTRIBUTE_PATTERN.matcher(attributeString);
                    while (matcher.find()) {
                        String key = matcher.group(1);
                        if (key.equalsIgnoreCase("name")) {
                            part_name = matcher.group(2);
                        } else if (key.equalsIgnoreCase("filename")) {
                            file_name = matcher.group(2);
                        }
                    }
                }
                matcher = CONTENT_TYPE_PATTERN.matcher(mpline);
                if (matcher.matches()) {
                    contentLength += mpline.length() + 2;
                    content_type = matcher.group(2).trim();
                }
                mpline = in.readLine();
                headerLines++;
            }
            int part_header_len = 0;
            while (headerLines-- > 0) {
                while (part_header_buff[part_header_len] != '\n') {
                    part_header_len++;
                }
            }
            part_header_len++;
            if (part_header_len >= len - 4) {
                onServerBadRequest("BAD REQUEST:Multipart header size exceeds MAX_HEADER_SIZE.");
                httpListener.onError(new Exception("BAD REQUEST:Multipart header size exceeds MAX_HEADER_SIZE."));
            }
            int part_data_start = boundary_idxs[bi] + part_header_len + contentLength;
            int part_data_end = boundary_idxs[bi + 1] - 4;

            byteBuffer.position(part_data_start);

            File file = saveFile(byteBuffer, part_data_start, part_data_end - part_data_start, file_name);
            if (!httpRequest.containsFile(part_name)) {
                httpRequest.addToFileList(part_name, file);
            } else {
                int count = 2;
                while (httpRequest.containsFile(part_name + count)) {
                    count++;
                }
                httpRequest.addToFileList(part_name + count, file);
            }
            httpRequest.getParms().put(part_name, file_name);
        }
    }

    private File saveFile(ByteBuffer b, int offset, int len, String filename_hint) {
        if (len > 0) {
            FileOutputStream fileOutputStream = null;
            try {
                File tempFile = fileManager.createFile(filename_hint);
                ByteBuffer src = b.duplicate();
                fileOutputStream = new FileOutputStream(tempFile);
                FileChannel dest = fileOutputStream.getChannel();
                src.position(offset).limit(offset + len);
                dest.write(src.slice());
                return tempFile;
            } catch (Exception e) {
                onServerInternal(e.getLocalizedMessage());
                httpListener.onError(e);
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 正则获取数据
     *
     * @param contentTypeHeader
     * @param pattern
     * @param defaultValue
     * @return
     */
    private String getAttributeFromContentHeader(String contentTypeHeader, Pattern pattern, String defaultValue) {
        Matcher matcher = pattern.matcher(contentTypeHeader);
        return matcher.find() ? matcher.group(2) : defaultValue;
    }

    /**
     * 解析http header
     *
     * @param header
     * @param httpRequest
     * @param remaining
     * @throws IOException
     */
    private void parseHttpHeader(String header, HttpRequest httpRequest, int remaining) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(header));
        String inLine = reader.readLine();
        if (inLine == null) {
            return;
        }
        StringTokenizer requestLine = new StringTokenizer(inLine);
        if (requestLine.countTokens() < 2) {
            onServerBadRequest("BAD REQUEST:parse http error:" + inLine);
            httpListener.onError(new Throwable("BAD REQUEST:parse http error:" + inLine));
        }
        httpRequest.setMethod(HttpMethod.find(requestLine.nextToken()));

        String uri = requestLine.nextToken();
        int qmi = uri.indexOf('?');
        if (qmi >= 0) {
            httpRequest.setQueryParameterString(uri.substring(qmi + 1));
            httpRequest.setParms(parseParms(httpRequest.getQueryParameterString()));
            uri = decodeURLString(uri.substring(0, qmi));
        } else {
            uri = decodeURLString(uri);
        }
        httpRequest.setUri(uri);

        if (requestLine.hasMoreTokens()) {
            httpRequest.setProtocolVersion(requestLine.nextToken());
        } else {
            httpRequest.setProtocolVersion("HTTP/1.1");
        }

        httpRequest.setHeaders(parseHeaders(reader));

        long contentLength = 0;
        if (httpRequest.getHeaders().containsKey(HttpHeaderNames.CONTENT_LENGTH)) {
            try {
                contentLength = Long.parseLong(httpRequest.getHeaders().get(HttpHeaderNames.CONTENT_LENGTH));
            } catch (Exception e) {
                if (remaining > 0) {
                    contentLength = remaining;
                }
            }
        }
        httpRequest.setContentLength(contentLength);
    }

    /**
     * 解析header参数
     *
     * @param reader
     * @return
     * @throws IOException
     */
    private Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> map = new HashMap<>();
        String line = reader.readLine();
        while (line != null && line.trim().length() > 0) {
            int p = line.indexOf(':');
            if (p >= 0) {
                map.put(line.substring(0, p).trim().toLowerCase(Locale.US), line.substring(p + 1).trim());
            }
            line = reader.readLine();
        }
        return map;
    }

    /**
     * 解析uri中的参数或者表单传的参数
     *
     * @param uriParms
     * @return
     */
    private Map<String, String> parseParms(String uriParms) {
        Map<String, String> map = new HashMap<>();
        if (uriParms == null) {
            return map;
        }
        StringTokenizer parms = new StringTokenizer(uriParms, "&");
        while (parms.hasMoreTokens()) {
            String e = parms.nextToken();
            int sep = e.indexOf('=');
            if (sep >= 0) {
                map.put(e.substring(0, sep).trim(), e.substring(sep + 1));
            } else {
                map.put(e.trim(), "");
            }
        }
        return map;
    }

    /**
     * URL decode
     *
     * @param str
     * @return
     */
    protected static String decodeURLString(String str) {
        String decoded = "";
        try {
            decoded = URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException ignored) {
            ignored.printStackTrace();
        }
        return decoded;
    }

    /**
     * 查找header结尾点,如果符合RFC2616标准，找\r\n\r\n  ，否则找\n\n
     *
     * @param buf
     * @return
     */
    private int indexOfHeaderEndPoint(ByteBuffer buf) {
        int splitbyte = 0;
        while (splitbyte + 1 < buf.limit()) {
            // RFC2616
            if (buf.array()[splitbyte] == '\r' && buf.array()[splitbyte + 1] == '\n' && splitbyte + 3 < buf.limit() && buf.array()[splitbyte + 2] == '\r' && buf.array()[splitbyte + 3] == '\n')
                return splitbyte + 4;
            // tolerance
            if (buf.array()[splitbyte] == '\n' && buf.array()[splitbyte + 1] == '\n') return splitbyte + 2;
            splitbyte++;
        }
        return 0;
    }

    @Override
    public void run() {
        if (httpRequestHander == null) {
            return;
        }
        try {
            httpListener.onHandler(httpRequestHander, httpResponse);
        } catch (Exception e) {
            httpListener.onError(e);
        }
    }

    public void onServerInternal(String msg) {
        try {
            httpResponse.setData(ByteBuffer.wrap(msg.getBytes("utf-8")));
            httpResponse.sendData(HttpStatus.INTERNAL_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onServerBadRequest(String msg) throws IOException {
        httpResponse.setData(ByteBuffer.wrap(msg.getBytes("utf-8")));
        httpResponse.sendData(HttpStatus.BAD_REQUEST);
    }
}
