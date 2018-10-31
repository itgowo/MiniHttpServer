package com.itgowo.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;


public class test {
    private static final String rootDir = "/Users/lujianchao/GitDemo/RemoteDataController/RemoteDataControllerServer/web";

    public static void main(String[] args) {


        MiniHttpServer httpNioServer = new MiniHttpServer();
        try {
            httpNioServer.init(false, new InetSocketAddress(12111), rootDir, new onHttpListener() {
                @Override
                public void onHandler(HttpRequest httpHander, HttpResponse httpResponse) throws Exception {
                    if (HttpMethod.GET == httpHander.getMethod()) {
                        if (httpHander.getUri().equalsIgnoreCase("/")) {
                            httpHander.setUri("/index.html");
                        }
                        httpResponse.sendFile(httpNioServer.getFileManager().getFile(httpHander.getUri()), true);
                    }else {
                        httpResponse.setData("sdfasfaaaa").sendRedirect("http://www.baidu.com");
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
            httpNioServer.startServer();
            Thread thread = new Thread(httpNioServer);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}