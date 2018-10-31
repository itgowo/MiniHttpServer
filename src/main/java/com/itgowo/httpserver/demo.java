package com.itgowo.httpserver;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Map;


public class demo {
    private static final String rootDir = "/Users/lujianchao/GitDemo/RemoteDataController/RemoteDataControllerServer/web";

    public static void main(String[] args) {
        MiniHttpServer httpNioServer = new MiniHttpServer();
        httpNioServer.init(false, new InetSocketAddress(12111), rootDir, new onHttpListener() {
            @Override
            public void onHandler(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
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
                    httpResponse.sendFile(httpNioServer.getFileManager().getFile(httpRequest.getUri()), true);
                }


                //POST请求，常见的Body传Json文本或者表单上传文件
                if (HttpMethod.POST == httpRequest.getMethod()) {
                    if (httpRequest.isMultipart_formdata()) {
                        Map<String, File> fileMap = httpRequest.getFileList();
                    } else {
                        String requestBody = httpRequest.getBody();
                    }
                    String jsonStr = "{\"name\":\"小王\",\"age\":33}";
                    httpResponse.setData(jsonStr).sendData(HttpStatus.OK);
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
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        httpNioServer.startServer();


    }

}