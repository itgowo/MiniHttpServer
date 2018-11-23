package com.itgowo.httpserver;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;


public class demo {
    private static final String rootDir = "/Users/lujianchao/itgowo/MiniHttpServer/web";

    public static void main(String[] args) {
        MiniHttpServer miniHttpServer = new MiniHttpServer();
        miniHttpServer.setFileLimit(1024 * 1024 * 500, 1000 * 60 * 60 * 24 * 6);
        miniHttpServer.init(false, new InetSocketAddress(12111), rootDir, new onSimpleHttpListener() {
            @Override
            public void onHandler(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
                System.out.println(httpRequest);

                //headers操作
                Map<String, String> headers = httpRequest.getHeaders();
                boolean hasContentType = httpRequest.containsHeader(HttpHeaderNames.CONTENT_TYPE);
                String contentType = httpRequest.getHeaders().get(HttpHeaderNames.CONTENT_TYPE);


                //参数操作，来源一:url中"?"后面解析出来的参数键值对;来源二:POST表单参数解析
                Map<String, String> parms = httpRequest.getParms();
                String userId = httpRequest.getParms().get("userId");


                //GET请求资源
                if (HttpMethod.GET == httpRequest.getMethod()) {
                    if (httpRequest.getUri().equalsIgnoreCase("/")) {
                        httpRequest.setUri("/index.html");
                    }
                    //缓存策略，浏览器指定时间内只获取一次文件,如果sendFile()包含cacheControl参数，则不需要在设置，设置了以单独设置为准。没有cacheControl的方法则默认没有此参数
//                    httpResponse.addHeader(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.MAX_AGE + "=3600");

//                    httpResponse.sendFile(miniHttpServer.getFileManager().getFile(httpRequest.getUri()));
//                    httpResponse.sendFile(miniHttpServer.getFileManager().getFile(httpRequest.getUri()), true);
                    //cacheControl参数添加了不一定起作用，如果单独加了header，则此方法参数无效
                    httpResponse.sendFile(miniHttpServer.getFileManager().getFile(httpRequest.getUri()), HttpStatus.OK, 3600, true);
                }


                //POST请求，常见的Body传Json文本或者表单上传文件
                if (HttpMethod.POST == httpRequest.getMethod()) {
                    if (httpRequest.isMultipart_formdata()) {
                        Map<String, File> fileMap = httpRequest.getFileList();
                        httpResponse.setData(Arrays.toString(fileMap.values().toArray())).sendData(HttpStatus.OK);
                    } else {
                        String requestBody = httpRequest.getBody();
                        httpResponse.setData(requestBody).sendData(HttpStatus.OK);
                    }
                }


                //OPTIONS请求，跨域请求最多的是ajax发出的，应对web请求
                if (HttpMethod.OPTIONS == httpRequest.getMethod()) {
                    httpResponse.sendOptionsResult();
                }


                //PUT请求，跟POST表单上传文件不同，Http中Body默认为一个文件，临时存在temp目录，PUT如果需要传递文件名请在headers中添加自定义数据。
                if (HttpMethod.PUT == httpRequest.getMethod()) {
                    Map<String, File> fileMap = httpRequest.getFileList();
                    httpResponse.sendData(HttpStatus.OK);
                }
                //HEAD、DELETE、TRACE、CONNECT、PATCH 等不常见，需要自己返回结果即可，无特殊需求


                //重定向
//                httpResponse.sendRedirect("http://www.baidu.com");


                if (HttpMethod.POST == httpRequest.getMethod()) {
                    if (httpRequest.isMultipart_formdata()) {
                        //处理文件
                    } else {
                        String requestBody = httpRequest.getBody();
                        httpResponse.setData(requestBody).sendData(HttpStatus.OK);
                    }
                }

                //例，触发清理文件功能
                if (httpRequest.getUri().startsWith("/cleanOldFile")) {
                    miniHttpServer.getFileManager().cleanOldFile();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        miniHttpServer.startServer();

    }

}